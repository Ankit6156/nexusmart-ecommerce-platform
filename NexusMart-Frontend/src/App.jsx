import { BrowserRouter } from "react-router-dom";
import AppRoutes from "./components/Routes";

export default function App() {
  return (
    <BrowserRouter>
      <AppRoutes />
    </BrowserRouter>
  );
}
