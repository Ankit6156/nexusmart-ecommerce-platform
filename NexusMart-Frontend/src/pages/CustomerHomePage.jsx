import React, { useEffect, useState } from "react";
import CategoryNavigation from "../components/CategoryNavigation";
import { ProductList } from "../components/ProductList";
import Header from "../components/Header";
import Footer from "../components/Footer";
import "../assets/styles.css";

export default function CustomerHomePage() {

  /* ================= STATES ================= */

  const [allProducts, setAllProducts] = useState([]);
  const [products, setProducts] = useState([]);
  const [username, setUsername] = useState("");

  const [cartCount, setCartCount] = useState(0);
  const [cartError, setCartError] = useState(false);
  const [isCartLoading, setIsCartLoading] = useState(true);

  const [loading, setLoading] = useState(true);   // ✅ NEW LOADING STATE


  /* ================= LOAD ALL PRODUCTS ================= */

  useEffect(() => {
    fetchAllProducts();
  }, []);


  /* ================= LOAD CART WHEN USER AVAILABLE ================= */

  useEffect(() => {
    if (username) {
      fetchCartCount();
    }
  }, [username]);


  /* ================= FETCH ALL PRODUCTS ================= */

  const fetchAllProducts = async () => {

    setLoading(true);   // ✅ START LOADING

    try {

      const response = await fetch(
        "http://localhost:8080/api/products",
        {
          credentials: "include"
        }
      );

      if (!response.ok) {

        setAllProducts([]);
        setProducts([]);

        return;
      }

      const data = await response.json();

      setUsername(data?.user?.name || "");

      setAllProducts(data?.products || []);
      setProducts(data?.products || []);

    }
    catch (error) {

      console.error("Error fetching products:", error);

      setAllProducts([]);
      setProducts([]);

    }
    finally {

      setLoading(false);   // ✅ STOP LOADING

    }
  };


  /* ================= CATEGORY FILTER ================= */

  const CATEGORY_KEYWORDS = {

    Shirts: ["shirt"],

    Pants: ["pant", "jeans", "trouser"],

    Mobiles: ["mobile", "phone"],

    Accessories: ["accessory", "watch", "belt", "tv"],

    "Mobile Accessories": ["charger", "cover", "case", "accessory"]

  };


  const handleCategoryClick = (category) => {

    const keywords = CATEGORY_KEYWORDS[category] || [];

    const filtered = allProducts.filter((product) => {

      const text =
        (product.name + product.description).toLowerCase();

      return keywords.some((k) =>
        text.includes(k)
      );

    });

    setProducts(filtered);

  };


  /* ================= FETCH CART COUNT ================= */

  const fetchCartCount = async () => {

    setIsCartLoading(true);

    try {

      const response = await fetch(

        `http://localhost:8080/api/cart/items/count?username=${username}`,

        {
          credentials: "include"
        }

      );

      if (!response.ok) {

        setCartCount(0);
        setCartError(true);

        return;
      }

      const count = await response.json();

      setCartCount(typeof count === "number" ? count : 0);

      setCartError(false);

    }
    catch (error) {

      console.error("Cart count error:", error);

      setCartError(true);

    }
    finally {

      setIsCartLoading(false);

    }

  };


  /* ================= ADD TO CART ================= */

  const handleAddToCart = async (productId) => {

    if (!username) return;

    try {

      const response = await fetch(

        "http://localhost:8080/api/cart/add",

        {

          method: "POST",

          headers: {
            "Content-Type": "application/json"
          },

          credentials: "include",

          body: JSON.stringify({

            username,
            productId

          })

        }

      );

      if (response.ok) {

        fetchCartCount();

      }

    }
    catch (error) {

      console.error("Add to cart error:", error);

    }

  };


  /* ================= UI ================= */

  return (

    <div className="customer-homepage">


      {/* HEADER */}

      <Header

        cartCount={
          isCartLoading
            ? 0
            : cartError
              ? 0
              : cartCount
        }

        username={username}

      />


      {/* CATEGORY NAV */}

      <nav className="navigation">

        <CategoryNavigation

          onCategoryClick={handleCategoryClick}

        />

      </nav>


      {/* MAIN CONTENT */}

      <main className="main-content">


        {loading ? (

          <div className="loading-container">

            <h2>Loading products...</h2>

          </div>

        ) : (

          <ProductList

            products={products}

            onAddToCart={handleAddToCart}

          />

        )}


      </main>


      {/* FOOTER */}

      <Footer />


    </div>

  );

}
