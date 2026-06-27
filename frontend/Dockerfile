FROM node:22-alpine AS build
WORKDIR /app
ARG API_URL=http://localhost:8080
ENV API_URL=$API_URL
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:22-alpine
RUN npm install -g serve
WORKDIR /app
COPY --from=build /app/dist/frontend/browser ./
CMD sh -c "serve -s . -l $PORT"
