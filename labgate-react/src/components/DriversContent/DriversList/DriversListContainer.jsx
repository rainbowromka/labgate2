import {connect} from "react-redux";
import DriversList from "./DriversList";
import {
  acRunStopDriver, acSetCurrentPage, acSetDrivers, acSetIsFetchting
} from "../../../redux/drivers-reducer";

let mapStateToProps = (state) => ({
  drivers: state.drivers,
})

let mapDispatchToProps = (dispatch) => {
  return {
    runStopDriver: (id) => {
      dispatch(acRunStopDriver(id))
    },
    setDrivers: (list, page, pageSize, totalElements) => {
      dispatch(acSetDrivers(list, page, pageSize, totalElements));
    },
    setCurrentPage: (page) => {
      dispatch(acSetCurrentPage(page));
    },
    setIsFetching: (isFetching) => {
      dispatch(acSetIsFetchting(isFetching));
    }
  }
}

const DriversListContainer = connect(mapStateToProps, mapDispatchToProps)(DriversList);

export default DriversListContainer;