#!/bin/sh
mkdir -p /usr/share/nginx/html/assets
# Inject API URL
cat > /usr/share/nginx/html/assets/env.js << ENVEOF
(function(window) {
  window.__env = window.__env || {};
  window.__env.apiUrl = '${API_URL:-http://localhost:8080}';
}(this));
ENVEOF

# Substitute $PORT in nginx config
envsubst '$PORT' < /etc/nginx/conf.d/default.conf > /tmp/default.conf
cp /tmp/default.conf /etc/nginx/conf.d/default.conf

exec nginx -g "daemon off;"
