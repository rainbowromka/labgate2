import './App.css';
import Header from "./components/Header/Header";
import Footer from "./components/Footer/Footer";
import Navbar from "./components/NavBar/Navbar";
import DriversContent from "./components/DriversContent/DriversContent";
import {Route} from "react-router-dom";
import Devices from "./components/Devices/Devices";
import DriverContainer
  from "./components/DriversContent/Driver/DriverContainer";

function App() {
  return (
      <div className="app-wrapper">
        <Header/>
        <Navbar/>
        <div className="driverContent">
          <Route path='/drivers' render={() => <DriversContent/>}/>
          <Route path='/devices' render={() => <Devices/>}/>
          <Route path='/driver' render={() => <DriverContainer/>}/>
        </div>
        <Footer/>
      </div>
  );
}

export default App;
