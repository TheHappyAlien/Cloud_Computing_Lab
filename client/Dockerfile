FROM node:18
WORKDIR /client
COPY package.json .
COPY package-lock.json .
RUN npm i
COPY . .
RUN npm run build

EXPOSE 81
CMD ["npm", "run", "preview"]