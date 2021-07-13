import React from "react";
import DriverItem from "./DriverItem/DriverItem";
import PostDriverContainer from "./PostDriver/PostDriverContainer";
import * as axios from 'axios';

const DriversList = (store) => {
  let drivers = store.drivers;

  if (drivers.list.length === 0) {
    axios.get("http://localhost:8080/api/driverEntities").then(response => {      
      store.setDrivers(response.data._embedded.driverEntities);
    });
  }


  let driverElements = drivers.list.map (item => <DriverItem
    key={item._links.self.href}
    driver={item}
    runStopDriver={store.runStopDriver}
  />);

  return (
    <>
      <PostDriverContainer store={store}/>
      {driverElements}
    </>
  )

}

export default DriversList;