import react, {SyntheticEvent} from "react";
import {NavLink} from "react-router-dom";
import {drawerWidth, OpenPanelType} from "../Header/Header";
import {ListItemText, makeStyles, useTheme} from "@material-ui/core";
import ChevronLeftIcon from "@material-ui/icons/ChevronLeft";
import ChevronRightIcon from "@material-ui/icons/ChevronRight";
import Divider from "@material-ui/core/Divider";
import Drawer from "@material-ui/core/Drawer";
import IconButton from "@material-ui/core/IconButton";
import InboxIcon from "@material-ui/icons/MoveToInbox";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import Typography from "@material-ui/core/Typography";

const useStyles = makeStyles((theme) => ({
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    background: "#0f94b6",
    width: drawerWidth,
  },
  drawerHeader: {
    display: 'flex',
    alignItems: 'center',
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
    justifyContent: 'flex-start',
  },
  selectedListItem: {
    color: "#f3cb86",
    fontWeight: "bolder",
    "& .MuiListItemIcon-root": {
      color: "#f3cb86",
      fontWeight: "bolder",
    },
  },
  listItem: {
    color: "white",
    "& .MuiListItemIcon-root": {
      color: "white",
    },
    "&:hover": {
      // backgroundColor: "blue",
      color: "#ffba73",
      "& .MuiListItemIcon-root": {
        color: "#ffba73"
      }
    }
  },
  title: {
    flexGrow: 1,
  },
}));

type AllProps = OpenPanelType & {
}

const Navbar = (props: AllProps) => {
  const classes = useStyles();
  const theme = useTheme();

  const [open, setOpen] = [props.open, props.setOpen];
  const [selectedIndex, setSelectedIndex] = react.useState(1);

  const handleDrawerClose = () => {
    setOpen(false);
  }

  const handleListItemClick = (event: SyntheticEvent, index: number) => {
    setSelectedIndex(index);
    setOpen(false);
  }

  return (
    <Drawer
      className={classes.drawer}
      variant="persistent"
      anchor="right"
      open={open}
      classes={{
        paper: classes.drawerPaper,
      }}
      onClose={handleDrawerClose}
    >
      <div className={classes.drawerHeader}>
        <Typography variant="h6" noWrap className={classes.title}/>
        <IconButton onClick={handleDrawerClose}>
          {theme.direction === 'rtl' ? <ChevronLeftIcon/> : <ChevronRightIcon/>}
        </IconButton>
      </div>
      <Divider/>
      <List>
        {[{
          to: "/drivers",
          icon: <InboxIcon/>,
          title: "Список драйверов",
        },{
          to: "/devices",
          icon: <InboxIcon/>,
          title: "Приборы",
        }].map((item, key) => {
          let selected = selectedIndex === key;
          return <ListItem
            button
            component={NavLink} to={item.to}
            selected={selected}
            onClick={(event: SyntheticEvent) => handleListItemClick(event, key)}
            className={selected ? classes.selectedListItem : classes.listItem}
            key={key}
          >
            <ListItemIcon><InboxIcon className={"color:'inherit'"}/></ListItemIcon>
            <ListItemText primary={item.title} />
          </ListItem>
        })}
      </List>
    </Drawer>
  )
}

export default Navbar;