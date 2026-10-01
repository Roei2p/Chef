const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const APK_PATH = '/app/applet/app/build/outputs/apk/debug/app-debug.apk';

const server = http.createServer((req, res) => {
    // Enable CORS
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

    if (req.method === 'OPTIONS') {
        res.writeHead(204);
        res.end();
        return;
    }

    const url = req.url.split('?')[0];

    // Health check
    if (url === '/health' || url === '/api/health') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ status: 'healthy', app: 'ChefMarket', version: '1.0' }));
        return;
    }

    // Direct APK download
    if (url === '/download-apk' || url === '/ChefMarket.apk' || url.endsWith('.apk')) {
        if (fs.existsSync(APK_PATH)) {
            const stat = fs.statSync(APK_PATH);
            res.writeHead(200, {
                'Content-Type': 'application/vnd.android.package-archive',
                'Content-Length': stat.size,
                'Content-Disposition': 'attachment; filename="ChefMarket.apk"'
            });
            fs.createReadStream(APK_PATH).pipe(res);
            return;
        } else {
            res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
            res.end('קובץ ה-APK עדיין בבנייה, אנא המתן מספר שניות.');
            return;
        }
    }

    // Serve ChefMarket Web UI
    const htmlPath = path.join(__dirname, 'index.html');
    if (fs.existsSync(htmlPath)) {
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
        fs.createReadStream(htmlPath).pipe(res);
    } else {
        res.writeHead(200, { 'Content-Type': 'text/plain; charset=utf-8' });
        res.end('ChefMarket Web Server Active on port 3000');
    }
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`ChefMarket dev server listening on http://0.0.0.0:${PORT}`);
});
