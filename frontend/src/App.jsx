import { useEffect, useState } from 'react';
import { request, refreshCsrf } from './api.js';
import { currentUser, register, login, logout } from './auth.js';
import Header from './components/Header.jsx';
import AuthForm from './components/AuthForm.jsx';
import ProductList from './components/ProductList.jsx';
import ProductForm from './components/ProductForm.jsx';
import CartPanel from './components/CartPanel.jsx';
import VendorList from './components/VendorList.jsx';
import VendorForm from './components/VendorForm.jsx';

export default function App() {
  const [user, setUser] = useState(null);
  const [products, setProducts] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [cart, setCart] = useState([]);
  const [editing, setEditing] = useState(null);
  const [editingVendor, setEditingVendor] = useState(null);
  const [view, setView] = useState('shop');
  const [busy, setBusy] = useState(false);
  const [notice, setNotice] = useState(null);
  const isAdmin = user?.role === 'ADMIN';
  const showVendors = isAdmin && view === 'vendors';

  async function reload() {
    const activeUser = await currentUser();
    setUser(activeUser);
    setProducts(await request('/api/products'));
    setVendors(activeUser?.role === 'ADMIN' ? await request('/api/vendors') : []);
    setCart(activeUser ? await request('/api/cart') : []);
  }
  async function run(action, message) {
    setBusy(true);
    setNotice(null);
    try {
      const result = await action();
      await reload();
      if (message) setNotice({ text: typeof message === 'function' ? message(result) : message });
      return true;
    } catch (error) {
      setNotice({ error: true, text: error.message });
      if (error.status === 401) {
        setUser(null); setCart([]); setVendors([]);
        setEditing(null); setEditingVendor(null); setView('shop');
      }
      return false;
    } finally { setBusy(false); }
  }
  useEffect(() => { run(refreshCsrf); }, []);

  function signIn(email, password) {
    return run(() => login(email, password), 'You are logged in.');
  }
  function signUp(name, email, password) {
    return run(() => register(name, email, password), 'Account created. Log in with your new credentials.');
  }
  function signOut() {
    return run(async () => {
      await logout(); setEditing(null); setEditingVendor(null); setView('shop');
    }, 'You are logged out.');
  }
  function add(productId) {
    return run(() => request('/api/cart/items', 'POST',
      new URLSearchParams({ productId, quantity: 1 })), 'Product added to your cart.');
  }
  function quantity(id, value) {
    return run(() => request(`/api/cart/items/${id}`, 'PUT',
      new URLSearchParams({ quantity: value })), 'Quantity updated.');
  }
  function remove(id) {
    return run(() => request(`/api/cart/items/${id}`, 'DELETE'), 'Item removed.');
  }
  function clearCart() {
    if (window.confirm('Remove every item from your cart?')) {
      return run(async () => {
        for (const item of cart) {
          await request(`/api/cart/items/${item.id}`, 'DELETE');
        }
      }, 'Your cart has been cleared.');
    }
  }
  function checkout() {
    return run(() => request('/api/cart/checkout', 'POST'),
      result => `Demo checkout complete: $${Number(result.total).toFixed(2)}. No payment was taken.`);
  }
  function saveProduct(id, body) {
    return run(async () => {
      await request(id ? `/api/products/${id}` : '/api/products', id ? 'PUT' : 'POST', body);
      setEditing(null);
    }, 'Product saved.');
  }
  function deleteProduct(id) {
    if (window.confirm('Delete this product?')) {
      return run(() => request(`/api/products/${id}`, 'DELETE'), 'Product deleted.');
    }
  }
  function editVendor(id) {
    return run(async () => {
      setEditingVendor(await request(`/api/vendors/${id}`));
    });
  }
  function saveVendor(id, body) {
    return run(async () => {
      await request(id ? `/api/vendors/${id}` : '/api/vendors', id ? 'PUT' : 'POST', body);
      setEditingVendor(null);
    }, 'Vendor saved.');
  }
  function deleteVendor(vendor) {
    if (window.confirm(`Delete ${vendor.name}? Its products must be reassigned or removed first.`)) {
      return run(async () => {
        await request(`/api/vendors/${vendor.id}`, 'DELETE');
        if (editingVendor?.id === vendor.id) setEditingVendor(null);
      }, 'Vendor deleted.');
    }
  }

  return <>
    <Header user={user} busy={busy} onLogout={signOut} onRefresh={() => run(refreshCsrf, 'Store refreshed.')} />
    <main>
      <section className="intro"><p className="eyebrow">{showVendors ? 'STORE ADMINISTRATION' : 'THE EVERYDAY COLLECTION'}</p>
        <h1>{showVendors ? 'Your suppliers.' : <>Good things.<br />Simple choices.</>}</h1>
        <p>{showVendors ? 'Manage vendor details and keep your product catalog connected.' : 'Useful essentials for your desk and daily routine.'}</p>
      </section>
      {isAdmin && <nav className="admin-tabs" aria-label="Store administration">
        <button className={view === 'shop' ? 'selected' : 'secondary'} aria-current={view === 'shop' ? 'page' : undefined}
          disabled={busy} onClick={() => { setView('shop'); setNotice(null); }}>Shop & products</button>
        <button className={view === 'vendors' ? 'selected' : 'secondary'} aria-current={view === 'vendors' ? 'page' : undefined}
          disabled={busy} onClick={() => { setView('vendors'); setNotice(null); }}>Vendors</button>
      </nav>}
      <div className="notice-area" aria-live="polite">
        {notice && <p className={notice.error ? 'notice error' : 'notice'} role={notice.error ? 'alert' : 'status'}>{notice.text}</p>}
        {busy && <span>Working...</span>}
      </div>
      {showVendors ? <div className="store-layout vendor-layout">
        <VendorList vendors={vendors} onEdit={editVendor} onDelete={deleteVendor} busy={busy} />
        <aside><VendorForm vendor={editingVendor} onSave={saveVendor}
          onCancel={() => setEditingVendor(null)} busy={busy} /></aside>
      </div> : <div className="store-layout">
        <ProductList products={products} user={user} busy={busy} onAdd={add}
          onEdit={setEditing} onDelete={deleteProduct} />
        <aside>{user ? <CartPanel items={cart} busy={busy} onQuantity={quantity}
          onRemove={remove} onClear={clearCart} onCheckout={checkout} /> : <AuthForm onLogin={signIn} onRegister={signUp} busy={busy} />}
          {user?.role === 'ADMIN' && <ProductForm product={editing} vendors={vendors}
            onSave={saveProduct} onCancel={() => setEditing(null)} busy={busy} />}
        </aside>
      </div>}
    </main>
    <footer>Spring Security demonstration. Use fictional account information only.</footer>
  </>;
}
