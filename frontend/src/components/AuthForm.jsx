import { useState } from 'react';

export default function AuthForm({ onLogin, onRegister, busy }) {
  const [mode, setMode] = useState('login');
  async function submit(event) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const email = data.get('email');
    const password = data.get('password');
    const ok = mode === 'login' ? await onLogin(email, password)
      : await onRegister(data.get('displayName'), email, password);
    if (ok && mode === 'register') setMode('login');
  }
  return <section className="panel">
    <h2>{mode === 'login' ? 'Welcome back' : 'Create an account'}</h2>
    <p>Sign in to keep a cart and try the demonstration checkout.</p>
    <form onSubmit={submit}>
      {mode === 'register' && <label>Name<input name="displayName" required maxLength={100} /></label>}
      <label>Email<input name="email" type="email" autoComplete="username" required /></label>
      <label>Password<input name="password" type="password" required minLength={mode === 'register' ? 8 : 1}
        autoComplete={mode === 'login' ? 'current-password' : 'new-password'} /></label>
      {mode === 'register' && <small>At least 8 characters. Use a demonstration password.</small>}
      <button disabled={busy}>{mode === 'login' ? 'Log in' : 'Register'}</button>
    </form>
    <button className="text-button" disabled={busy} onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
      {mode === 'login' ? 'Create an account' : 'Already registered? Log in'}
    </button>
  </section>;
}
