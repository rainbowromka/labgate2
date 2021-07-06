import React from "react";
import DriverItem from "./DriverItem/DriverItem";
import PostDriverContainer from "./PostDriver/PostDriverContainer";
import {
  DRIVER_STATUS_STOP,
  DRIVER_STATUS_WORK
} from "../../../redux/drivers-reducer";

const DriversList = (store) => {
  let drivers = store.drivers;

  if (drivers.list.length === 0) {
    store.setDrivers(
      [{
        id: 1,
        name: "KDLPrime3",
        code: "KDLPrime",
        type: "SOCKET",
        status: DRIVER_STATUS_WORK,
      }, {
        id: 2,
        name: "KDLMax2",
        code: "KDLMax",
        type: "SOCKET",
        status: DRIVER_STATUS_STOP,
      }, {
        id: 3,
        name: "CITM1",
        code: "CITM",
        type: "TTY",
        status: DRIVER_STATUS_WORK,
      }]
    )
  }


  let driverElements = drivers.list.map (item => <DriverItem
    key={item.id}
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