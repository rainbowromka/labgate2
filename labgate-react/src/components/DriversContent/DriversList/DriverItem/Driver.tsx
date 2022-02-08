import {FC} from "react";
import {NavLink} from "react-router-dom";
import {makeStyles} from "@material-ui/core";
import Box from "@material-ui/core/Box";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Grid from "@material-ui/core/Grid";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import CreateIcon from "@material-ui/icons/Create";
import DeleteIcon from "@material-ui/icons/Delete";
import PauseIcon from '@material-ui/icons/Pause';
import PlayArrowIcon from '@material-ui/icons/PlayArrow';
import ShareIcon from '@material-ui/icons/Share';
import {
  DRIVER_STATUS_STARTING, DRIVER_STATUS_STOP, DRIVER_STATUS_STOPPING,
  DRIVER_STATUS_WORK,
  DriverItem
} from "../../../../def/client-types";
import {observer} from "mobx-react";
import {CircularProgress} from "@mui/material";
import RefreshIcon from '@mui/icons-material/Refresh';
import StopIcon from '@mui/icons-material/Stop';

/**
 * Стили формы авторизации.
 */
const useStyles = makeStyles((theme) => ({
  driverItem: {
    // background: "#b5d5a7",
    // background: "red",
    padding: theme.spacing(1),
    minWidth: "180px",
    // flexGrow: 1,
  },
  driverName: {
    color: "black",
    textDecoration: "none",
  },
  "blackButton": {
    color: "black",
  }
}));

/**
 * Пропсы принимаемые функциональной компонентой.
 */
type Props = {
  driver: DriverItem
  runDriver: (id: number) => void
  stopDriver: (id: number) => void
  refreshDriver: (id: number) => void
}

/**
 * Функциональная компонента отображения элемента драйвера.
 *
 * @param props
 */
const Driver: FC<Props>  = (props) => {
  const classes = useStyles();
  let driver: DriverItem = props.driver;

  let onRunStopDriver = () => {
    switch (driver.status) {
      case DRIVER_STATUS_WORK:
        props.stopDriver(driver.id)
        break
      case DRIVER_STATUS_STOP:
        props.runDriver(driver.id)
        break
      default:
        break
    }
  }

  let runStopDriverButton: JSX.Element = <></>;
  switch (driver.status) {
    case DRIVER_STATUS_WORK:
      runStopDriverButton = <PauseIcon/>;
      break
    case DRIVER_STATUS_STOP:
      runStopDriverButton = <PlayArrowIcon/>;
      break
    case DRIVER_STATUS_STARTING:
    case DRIVER_STATUS_STOPPING:
    default:
      runStopDriverButton = <CircularProgress
          size={20}
          sx={{color: (theme) => ("#000000")}}/>
  }

  let bc: string;
  let bgc: string;

  let driverStatusText: string;
  switch (driver.status){
    case DRIVER_STATUS_WORK:
      driverStatusText = "работает"
      bc = "#b5d5a7"
      bgc = "#30af3c"
      break
    case DRIVER_STATUS_STARTING:
    case DRIVER_STATUS_STOPPING:
      driverStatusText = "запускается"
      bc = "#cbb089"
      bgc = "#c7821e"
      break
    case DRIVER_STATUS_STOP:
      driverStatusText = "остановлен"
      bc = "#e89898";
      bgc = "#ce2a27";
      break
    default:
      driverStatusText = "-"
      bc = "#e89898";
      bgc = "#ce2a27";
  }


  return (
    <Grid item xs={12} lg={2} md={3} sm={6}>
      <Box
        className={classes.driverItem} border={2}
        borderColor={bgc} bgcolor={bc}
      >
        <Box display="flex" flexDirection="row">
          <Box p={1} flexGrow={1} alignItems="center">
            {/*<Typography variant="h6" component={NavLink} to={"/driver/" + driver.id} className={classes.driverName}>*/}
            <Typography variant="h6" component={NavLink} to={"/driver/" + driver.id} className={classes.driverName}>
              {driver.name}
            </Typography>
          </Box>
          <Box p={1}>
            <ShareIcon/>
          </Box>
        </Box>
        <div>
          <span>Код драйвера:</span>
          <span>{driver.code}</span>
        </div>
        <div>
          <span>Тип:</span>
          <span>{driver.type}</span>
        </div>
        <div>
          <span>Статус:</span>
          <span>{driverStatusText}</span>
          <IconButton onClick={() => {props.refreshDriver(driver.id)}}>
            <RefreshIcon />
          </IconButton>
        </div>
        <ButtonGroup>
          <IconButton className={classes.blackButton} component={NavLink} to={"/driver/" + driver.id}><CreateIcon/></IconButton>
          <IconButton className={classes.blackButton}><DeleteIcon/></IconButton>
          <IconButton
            className={classes.blackButton}
            onClick={onRunStopDriver}
          >
            {runStopDriverButton}
          </IconButton>
          <IconButton className={classes.blackButton}><StopIcon/></IconButton>
        </ButtonGroup>
      </Box>
    </Grid>
  )
}

export default observer(Driver);