import React from "react";
import {connect} from "react-redux";
import DriversList from "./DriversList";
import {
  runStopDriver, setCurrentPage, setDrivers, setIsFetching
} from "../../../redux/drivers-reducer";
import * as axios from "axios";
import Preloader from "../../Commons/Preloader/Preloader";

class DriversListContainer extends React.Component
{
  componentDidMount() {
    this.props.setIsFetching(true);
    axios.get(`http://localhost:8080/services/drivers/list?page=${this.props.drivers.page}&size=${this.props.drivers.pageSize}`).then(response => {
      this.props.setDrivers(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
      this.props.setIsFetching(false);
    });
  }

  onSetCurrentPage = (pageNumber) => {
    this.props.setIsFetching(true);
    this.props.setCurrentPage(pageNumber);
    axios.get(`http://localhost:8080/services/drivers/list?page=${pageNumber}&size=${this.props.drivers.pageSize}`).then(response => {
      this.props.setDrivers(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
      this.props.setIsFetching(false);
    });
  }

  render() {
    return ( this.props.drivers.isFetching
        ? <Preloader/>
        : <DriversList {...this.props} onSetCurrentPage={this.onSetCurrentPage}/>
    );
  }

}

let mapStateToProps = (state) => ({
  drivers: state.drivers,
})

export default connect(mapStateToProps,
  { runStopDriver, setDrivers, setCurrentPage, setIsFetching}
)(DriversListContainer);