const { Client } = require('@stomp/stompjs');
const WebSocket = require('websocket').w3cwebsocket;

Object.assign(global, { WebSocket });

const client = new Client({
    brokerURL: 'wss://bookstack-page.loca.lt/ws-native',
    // brokerURL: 'ws://localhost:9091/ws-native',
    reconnectDelay: 0,
    onConnect: () => {
        console.log('Connected!');
        client.subscribe('/topic/seller.1', (message) => {
            console.log('Sale received:');
            console.log(JSON.parse(message.body));
        });
        console.log('Subscribed to /topic/seller.1 — waiting for messages...');
    },
    onStompError: (frame) => {
        console.error('STOMP error:', frame);
    },
    onDisconnect: () => {
        console.log('Disconnected.');
    }
});

client.activate();