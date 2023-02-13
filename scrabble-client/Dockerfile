FROM node:lts-alpine

COPY . /app

RUN cd /app && npm i && npm run build

# CMD cd /app && npm run dev
CMD cd /app && npm run start

EXPOSE 8080