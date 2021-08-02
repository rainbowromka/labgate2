import React from "react";
import {NavLink} from "react-router-dom";
import {DRIVER_STATUS_WORK} from "../../../../redux/drivers-reducer";
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


const useStyles = makeStyles((theme) => ({
  driverItem: {
    // background: "#b5d5a7",
    // background: "red",
    padding: theme.spacing(1),
    minwidth: "180px",
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

const DriverItem = (props) => {
  const classes = useStyles();
  let driver = props.driver;

  let onRunStopDriver = () => {
    props.runStopDriver(driver.id)
  }

  let isWork = driver.status===DRIVER_STATUS_WORK
  let bc = isWork ? "#b5d5a7" : "#e89898";
  let bgc = isWork ? "#30af3c" : "#ce2a27";

  return (
    <Grid item xs={12} lg={2} md={3} sm={6}>
      <Box
        className={classes.driverItem} border={2}
        borderColor={bgc} bgcolor={bc}
      >
        <Box display="flex" flexDirection="row">
          <Box p={1} flexGrow={1} alignItems="center">
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
          <span>{driver.status===DRIVER_STATUS_WORK ? "работает" : "остановлен"}</span>
        </div>
        <ButtonGroup>
          <IconButton className={classes.blackButton}><CreateIcon/></IconButton>
          <IconButton className={classes.blackButton}><DeleteIcon/></IconButton>
          <IconButton
            className={classes.blackButton}
            onClick={onRunStopDriver}
          >
            {driver.status===DRIVER_STATUS_WORK ? <PauseIcon/> : <PlayArrowIcon/>}
          </IconButton>
        </ButtonGroup>
      </Box>
    </Grid>
  )
}

export default DriverItem;