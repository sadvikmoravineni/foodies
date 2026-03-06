export default function ProductCard({ product, onAdd }) {
  return (
    <article className="product-card">
      <div className="product-image-wrap">
        <img src={product.imageUrl} alt={product.name} className="product-image" loading="lazy" />
      </div>
      <div className="product-body">
        <h3>{product.name}</h3>
        <p>{product.description}</p>
        <div className="product-row">
          <strong>${Number(product.price).toFixed(2)}</strong>
          <button onClick={() => onAdd(product.id)}>Add</button>
        </div>
      </div>
    </article>
  );
}
