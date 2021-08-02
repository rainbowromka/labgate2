import React from "react";
import './App.css';
import Header, {drawerWidth} from "./components/Header/Header";
import Footer from "./components/Footer/Footer";
import Navbar from "./components/NavBar/Navbar";
import DriversContent from "./components/DriversContent/DriversContent";
import {Route} from "react-router-dom";
import Devices from "./components/Devices/Devices";
import DriverContainer
  from "./components/DriversContent/Driver/DriverContainer";
import CssBaseline from "@material-ui/core/CssBaseline";
import {makeStyles} from "@material-ui/core";
import clsx from "clsx";

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },
  drawerHeader: {
    display: 'flex',
    alignItems: 'center',
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
    justifyContent: 'flex-start',
  },
  main: {
    flexGrow: 1,
    // padding: theme.spacing(0, 0),
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginRight: -drawerWidth,
  },
  mainShift: {
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
    marginRight: 0,
  },
  content: {
    padding: theme.spacing(3,2,1,2),
    background: "#dbe7e7"
  }
}));

function App() {
  const classes = useStyles()
  const [open, setOpen] = React.useState(false);

  return (
    <>
      <CssBaseline/>
      <div className={classes.root}>
        <Header open={open} setOpen={setOpen}/>
        <main
          className={clsx(classes.main, {
            [classes.mainShift]: false,
          })}>
          <div className={classes.drawerHeader} />
          <div className={classes.content}>
            <Route path='/drivers' render={() => <DriversContent/>}/>
            <Route path='/devices' render={() => <Devices/>}/>
            <Route path='/driver' render={() => <DriverContainer/>}/>
          </div>
        </main>
        <Navbar open={open} setOpen={setOpen}/>
      </div>
      <Footer/>
    </>
  );
}

export default App;
