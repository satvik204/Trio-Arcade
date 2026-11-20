package com.example.subwaypilot;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.hardware.HardwareBuffer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;
public final class PilotService extends AccessibilityService {
 private final Handler h=new Handler(Looper.getMainLooper());
 private boolean inGame=false, busy=false;
 private int lane=1, width=1080, height=2000;
 private long lastSwipe=0;
 private boolean active(){return inGame && getSharedPreferences("pilot",MODE_PRIVATE).getBoolean("running",false);}
 private final Runnable loop=new Runnable(){
   @Override public void run(){
     if(active() && !busy){
       busy=true;
       try{
         takeScreenshot(Display.DEFAULT_DISPLAY,getMainExecutor(),new TakeScreenshotCallback(){
           @Override public void onSuccess(ScreenshotResult sr){
             busy=false;
             HardwareBuffer hb=sr.getHardwareBuffer();
             try{
               Bitmap b=Bitmap.wrapHardwareBuffer(hb,sr.getColorSpace());
               if(b!=null){
                 width=b.getWidth();height=b.getHeight();
                 analyze(b);
                 b.recycle();
               }
             }catch(Exception ignored){} finally{if(hb!=null)hb.close();}
           }
           @Override public void onFailure(int error){busy=false;}
         });
       } catch(Exception ignored){busy=false;}
     }
     h.postDelayed(this,450);
   }
 };
 @Override protected void onServiceConnected(){super.onServiceConnected();h.removeCallbacks(loop);h.postDelayed(loop,500);}
 @Override public void onAccessibilityEvent(AccessibilityEvent e){
   if(e==null || e.getPackageName()==null)return;
   String pkg=e.getPackageName().toString();
   inGame=pkg.equals("com.kiloo.subwaysurf")||pkg.equals("com.sybo.subwaysurfers");
 }
 @Override public void onInterrupt(){inGame=false;}
 @Override public void onDestroy(){h.removeCallbacks(loop);super.onDestroy();}
 private void analyze(Bitmap b){
   if(!active() || SystemClock.uptimeMillis()-lastSwipe<650)return;
   float[] scores=new float[3];
   int step=Math.max(5,b.getWidth()/115);
   for(int l=0;l<3;l++){
     float center=0.27f+0.23f*l;
     int x0=(int)(b.getWidth()*(center-0.065f));
     int x1=(int)(b.getWidth()*(center+0.065f));
     int y0=(int)(b.getHeight()*0.43f);
     int y1=(int)(b.getHeight()*0.68f);
     float dark=0,edges=0,total=0;
     for(int y=y0;y<y1;y+=step)for(int x=x0;x<x1;x+=step){
       int pixel=b.getPixel(x,y);
       int r=Color.red(pixel),g=Color.green(pixel),blue=Color.blue(pixel);
       int near=b.getPixel(Math.min(b.getWidth()-1,x+step),y);
       int diff=Math.abs(r-Color.red(near))+Math.abs(g-Color.green(near))+Math.abs(blue-Color.blue(near));
       if((r+g+blue)/3<72 || (r>105 && r>g*1.5 && r>blue*1.4))dark++;
       if(diff>125)edges++;
       total++;
     }
     scores[l]=(0.6f*dark+0.4f*edges)/Math.max(1,total);
   }
   int best=lane;
   for(int i=0;i<3;i++)if(scores[i]<scores[best])best=i;
   if(best!=lane && scores[lane]>0.22f && scores[lane]-scores[best]>0.07f){
     int move=best>lane?1:-1;
     swipe(move);
     lane+=move;
     lastSwipe=SystemClock.uptimeMillis();
   }
 }
 private void swipe(int direction){
   if(!active())return;
   float x=width*0.5f,y=height*0.72f;
   Path path=new Path();
   path.moveTo(x,y);
   path.lineTo(x+direction*width*0.20f,y);
   GestureDescription.StrokeDescription stroke=new GestureDescription.StrokeDescription(path,0,120);
   dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(),null,null);
 }
}
