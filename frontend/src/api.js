const BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

function headers(userId, isJson = true) {
  const baseHeaders = {
    'X-User-Id': userId || 'demo-user'
  };

  if (isJson) {
    baseHeaders['Content-Type'] = 'application/json';
  }

  return baseHeaders;
}

function extractErrorMessage(payload, fallback) {
  if (!payload) {
    return fallback;
  }
  if (typeof payload === 'string') {
    return payload;
  }
  return payload.detail || payload.message || payload.error || payload.title || fallback;
}

async function parseErrorResponse(response) {
  const fallback = `Request failed: ${response.status}`;
  const contentType = response.headers.get('content-type') || '';

  if (contentType.includes('application/json')) {
    try {
      const json = await response.json();
      return extractErrorMessage(json, fallback);
    } catch (error) {
      return fallback;
    }
  }

  const text = await response.text();
  return text || fallback;
}

async function request(path, options = {}, userId) {
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      ...headers(userId, options.isJson !== false),
      ...(options.headers || {})
    }
  });

  if (!response.ok) {
    const message = await parseErrorResponse(response);
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export const api = {
  getProducts: () => request('/api/products'),
  getCart: (userId) => request('/api/cart', { method: 'GET' }, userId),
  addItem: (userId, productId, quantity = 1) => request(
    '/api/cart/items',
    {
      method: 'POST',
      body: JSON.stringify({ productId, quantity })
    },
    userId
  ),
  updateItem: (userId, productId, quantity) => request(
    `/api/cart/items/${productId}`,
    {
      method: 'PUT',
      body: JSON.stringify({ quantity })
    },
    userId
  ),
  removeItem: (userId, productId) => request(
    `/api/cart/items/${productId}`,
    { method: 'DELETE' },
    userId
  ),
  checkout: (userId, payload) => request(
    '/api/cart/checkout',
    {
      method: 'POST',
      body: JSON.stringify(payload)
    },
    userId
  ),
  getOrders: (userId) => request('/api/orders', { method: 'GET' }, userId),
  getAllOrders: () => request('/api/admin/orders', { method: 'GET' }),
  uploadImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return request('/api/files/upload', {
      method: 'POST',
      body: formData,
      isJson: false
    });
  },
  createProduct: (payload) => request('/api/products', {
    method: 'POST',
    body: JSON.stringify(payload)
  })
};
