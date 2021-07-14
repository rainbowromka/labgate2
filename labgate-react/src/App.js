import './App.css';
import Header from "./components/Header/Header";
import Footer from "./components/Footer/Footer";
import Navbar from "./components/NavBar/Navbar";
import DriversContent from "./components/DriversContent/DriversContent";
import {Route} from "react-router-dom";
import Devices from "./components/Devices/Devices";

function App() {
  return (
      <div className="app-wrapper">
        <Header/>
        <Navbar/>
        <div className="driverContent">
          <Route path='/drivers' render={() => <DriversContent/>}/>
          <Route path='/devices' render={() => <Devices/>}/>
        </div>
        <Footer/>
      </div>
  );
}

export default App;
