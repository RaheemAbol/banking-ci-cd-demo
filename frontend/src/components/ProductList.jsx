export default function ProductList({ products, user, busy, onAdd, onEdit, onDelete }) {
  return <section className="catalog">
    <div className="section-heading"><h2>The collection</h2><span>{products.length} products</span></div>
    <div className="product-grid">
      {products.map(product => <article className="product" key={product.id}>
        <div className="product-category">{product.category}</div>
        <h3>{product.name}</h3><p>{product.vendor.name}</p>
        <strong className="price">${Number(product.price).toFixed(2)}</strong>
        <small>Product #{product.id}</small>
        <button disabled={busy || !user} onClick={() => onAdd(product.id)}>
          {user ? 'Add to cart' : 'Log in to shop'}
        </button>
        {user?.role === 'ADMIN' && <div className="actions">
          <button className="secondary" disabled={busy} onClick={() => onEdit(product)}>Edit</button>
          <button className="danger" disabled={busy} onClick={() => onDelete(product.id)}>Delete</button>
        </div>}
      </article>)}
    </div>
  </section>;
}
