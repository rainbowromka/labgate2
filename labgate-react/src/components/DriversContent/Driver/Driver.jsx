import React from "react";
import s from "../DriversList/DriverItem/DriverItem.module.css";
import {NavLink} from "react-router-dom";

const Driver = (props) => {
  let driver = props.driver;

  let parametrs = [];

  for (let key in driver.parameters) {
    let item = driver.parameters[key];
    parametrs.push(<>
      <div>Параметр: {item.name} = {item.value}</div>
    </>)
  };

  return <div>
    <NavLink to="/drivers"><button>{"<"}</button></NavLink>
    <h1>Драйвер: <span className={s.driverName}>{driver.name}</span></h1>
    <div>Код драйвера: {driver.code}</div>
    <div>Тип драйвера: {driver.type}</div>
    <div>Состояние: {driver.state}</div>
    <h4>Параметры:</h4>
    {parametrs}
  </div>
}

export default Driver;