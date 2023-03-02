FROM node:lts-alpine
ENV NEXT_PUBLIC_SERVER_ADDR ""
WORKDIR /app
COPY . .
RUN npm i && npm run build
CMD npm run start

EXPOSE 8081