export default function Header({ user, onLogout, onRefresh, busy }) {
  return <header className="site-header">
    <div><strong>NORTHLINE</strong><span>Everyday essentials</span></div>
    <nav aria-label="Account">
      <span>{user ? `${user.displayName} · ${user.role}` : 'Browsing as a guest'}</span>
      <button className="secondary" onClick={onRefresh} disabled={busy}>Refresh</button>
      {user && <button onClick={onLogout} disabled={busy}>Log out</button>}
    </nav>
  </header>;
}
