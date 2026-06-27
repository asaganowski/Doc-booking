const fs = require('fs');
const apiUrl = process.env.API_URL || 'http://localhost:8080';
const content = `(function(window){window.__env=window.__env||{};window.__env.apiUrl='${apiUrl}';}(this));\n`;
fs.writeFileSync('src/assets/env.js', content);
console.log('env.js generated with apiUrl:', apiUrl);
