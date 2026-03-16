export default function ProductCard({ product, onAddToCart }) {
  return (
    <div className="product-card">
      <img
        src={product.imageUrl || "https://via.placeholder.com/220"}
        alt={product.name}
      />
      <h4>{product.name}</h4>
      <p className="price">₹{product.price}</p>
      <button onClick={() => onAddToCart(product.id)}>
        Add to Cart
      </button>
    </div>
  );
}
