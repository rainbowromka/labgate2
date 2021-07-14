import React from "react";
import DriverItem from "./DriverItem/DriverItem";
import PostDriverContainer from "./PostDriver/PostDriverContainer";
import * as axios from 'axios';

class DriversList extends React.Component {

  componentDidMount() {
    axios.get("http://localhost:8080/services/drivers/list").then(response => {
      this.props.setDrivers(response.data.content);
    });
  }

  render() {

    let driverElements = this.props.drivers.list.map (
      item => <DriverItem
        key={item.id}
        driver={item}
        runStopDriver={this.props.runStopDriver}/>
    );

    return (
      <>
        <PostDriverContainer store={this.props}/>
        {driverElements}
      </>
    )
  }
}

export default DriversList;