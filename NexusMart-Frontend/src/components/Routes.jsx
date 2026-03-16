import { Routes, Route } from "react-router-dom";
import Landing from "../pages/Landing";
import AdminLogin from "../components/AdminLogin";
import AdminDashboard from "../pages/AdminDashboard"; // ✅ ADD
import CustomerHomePage from "../pages/CustomerHomePage";
import CartPage from "../pages/CartPage";
import OrderPage from "../pages/OrderPage";
import Profile from "../pages/Profile";

<Route path="/profile" element={<Profile />} />


export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />

      {/* ADMIN */}
      <Route path="/admin-login" element={<AdminLogin />} />
      <Route path="/admindashboard" element={<AdminDashboard />} />

      {/* USER */}
      <Route path="/customerhome" element={<CustomerHomePage />} />
      <Route path="/UserCartPage" element={<CartPage />} />
      <Route path="/orders" element={<OrderPage />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/profile" element={<Profile />} />

    </Routes>
  );
}
