FROM node:lts-alpine

COPY . /app

RUN npm i -g http-server && cd /app && npm i && npm run build

CMD http-server /app/dist

EXPOSE 8080