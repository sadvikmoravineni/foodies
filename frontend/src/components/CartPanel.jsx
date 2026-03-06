export default function CartPanel({ cart, onUpdateQty, onRemove }) {
  const items = cart?.items || [];
  const total = Number(cart?.total || 0).toFixed(2);

  return (
    <section className="panel">
      <div className="panel-header">
        <h2>Cart</h2>
        <span>{items.length} items</span>
      </div>

      {items.length === 0 ? <p className="empty">Your cart is empty.</p> : null}

      {items.map((item) => (
        <div key={item.productId} className="cart-item">
          <div className="cart-main">
            <strong>{item.productName}</strong>
            <span>${Number(item.lineTotal).toFixed(2)}</span>
          </div>
          <div className="cart-actions">
            <input
              type="number"
              min="1"
              defaultValue={item.quantity}
              onBlur={(e) => onUpdateQty(item.productId, Number(e.target.value || 1))}
            />
            <button className="ghost" onClick={() => onRemove(item.productId)}>Remove</button>
          </div>
        </div>
      ))}

      <div className="checkout-row">
        <strong>Total</strong>
        <strong>${total}</strong>
      </div>
    </section>
  );
}
