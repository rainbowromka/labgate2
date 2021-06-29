import React from "react";
import DriverItem from "./DriverItem/DriverItem";
import PostDriverContainer from "./PostDriver/PostDriverContainer";

const DriversList = (store) => {
  let drivers = store.drivers;

  let driverElements = drivers.list.map (item => <DriverItem driver={item}/>);

  return (
    <>
      <PostDriverContainer store={store}/>
      {driverElements}
    </>
  )

}

export default DriversList;