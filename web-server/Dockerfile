FROM node:14

WORKDIR /usr/node

COPY package*.json ./

RUN npm install

COPY . .

CMD ["npm", "start"]
