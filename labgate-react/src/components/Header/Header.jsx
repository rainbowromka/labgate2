import React from "react";
import AppBar from "@material-ui/core/AppBar";
import IconButton from "@material-ui/core/IconButton";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import clsx from "clsx";
import MenuIcon from "@material-ui/icons/Menu";
import {makeStyles} from "@material-ui/core";

export const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  appBar: {
    background: "#06b0b1",
    transition: theme.transitions.create(['margin', 'width'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
  },
  appBarShift: {
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(['margin', 'width'], {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
    marginRight: drawerWidth,
  },
  logo: {
    maxWidth: 214,
  },
  title: {
    flexGrow: 1,
  },
  hide: {
    display: 'none',
  },
}));

const Header = (props) => {
  const classes = useStyles();
  const [open, setOpen] = [props.open, props.setOpen];

  let handleDrawerOpen = () => {
    setOpen(true);
  }

  return (
    <AppBar
      position="fixed"
      className={clsx(classes.appBar, {
        [classes.appBarShift]: open,
      })}
    >
      <Toolbar>
        <img className={classes.logo} src="content\idc_logo.png"/>
        <Typography variant="h6" noWrap className={classes.title}/>
        <IconButton
          color="inherit"
          arial-label="open drawer"
          edge="end"
          onClick={handleDrawerOpen}
          className={clsx(open && classes.hide)}
        >
          <MenuIcon/>
        </IconButton>
      </Toolbar>
    </AppBar>
  // <div className={s.header}>
  //
  //     <div>menu</div>
  //   </div>
  )
}

export default Header;