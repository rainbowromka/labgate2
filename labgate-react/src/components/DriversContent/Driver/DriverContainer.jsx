import React from "react";
import Driver from "./Driver";
import * as axios from "axios";
import {connect} from "react-redux";
import {setDriver, setIsFetching} from "../../../redux/driver-reducer";
import Preloader from "../../Commons/Preloader/Preloader";

class DriverContainer extends React.Component
{

  componentDidMount() {
    this.props.setIsFetching(true);
    axios.get(`http://localhost:8080/services/drivers/list/1`).then(response => {
      this.props.setDriver(response.data)
      this.props.setIsFetching(false)
    });
  }

  render() {
    return (
      this.props.driver.isFetching
        ? <Preloader/>
        : <Driver {...this.props}/>
    );
  }
}

const mapStateToProps = (state) => ({
  driver: state.driver,
})

export default connect(mapStateToProps, {setDriver, setIsFetching})(DriverContainer);