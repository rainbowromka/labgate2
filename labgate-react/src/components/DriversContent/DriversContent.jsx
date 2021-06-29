import React from "react";
import s from "./DriversContent.module.css";
import DriversList from "./DriversList/DriversList";

const DriversContent = (props) => {
  let store = props.store;

  return (
      <div className={s.driverContent}>
        <DriversList store={store}/>
      </div>
  )
}

export default DriversContent;