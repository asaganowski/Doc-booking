#!/bin/sh
cat > /usr/share/nginx/html/assets/env.js << EOF
(function(window) {
  window.__env = window.__env || {};
  window.__env.apiUrl = '${API_URL:-http://localhost:8080}';
}(this));
EOF
exec nginx -g "daemon off;"
