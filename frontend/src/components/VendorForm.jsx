export default function VendorForm({ vendor, onSave, onCancel, busy }) {
  async function submit(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const data = new FormData(form);
    const saved = await onSave(vendor?.id, {
      name: data.get('name').trim(),
      email: data.get('email').trim()
    });
    if (saved && !vendor) form.reset();
  }

  return <section className="panel admin-panel">
    <h2>{vendor ? `Edit vendor #${vendor.id}` : 'Add a vendor'}</h2>
    <p>Maintain the companies that supply your products.</p>
    <form key={vendor?.id ?? 'new'} onSubmit={submit}>
      <label>Vendor name
        <input name="name" defaultValue={vendor?.name ?? ''} required maxLength={100} disabled={busy} />
      </label>
      <label>Vendor email
        <input name="email" type="email" defaultValue={vendor?.email ?? ''} required maxLength={150} disabled={busy} />
      </label>
      <button disabled={busy}>Save vendor</button>
      {vendor && <button type="button" className="secondary" onClick={onCancel} disabled={busy}>Cancel edit</button>}
    </form>
  </section>;
}
