import { describe, expect, it } from 'vitest';
import { renderToStaticMarkup } from 'react-dom/server';
import ProductList from './ProductList';

const products = [{ id: 101, name: 'Desk Lamp', category: 'OFFICE', price: 39.9,
  vendor: { name: 'Northline Office' } }];

function renderCatalog(user = null) {
  return renderToStaticMarkup(<ProductList products={products} user={user}
    busy={false} onAdd={() => {}} onEdit={() => {}} onDelete={() => {}} />);
}

// Rendering tests need neither a running browser nor a running backend.
describe('Product catalog', () => {
  it('renders the product, vendor, and a price with two decimal places', () => {
    const html = renderCatalog();
    expect(html).toContain('Desk Lamp');
    expect(html).toContain('Northline Office');
    expect(html).toContain('$39.90');
  });

  it('asks a guest to log in and disables the shopping button', () => {
    const html = renderCatalog();
    expect(html).toMatch(/<button[^>]*disabled[^>]*>Log in to shop<\/button>/);
    expect(html).not.toContain('>Edit</button>');
    expect(html).not.toContain('>Delete</button>');
  });

  it('shows shopping and management buttons for an admin', () => {
    const html = renderCatalog({ role: 'ADMIN' });
    expect(html).toContain('>Add to cart</button>');
    expect(html).toContain('>Edit</button>');
    expect(html).toContain('>Delete</button>');
  });
});
