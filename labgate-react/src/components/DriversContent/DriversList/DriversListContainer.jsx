import {connect} from "react-redux";
import DriversList from "./DriversList";
import {
  acRunStopDriver, acSetDrivers
} from "../../../redux/drivers-reducer";

let mapStateToProps = (state) => ({
  drivers: state.drivers,
})

let mapDispatchToProps = (dispatch) => {
  return {
    runStopDriver: (id) => {
      dispatch(acRunStopDriver(id))
    },
    setDrivers: (list) => {
      dispatch(acSetDrivers(list));
    }
  }
}

const DriversListContainer = connect(mapStateToProps, mapDispatchToProps)(DriversList);

export default DriversListContainer;