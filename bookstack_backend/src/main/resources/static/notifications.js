// Requires SockJS and STOMP
// <script src="https://cdn.jsdelivr.net/npm/sockjs-client/dist/sockjs.min.js"></script>
// <script src="https://cdn.jsdelivr.net/npm/stompjs/lib/stomp.min.js"></script>

const Notifications = (() => {
    let stompClient = null;
    let subscribed = false;

    function connect(sellerId, onMessage) {
        if (stompClient && stompClient.connected) return;

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.debug = null; // silence console noise

        stompClient.connect({}, () => {
            stompClient.subscribe(`/topic/seller.${sellerId}`, (frame) => {
                const sale = JSON.parse(frame.body);
                onMessage(sale);
            });
            subscribed = true;
        });
    }

    function disconnect() {
        if (stompClient) stompClient.disconnect();
        subscribed = false;
    }

    function isSubscribed() { return subscribed; }

    return { connect, disconnect, isSubscribed };
})();