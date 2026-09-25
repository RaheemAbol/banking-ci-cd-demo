let csrf = null;

export async function refreshCsrf() {
  const response = await fetch('/api/auth/csrf');
  if (!response.ok) throw new Error('Cannot get CSRF token. Check the backend.');
  csrf = await response.json();
}

export async function request(path, method = 'GET', body = null) {
  const headers = {};
  if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
    if (!csrf) await refreshCsrf();
    headers[csrf.headerName] = csrf.token;
  }
  const isForm = body instanceof URLSearchParams;
  if (body !== null && !isForm) headers['Content-Type'] = 'application/json';
  const response = await fetch(path, {
    method, headers, credentials: 'same-origin',
    body: body === null ? null : (isForm ? body : JSON.stringify(body))
  });
  if (!response.ok) {
    if (response.status === 401 || response.status === 403) csrf = null;
    const messages = {
      400: 'Check the required fields, quantity, or password length.',
      401: 'Login required, or the email/password is incorrect.',
      403: 'Access denied, or the CSRF token expired. Refresh before retrying.',
      404: 'That record is unavailable.',
      409: path.startsWith('/api/vendors')
        ? "This vendor still has products. Reassign or remove those products before deleting the vendor."
        : 'Email already registered, or related records prevent this change.'
    };
    const error = new Error(`HTTP ${response.status}: ${messages[response.status] || 'Request failed.'}`);
    error.status = response.status;
    throw error;
  }
  const text = await response.text();
  if (!text) return null;
  const contentType = response.headers.get('Content-Type') || '';
  return contentType.includes('application/json') ? JSON.parse(text) : text;
}
