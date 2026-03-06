import { useEffect, useMemo, useState } from 'react';
import { api } from './api';
import ProductCard from './components/ProductCard';
import CartPanel from './components/CartPanel';
import OrdersPanel from './components/OrdersPanel';
import AddProductForm from './components/AddProductForm';
import CheckoutForm from './components/CheckoutForm';
import AdminOrdersPanel from './components/AdminOrdersPanel';

function CustomerApp() {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState({ items: [], total: 0 });
  const [orders, setOrders] = useState([]);
  const [userId, setUserId] = useState('demo-user');
  const [status, setStatus] = useState({ type: 'info', message: 'Loading customer app...' });

  const itemCount = useMemo(
    () => (cart.items || []).reduce((sum, item) => sum + item.quantity, 0),
    [cart.items]
  );

  async function refreshAll(activeUserId = userId) {
    try {
      const [productData, cartData, orderData] = await Promise.all([
        api.getProducts(),
        api.getCart(activeUserId),
        api.getOrders(activeUserId)
      ]);
      setProducts(productData);
      setCart(cartData);
      setOrders(orderData);
      setStatus({ type: 'success', message: 'Customer data synced.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  useEffect(() => {
    refreshAll();
  }, []);

  async function addToCart(productId) {
    try {
      const updatedCart = await api.addItem(userId, productId, 1);
      setCart(updatedCart);
      setStatus({ type: 'success', message: 'Added to cart.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  async function updateQty(productId, quantity) {
    if (quantity < 1) {
      return;
    }
    try {
      const updatedCart = await api.updateItem(userId, productId, quantity);
      setCart(updatedCart);
      setStatus({ type: 'success', message: 'Quantity updated.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  async function removeItem(productId) {
    try {
      await api.removeItem(userId, productId);
      await refreshAll();
      setStatus({ type: 'success', message: 'Item removed.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  async function placeOrder(checkoutPayload) {
    try {
      await api.checkout(userId, checkoutPayload);
      await refreshAll();
      setStatus({ type: 'success', message: 'Order placed.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  async function changeUser(event) {
    event.preventDefault();
    await refreshAll(userId);
  }

  return (
    <div className="app-shell">
      <header className="hero">
        <div>
          <p className="kicker">Customer App</p>
          <h1>Foodies Customer Checkout</h1>
          <p>Browse items, add to cart, enter delivery details, and pay via COD.</p>
        </div>
        <form onSubmit={changeUser} className="user-form">
          <label htmlFor="userId">Customer ID</label>
          <div className="user-row">
            <input id="userId" value={userId} onChange={(e) => setUserId(e.target.value)} />
            <button type="submit">Switch</button>
          </div>
          <div className="cart-badge">Cart Items: {itemCount}</div>
        </form>
      </header>

      <main className="layout">
        <section className="panel product-panel">
          <div className="panel-header">
            <h2>Menu</h2>
            <span>{products.length} products</span>
          </div>
          <div className="product-grid">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} onAdd={addToCart} />
            ))}
          </div>
        </section>

        <aside className="side-column">
          <CartPanel cart={cart} onUpdateQty={updateQty} onRemove={removeItem} />
          <CheckoutForm disabled={(cart.items || []).length === 0} onCheckout={placeOrder} />
          <OrdersPanel orders={orders} />
          <div className={`status ${status.type}`}>{status.message}</div>
        </aside>
      </main>
    </div>
  );
}

function AdminApp() {
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [status, setStatus] = useState({ type: 'info', message: 'Loading admin app...' });

  async function refreshAll() {
    try {
      const [productData, orderData] = await Promise.all([
        api.getProducts(),
        api.getAllOrders()
      ]);
      setProducts(productData);
      setOrders(orderData);
      setStatus({ type: 'success', message: 'Admin data synced.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  useEffect(() => {
    refreshAll();
  }, []);

  async function createProduct(payload) {
    try {
      if (!payload.file) {
        throw new Error('Please select an image file.');
      }
      const maxSizeBytes = 10 * 1024 * 1024;
      if (payload.file.size > maxSizeBytes) {
        throw new Error('Image is too large. Max allowed size is 10MB.');
      }
      const uploadResponse = await api.uploadImage(payload.file);
      await api.createProduct({
        name: payload.name,
        description: payload.description,
        price: payload.price,
        imageUrl: uploadResponse.imageUrl
      });
      await refreshAll();
      setStatus({ type: 'success', message: 'Product created with S3 image.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  }

  return (
    <div className="app-shell">
      <header className="hero">
        <div>
          <p className="kicker">Admin App</p>
          <h1>Foodies Admin Console</h1>
          <p>Add products and monitor all customer orders.</p>
        </div>
        <div className="user-form">
          <button type="button" onClick={refreshAll}>Refresh Data</button>
          <div className="cart-badge">Products: {products.length} | Orders: {orders.length}</div>
        </div>
      </header>

      <main className="layout">
        <section className="panel product-panel">
          <div className="panel-header">
            <h2>Current Products</h2>
            <span>{products.length} items</span>
          </div>
          <div className="product-grid">
            {products.map((product) => (
              <article key={product.id} className="product-card">
                <div className="product-image-wrap">
                  <img src={product.imageUrl} alt={product.name} className="product-image" loading="lazy" />
                </div>
                <div className="product-body">
                  <strong>{product.name}</strong>
                  <p>{product.description}</p>
                  <div className="product-row">
                    <span>${Number(product.price).toFixed(2)}</span>
                    <span>{product.id.slice(-6)}</span>
                  </div>
                </div>
              </article>
            ))}
          </div>
        </section>

        <aside className="side-column">
          <AddProductForm onCreate={createProduct} />
          <AdminOrdersPanel orders={orders} />
          <div className={`status ${status.type}`}>{status.message}</div>
        </aside>
      </main>
    </div>
  );
}

export default function App() {
  const [route, setRoute] = useState(window.location.hash || '#/customer');

  useEffect(() => {
    if (!window.location.hash) {
      window.location.hash = '#/customer';
    }
    function onHashChange() {
      setRoute(window.location.hash || '#/customer');
    }
    window.addEventListener('hashchange', onHashChange);
    return () => window.removeEventListener('hashchange', onHashChange);
  }, []);

  const isAdminApp = route.startsWith('#/admin');

  return (
    <>
      <nav className="mode-nav">
        <a className={!isAdminApp ? 'active' : ''} href="#/customer">Customer App</a>
        <a className={isAdminApp ? 'active' : ''} href="#/admin">Admin App</a>
      </nav>
      {isAdminApp ? <AdminApp /> : <CustomerApp />}
    </>
  );
}
