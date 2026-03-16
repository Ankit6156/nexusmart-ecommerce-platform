import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Footer from "../components/Footer";
import Logo from "../components/Logo";
import CustomModal from "../components/CustomModal";
import "../assets/admin.css";

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [modalType, setModalType] = useState(null); // State to manage modal visibility and type
  const [modalData, setModalData] = useState(null); // State to store data passed to modal (if needed)
  const [response, setResponse] = useState(null); // State to store API responses

  // Centralized card data
  const cardData = [
    {
      title: "Add Product",
      description: "Create and manage new product listings with validation",
      team: "Product Management",
      modalType: "addProduct",
    },
    {
      title: "Delete Product",
      description: "Remove products from inventory system",
      team: "Product Management",
      modalType: "deleteProduct",
    },
    {
      title: "Modify User",
      description: "Update user details and manage roles",
      team: "User Management",
      modalType: "modifyUser",
    },
    {
      title: "View User Details",
      description: "Fetch and display details of a specific user",
      team: "User Management",
      modalType: "viewUser",
    },
    {
      title: "Monthly Business",
      description: "View revenue metrics for specific months",
      team: "Analytics",
      modalType: "monthlyBusiness",
    },
    {
      title: "Day Business",
      description: "Track daily revenue and transactions",
      team: "Analytics",
      modalType: "dailyBusiness",
    },
    {
      title: "Yearly Business",
      description: "Analyze annual revenue performance",
      team: "Analytics",
      modalType: "yearlyBusiness",
    },
    {
      title: "Overall Business",
      description: "View total revenue since inception",
      team: "Analytics",
      modalType: "overallBusiness",
    },
  ];

  const handleLogout = async () => {
  try {
    await fetch("http://localhost:8080/api/auth/logout", {
      method: "POST",
      credentials: "include",
    });
  } catch (e) {
    console.warn("Logout request failed, clearing session anyway");
  } finally {
    localStorage.clear();   // ✅ ALWAYS clear client state
    navigate("/admin-login");
  }
};

  // Handlers for each modal action
  const handleAddProductSubmit = async (productData) => {
    try {
      const response = await fetch("http://localhost:8080/admin/products/add", {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(productData),
      });
      const data = await response.json();
      setResponse({ product: data, imageUrl: productData.imageUrl });
      setModalType("addProduct");
    } catch (error) {
      console.error("Error adding product:", error);
    }
  };

  const handleDeleteProductSubmit = async ({ productId }) => {
    try {
      const response = await fetch(
        "http://localhost:8080/admin/products/delete",
        {
          method: "DELETE",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ productId }),
        }
      );
      console.log("response", response);
      // Handle response logic
      if (response.ok) {
        setResponse({ message: "Delete Success" });
      } else {
        const errorMessage = await response.text();
        setResponse({ message: `Error: ${errorMessage}` });
      }
    } catch (error) {
      console.error("Error deleting product:", error);
    }
  };

  const handleViewUserSubmit = async ({ userId }) => {
  try {
    const response = await fetch(
      `http://localhost:8080/admin/user/getbyid/${userId}`,
      {
        method: "GET",
        credentials: "include",
      }
    );

    if (response.ok) {
      const data = await response.json();
      setResponse({ user: data });
      setModalType("response");
    } else {
      const errorMessage = await response.text();
      setResponse({ message: `Error: ${errorMessage}` });
      setModalType("response");
    }
  } catch (error) {
    console.error("Error fetching user details:", error);
    setResponse({ message: "Error: Something went wrong" });
    setModalType("response");
  }
};

  const handleModifyUserSubmit = async (data) => {

  // STEP 1: Fetch user details (when only ID is provided)
  if (!data.username) {
    try {
      console.log("Fetching user details for ID:", data.userId);

      const response = await fetch(
        `http://localhost:8080/admin/user/getbyid/${data.userId}`,
        {
          method: "GET",
          credentials: "include"
        }
      );

      if (response.ok) {
        const userDetails = await response.json();
        setResponse({ user: userDetails });
        setModalType("modifyUser");
      } else {
        const error = await response.text();
        setResponse({ message: `Error: ${error}` });
        setModalType("response");
      }

    } catch (error) {
      console.error("Error fetching user details:", error);
      setResponse({ message: "Error: Something went wrong" });
      setModalType("response");
    }
  }

  // STEP 2: Update user details
  else {
    try {
      console.log("Updating user details:", data);

      const response = await fetch(
        "http://localhost:8080/admin/user/modify",
        {
          method: "PUT",
          credentials: "include",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(data)
        }
      );

      if (response.ok) {
        const updatedUser = await response.json();
        setResponse({ user: updatedUser });
        setModalType("response");
      } else {
        const error = await response.text();
        setResponse({ message: `Error: ${error}` });
        setModalType("response");
      }

    } catch (error) {
      console.error("Error updating user details:", error);
      setResponse({ message: "Error: Something went wrong" });
      setModalType("response");
    }
  }
};

  const handleMonthlyBusiness = async (data) => {
    try {
      const response = await fetch(
        `http://localhost:8080/admin/business/monthly?month=${data?.month}&year=${data?.year}`,
        {
          method: "GET",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (response.ok) {
        const data = await response.json();
        setResponse({ monthlyBusiness: data });
        setModalType("monthlyBusiness");
      } else {
        const errorMessage = await response.text();
        setResponse({ message: `Error: ${errorMessage}` });
        setModalType("monthlyBusiness");
      }
    } catch (error) {
      console.error("Error fetching user details:", error);
      setResponse({ message: "Error: Something went wrong" });
      setModalType("response");
    }
  };

  const handleDailyBusiness = async (data) => {
    try {
      const response = await fetch(
        `http://localhost:8080/admin/business/daily?date=${data?.date}`,
        {
          method: "GET",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (response.ok) {
        const data = await response.json();
        setResponse({ dailyBusiness: data });
        setModalType("dailyBusiness");
      } else {
        const errorMessage = await response.text();
        setResponse({ message: `Error: ${errorMessage}` });
        setModalType("dailyBusiness");
      }
    } catch (error) {
      console.error("Error fetching user details:", error);
      setResponse({ message: "Error: Something went wrong" });
      setModalType("dailyBusiness");
    }
  };
const handleYearlyBusiness = async (data) => {

  const year = Number(data?.year);

  if (!year || isNaN(year)) {
    alert("Valid year is required");
    return;
  }

  try {
    const response = await fetch(
      `http://localhost:8080/admin/business/yearly?year=${year}`,
      {
        method: "GET",
        credentials: "include"
      }
    );

    const result = await response.json();
    setResponse({ yearlyBusiness: result });
    setModalType("yearlyBusiness");

  } catch (error) {
    console.error(error);
  }
};

  const handleOverallBusiness = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/admin/business/overall`,
        {
          method: "GET",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (response.ok) {
        const data = await response.json();
        setResponse({ overallBusiness: data });
        setModalType("overallBusiness");
      } else {
        const errorMessage = await response.text();
        setResponse({ message: `Error: ${errorMessage}` });
        setModalType("overallBusiness");
      }
    } catch (error) {
      console.error("Error fetching user details:", error);
      setResponse({ message: "Error: Something went wrong" });
      setModalType("overallBusiness");
    }
  };

  return (
    <div className="admin-dashboard">
      {/* Header */}
      <header className="dashboard-header">
        <Logo />
        <div className="user-info">
          <span className="username">Admin</span>
          <div className="dropdown">
            <button className="dropdown-button" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="dashboard-content">
        <div className="cards-grid">
          {cardData.map((card, index) => (
            <div
              key={index}
              className="card"
              onClick={() => {
                setModalType(card.modalType);
                setModalData(null); // Clear any previous data
              }}
            >
              <div className="card-content">
                <h3 className="card-title">{card.title}</h3>
                <p className="card-description">{card.description}</p>
                <span className="card-team">Team: {card.team}</span>
              </div>
            </div>
          ))}
        </div>
      </main>

      {/* Footer */}
      <Footer />

      {/* Modal */}
      {modalType && (
        <CustomModal
          modalType={modalType}
          onClose={() => {
            setModalType(null);
            setResponse(null);
          }}
          onSubmit={(data) => {
            switch (modalType) {
              case "addProduct":
                handleAddProductSubmit(data);
                break;
              case "deleteProduct":
                handleDeleteProductSubmit(data);
                break;
              case "viewUser":
                handleViewUserSubmit(data);
                break;
              case "modifyUser":
                handleModifyUserSubmit(data);
                break;
              case "monthlyBusiness":
                handleMonthlyBusiness(data);
                break;
              case "dailyBusiness":
                handleDailyBusiness(data);
                break;
              case "yearlyBusiness":
                handleYearlyBusiness(data);
                break;
              case "overallBusiness":
                handleOverallBusiness();
                break;

              // Add more cases here as needed
              default:
                break;
            }
          }}
          response={response}
        />
      )}
    </div>
  );
};

export default AdminDashboard;