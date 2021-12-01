import React from "react";
import {NavLink} from "react-router-dom";
import Box from "@material-ui/core/Box";
import Grid from "@material-ui/core/Grid";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import ChevronLeftIcon from "@material-ui/icons/ChevronLeft";
import {makeStyles} from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  driverName: {
    fontWeight: "bold",
  }
}));


const Driver = (props) => {
  const classes = useStyles();
  let driver = props.driver;
  let parametrs = [];

  for (let key in driver.parameters) {
    let item = driver.parameters[key];
    parametrs.push(<Typography variant="subtitle1">Параметр: {item.name} = {item.value}</Typography>)
  };

  return <div>
    <Grid container>
      <Grid item xs={12}>
        <Box display="flex" flexDirection="row" alignItems="center">
          <Box p={1}>
            <IconButton component={NavLink} to="/drivers">
              <ChevronLeftIcon/>
            </IconButton>
          </Box>
          <Box p={1} flexGrow={1}>
            <Typography className={classes.driverName} variant={"h5"}>Драйвер: {driver.name}</Typography>
          </Box>
        </Box>
      </Grid>
      <Grid item xs={12}>
        <Typography variant="subtitle1">Код драйвера: {driver.code}</Typography>
      </Grid>
      <Grid item xs={12}>
        <Typography variant="subtitle1">Тип драйвера: {driver.type}</Typography>
      </Grid>
      <Grid item xs={12}>
        <Typography variant="subtitle1">Состояние: {driver.state}</Typography>
      </Grid>
      <Grid item xs={12}>
        <Typography variant="h6">Параметры</Typography>
      </Grid>
      <Grid item xs={12}>
        {parametrs}
      </Grid>
    </Grid>
  </div>
}

export default Driver;