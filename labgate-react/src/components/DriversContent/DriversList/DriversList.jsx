import React from "react";
import DriverItem from "./DriverItem/DriverItem";
import PostDriver from "./PostDriver/PostDriver";

const DriversList = (props) => {
  let store = props.store;
  let driverState = store.getState().drivers;

  let driverElements = driverState.list.map (item => <DriverItem driver={item}/>);

  return (
    <>
      <PostDriver store={store}/>
      {driverElements}
    </>
  )

}

export default DriversList;