const { Client } = require('@stomp/stompjs');
const WebSocket = require('websocket').w3cwebsocket;

Object.assign(global, { WebSocket });

const client = new Client({
    brokerURL: 'wss://antivirus-giant-duress.ngrok-free.dev/ws-native',
    reconnectDelay: 0,
    onConnect: () => {
        console.log('Connected!');

        // Define the topic in one place
        const topic = '/topic/seller.8';

        client.subscribe(topic, (message) => {
            console.log(`Sale received on ${topic}:`);
            console.log(JSON.parse(message.body));
        });

        console.log(`Subscribed to ${topic} — waiting for messages...`);
    },
    onStompError: (frame) => {
        console.error('STOMP error:', frame);
    },
    onDisconnect: () => {
        console.log('Disconnected.');
    }
});

client.activate();