FROM node:21 as node
WORKDIR /home/app
COPY package*.json .
RUN npm i
COPY . .
RUN npm run build
# Stage 2
FROM nginx:alpine
WORKDIR /usr/share/nginx/html
COPY --from=node /home/app/dist/frontend/browser .
EXPOSE 4200
