export default function AdminOrdersPanel({ orders }) {
  return (
    <section className="panel">
      <div className="panel-header">
        <h2>All Orders</h2>
        <span>{orders.length} total</span>
      </div>
      {orders.length === 0 ? <p className="empty">No orders yet.</p> : null}
      {orders.map((order) => (
        <div key={order.id} className="order-item">
          <div className="cart-main">
            <strong>#{order.id.slice(-6).toUpperCase()}</strong>
            <span>${Number(order.total).toFixed(2)}</span>
          </div>
          <p>User: {order.userId}</p>
          <p>{order.fullName} | {order.phoneNumber}</p>
          <p>{order.addressLine1}{order.addressLine2 ? `, ${order.addressLine2}` : ''}, {order.city}, {order.state} {order.postalCode}, {order.country}</p>
          <p>{new Date(order.createdAt).toLocaleString()} | {order.paymentMethod} | {order.status}</p>
        </div>
      ))}
    </section>
  );
}
