export default function CartPanel({ items, onQuantity, onRemove, onClear, onCheckout, busy }) {
  const total = items.reduce((sum, item) => sum + Number(item.product.price) * item.quantity, 0);

  return <section className="panel cart-panel">
    <div className="cart-heading">
      <h2>Your cart</h2>
      <button className="text-button" disabled={busy || !items.length} onClick={onClear}>
        Clear cart
      </button>
    </div>
    {items.length === 0 && <p>Your cart is empty.</p>}
    {items.map(item => <div className="cart-line" key={item.id}>
      <strong>{item.product.name}</strong>
      <span>${Number(item.product.price).toFixed(2)} each</span>
      <div className="actions">
        <label>Quantity
          <select value={item.quantity} disabled={busy}
            aria-label={`Quantity for ${item.product.name}`}
            onChange={event => onQuantity(item.id, Number(event.target.value))}>
            {Array.from({ length: 20 }, (_, index) => index + 1).map(n =>
              <option key={n} value={n}>{n}</option>)}
          </select>
        </label>
        <button className="text-button" disabled={busy} onClick={() => onRemove(item.id)}>
          Remove
        </button>
      </div>
      <strong className="cart-subtotal">
        Subtotal: ${(Number(item.product.price) * item.quantity).toFixed(2)}
      </strong>
    </div>)}
    <div className="total"><span>Estimated total</span><strong>${total.toFixed(2)}</strong></div>
    <button disabled={busy || !items.length} onClick={onCheckout}>Demo checkout</button>
    <small>No payment, order history, or stock reservation. The server recalculates the total.</small>
  </section>;
}
