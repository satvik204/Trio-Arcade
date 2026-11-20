package com.example.subwaypilot;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
public class MainActivity extends Activity {
  private TextView status;
  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    LinearLayout root=new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(28,38,28,24);
    root.setBackgroundColor(Color.rgb(15,27,41));
    TextView title=new TextView(this);
    title.setText("SUBWAY PILOT");
    title.setTextSize(26);
    title.setTextColor(Color.WHITE);
    root.addView(title);
    TextView desc=new TextView(this);
    desc.setText("\nExperimental lane avoidance. Works on Android 11+. It may misdetect obstacles and cannot reliably play the game.\n");
    desc.setTextColor(0xffcadbe4);
    desc.setTextSize(16);
    root.addView(desc);
    status=new TextView(this);
    status.setTextColor(0xff79e7bc);
    root.addView(status);
    Button perms=new Button(this); perms.setText("1. Enable Accessibility Service"); root.addView(perms);
    perms.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
    Button start=new Button(this); start.setText("2. Start and open Subway Surfers"); root.addView(start);
    start.setOnClickListener(v->{
      getSharedPreferences("pilot",MODE_PRIVATE).edit().putBoolean("running",true).apply();
      update();
      Intent game=getPackageManager().getLaunchIntentForPackage("com.kiloo.subwaysurf");
      if(game==null)game=getPackageManager().getLaunchIntentForPackage("com.sybo.subwaysurfers");
      if(game!=null)startActivity(game);
      else status.setText("Enabled. Please open Subway Surfers manually.");
    });
    Button stop=new Button(this); stop.setText("STOP swipes"); root.addView(stop);
    stop.setOnClickListener(v->{getSharedPreferences("pilot",MODE_PRIVATE).edit().putBoolean("running",false).apply();update();});
    TextView note=new TextView(this);
    note.setText("\nPrivacy: no network permissions, no stored screen images. Swipes only when Subway Surfers is foreground. Disable the accessibility service when finished.\n\nA prototype, not a tested hands-free bot. For a competition, get agreement before using any automated runs.");
    note.setTextColor(0xffcadbe4);note.setTextSize(14);root.addView(note);
    setContentView(root);update();
  }
  private void update(){status.setText(getSharedPreferences("pilot",MODE_PRIVATE).getBoolean("running",false)?"Pilot enabled — enter game":"Pilot OFF");}
  @Override protected void onResume(){super.onResume(); if(status!=null)update();}
}
