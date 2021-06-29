import React from "react";
import s from "./DriversContent.module.css";
import DriversListContainer from "./DriversList/DriversListContainer";

const DriversContent = () => {
  return (
      <div className={s.driverContent}>
        <DriversListContainer/>
      </div>
  )
}

export default DriversContent;