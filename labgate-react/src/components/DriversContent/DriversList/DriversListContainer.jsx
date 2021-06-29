import {connect} from "react-redux";
import DriversList from "./DriversList";

let mapStateToProps = (state) => ({
  drivers: state.drivers,
})

let mapDispatchToProps = (dispatch) => {
  return {
  }
}

const DriversListContainer = connect(mapStateToProps, mapDispatchToProps)(DriversList);

export default DriversListContainer;