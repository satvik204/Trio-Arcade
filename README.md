# TRIO ARCADE — 3-player GitHub Pages party website

Three games: **Impostor**, **Word Bomb**, and **Drawing Disaster**. Private room codes, anonymous Firebase authentication, host-controlled rounds, mobile drawing, and a shared leaderboard.

## Deploy (about 10–15 minutes)

1. Create a Firebase project at https://console.firebase.google.com/.
2. In **Build → Authentication → Sign-in method**, enable **Anonymous**.
3. In **Build → Realtime Database**, create a database. Open **Rules**, replace them with the contents of `database.rules.json`, and click **Publish**. **Do not use test-mode rules**.
4. In **Project settings → Your apps**, register a Web app. Copy its configuration values into `firebase-config.js` (especially `databaseURL`). Firebase web API keys are public configuration, not admin secrets.
5. Create a GitHub repository, upload `index.html`, `style.css`, `app.js`, `firebase-config.js`, `database.rules.json`, and `README.md` to the repository root.
6. In GitHub **Settings → Pages**, set **Deploy from a branch**, branch `main`, folder `/ (root)`; save. Wait for the Pages URL to appear.
7. Open the URL in three separate browsers/devices. One friend creates a room; the others enter the six-character code or use the invite link.

## How the games work

- **Impostor:** Two players receive one word and one receives a related word. Everyone submits a clue and votes. Two or more correct votes give detectives 10 points each; otherwise the impostor receives 10.
- **Word Bomb:** Three lives each. Submit a word containing the displayed letters before 18 seconds pass. A missed turn costs one life. Last survivor gets 15 points.
- **Drawing Disaster:** Everyone writes a prompt, draws someone else's prompt, and guesses another person's drawing. Reveal all three chains. This is a creative game without automatic scoring.

## Notes and limitations

- **Exactly three players** are required to start a game. The host can select a different game after each round.
- The game needs a Firebase Realtime Database connection; **GitHub Pages alone cannot synchronize multiplayer**.
- This is a **playable prototype**, not a hardened competitive game server. Client clocks, word validity, scoring and turn transitions are not authoritative. A modified browser client can cheat. Word Bomb checks spelling *format* and required letters, not dictionary membership. It works best as a friendly party game.
- Drawing data is stored in Firebase until the room is deleted; avoid sharing personal or sensitive information. Drawings are compressed to JPEG to reduce database usage.
- Host must remain connected for timeout handling and progression. If the host leaves, the room closes.
- Rooms do not auto-expire. The host should click **Leave room** when done. Firebase usage may incur charges above free-tier limits.
- Secret words are stored separately in `trioSecrets` and readable only by their assigned user. The host's impostor assignment is stored in `trioHostSecrets`. The host could still inspect it, so use fair play.
- Rules should be tested in Firebase's Rules Playground before public launch. This prototype has not been end-to-end tested against a live Firebase project.