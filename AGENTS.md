# ChefMarket — Base44 Dev Environment

## What this is
A Hebrew (RTL) "private chef" app with recipe recommendations, meal builder, grocery cart, and cooking timer. The repo contains two surfaces:
- **Web UI** (`app/index.html`) — a single static HTML page (Tailwind via CDN, Google Fonts, Material Symbols) served by `app/server.js`. This is what the Base44 preview shows.
- **Android app** (`app/src/`, `app/build.gradle.kts`) — Kotlin + Jetpack Compose, Firebase AI (Gemini). Cannot run in the preview; the web UI mirrors it.

## Running the preview
```bash
docker compose -f docker-compose.base44.yml up -d
```
- `server.js` uses only Node.js built-in modules (`http`, `fs`, `path`) — no `npm install` needed.
- Serves `app/index.html` on port 3000. The HTML is read fresh on every request, so edits to `index.html` appear on browser refresh.
- Edits to `server.js` require a container restart: `docker compose -f docker-compose.base44.yml restart web`.
- Health check: `GET /health` → `{"status":"healthy"}`.
- `/download-apk` returns 404 text when no APK is built (expected in preview).

## No external credentials required
The web preview is fully static and needs no API keys. The Android app uses Firebase/Gemini but that is not part of the preview.
