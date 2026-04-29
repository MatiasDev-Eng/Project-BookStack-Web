const { Client } = require('@stomp/stompjs');
const WebSocket = require('websocket').w3cwebsocket;

Object.assign(global, { WebSocket });

const client = new Client({
    brokerURL: 'wss://antivirus-giant-duress.ngrok-free.dev/ws-native',
    // brokerURL: 'ws://localhost:9091/ws-native',
    reconnectDelay: 0,
    onConnect: () => {
        console.log('Connected!');
        client.subscribe('/topic/seller.2', (message) => {
            console.log('Sale received:');
            console.log(JSON.parse(message.body));
        });
        console.log('Subscribed to /topic/seller.5 — waiting for messages...');
    },
    onStompError: (frame) => {
        console.error('STOMP error:', frame);
    },
    onDisconnect: () => {
        console.log('Disconnected.');
    }
});

client.activate();