export default function ProductForm({ product, vendors, onSave, onCancel, busy }) {
  async function submit(event) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    await onSave(product?.id, {
      name: data.get('name'), category: data.get('category'),
      price: Number(data.get('price')), vendor: { id: Number(data.get('vendorId')) }
    });
  }
  return <section className="panel admin-panel">
    <h2>{product ? `Edit product #${product.id}` : 'Add a product'}</h2>
    <p>Administrator controls</p>
    <form key={product?.id ?? 'new'} onSubmit={submit}>
      <label>Product name<input name="name" defaultValue={product?.name ?? ''} required maxLength={100} /></label>
      <label>Category<input name="category" defaultValue={product?.category ?? ''} required maxLength={40} /></label>
      <label>Price<input name="price" type="number" min="0" max="99999999.99" step="0.01"
        defaultValue={product?.price ?? ''} required /></label>
      <label>Vendor<select name="vendorId" aria-label="Vendor" defaultValue={product?.vendor.id ?? ''} required>
        <option value="" disabled>Choose a vendor</option>
        {vendors.map(vendor => <option key={vendor.id} value={vendor.id}>{vendor.name}</option>)}
      </select></label>
      <button disabled={busy}>Save product</button>
      {product && <button type="button" className="secondary" onClick={onCancel}>Cancel edit</button>}
    </form>
  </section>;
}
