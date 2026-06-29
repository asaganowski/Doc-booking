FROM node:22-alpine AS build
WORKDIR /app
ARG BUILD_CONFIG=production
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration $BUILD_CONFIG

FROM node:22-alpine
RUN npm install -g serve
WORKDIR /app
COPY --from=build /app/dist/frontend/browser ./
CMD sh -c "serve -s . -l $PORT"
