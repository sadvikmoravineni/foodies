export default function OrdersPanel({ orders }) {
  return (
    <section className="panel">
      <div className="panel-header">
        <h2>Orders</h2>
      </div>
      {orders.length === 0 ? <p className="empty">No orders yet.</p> : null}
      {orders.map((order) => (
        <div key={order.id} className="order-item">
          <div className="cart-main">
            <strong>#{order.id.slice(-6).toUpperCase()}</strong>
            <span>${Number(order.total).toFixed(2)}</span>
          </div>
          <p>{new Date(order.createdAt).toLocaleString()} | {order.status}</p>
        </div>
      ))}
    </section>
  );
}
