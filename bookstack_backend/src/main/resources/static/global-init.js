// 1. Define the UI function in the GLOBAL scope first
function showGlobalToast(sale) {
    console.log("Rendering toast for:", sale.bookTitle);
    const usd = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' });
    const container = document.getElementById('toastContainer');

    if (!container) return;

    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.innerHTML = `
        <div class="toast-title">📦 Book sold!</div>
        <div class="toast-body">
            <strong>${sale.bookTitle}</strong> &times; ${sale.quantity}<br>
            Address: ${sale.deliveryAddress}
        </div>`;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'toastSlideOut 0.3s ease forwards';
        toast.addEventListener('animationend', () => toast.remove());
    }, 6000);
}

// 2. Then handle the logic
document.addEventListener('DOMContentLoaded', async () => {
    console.log("Global init script loaded");

    if (!document.getElementById('toastContainer')) {
        const container = document.createElement('div');
        container.id = 'toastContainer';
        document.body.appendChild(container);
    }

    const NOTIF_KEY = 'saleNotificationsEnabled';
    const enabled = localStorage.getItem(NOTIF_KEY);

    // We can use your stored ID or fetch it
    const response = await fetch('/api/me/', { credentials: 'include' });
    if (response.ok) {
        const user = await response.json();

        if (enabled === 'true' && typeof Notifications !== 'undefined') {
            console.log("Connecting to WS for user:", user.id);

            Notifications.connect(user.id, (sale) => {
                console.log("SALE RECEIVED:", sale);
                // Now this will be defined!
                showGlobalToast(sale);
            });
        }
    }
});