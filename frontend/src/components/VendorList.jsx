export default function VendorList({ vendors, onEdit, onDelete, busy }) {
  return <section className="panel vendor-list">
    <div className="section-heading"><h2>Vendors</h2><span>{vendors.length} vendors</span></div>
    {vendors.length === 0 ? <p>No vendors yet. Add one to make it available in the product editor.</p>
      : <div className="table-scroll"><table>
          <thead><tr><th>Name</th><th>Email</th><th>Actions</th></tr></thead>
          <tbody>{vendors.map(vendor => <tr key={vendor.id}>
            <td><strong>{vendor.name}</strong><small>Vendor #{vendor.id}</small></td>
            <td>{vendor.email}</td>
            <td><div className="vendor-actions">
              <button className="secondary" disabled={busy} onClick={() => onEdit(vendor.id)}
                aria-label={`Edit vendor ${vendor.name}`}>Edit</button>
              <button className="danger" disabled={busy} onClick={() => onDelete(vendor)}
                aria-label={`Delete vendor ${vendor.name}`}>Delete</button>
            </div></td>
          </tr>)}</tbody>
        </table></div>}
  </section>;
}
