import {FC} from "react";
import AppBar from "@material-ui/core/AppBar";
import IconButton from "@material-ui/core/IconButton";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import clsx from "clsx";
import FingerprintIcon from '@mui/icons-material/Fingerprint';
import MenuIcon from "@material-ui/icons/Menu";
import {makeStyles} from "@material-ui/core";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import {NavLink} from "react-router-dom";
import {observer} from "mobx-react";
import {AuthState, OpenPanelType, Principal} from "../../def/client-types";

export const drawerWidth = 240;
/**
 * Стили заголовка страницы.
 */
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

export type HeaderPanelProps = OpenPanelType & {authState: AuthState};

/**
 * Функциональная компонента, отображения заголовка страницы.
 *
 * @param props
 *        пропсы, передаваемые в компоненту.
 * @returns JSX элемент заголовка страницы.
 */
const Header: FC<HeaderPanelProps> = (props: HeaderPanelProps ) => {
  const classes = useStyles();
  const [open, setOpen]: [boolean, Function] = [props.open, props.setOpen];

  let userName = props?.authState?.principal?.username ?? "";

  /**
   * Отображает навигационное меню.
   */
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
        <Typography variant="h6">{userName}</Typography>
        <ButtonGroup>
          <IconButton
            color="inherit"
            aria-label="fingerprint"
            className={clsx(open && classes.hide)}
            component={NavLink} to='/login'
          >
            <FingerprintIcon />
          </IconButton>
          <IconButton
            color="inherit"
            arial-label="open drawer"
            onClick={handleDrawerOpen}
            className={clsx(open && classes.hide)}
          >
            <MenuIcon/>
          </IconButton>
        </ButtonGroup>
      </Toolbar>
    </AppBar>
  )
}

export default observer(Header);