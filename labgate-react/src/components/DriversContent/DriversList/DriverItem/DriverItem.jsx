import React from "react";
import s from "./DriverItem.module.css";
import {DRIVER_STATUS_WORK} from "../../../../redux/drivers-reducer";

const DriverItem = (props) => {
  let driver = props.driver;

  let onRunStopDriver = () => {
    props.runStopDriver(driver.id)
  }

  return (
    <div className="driverItem">
      <span>
        <div>
          <span>Драйвер:</span>
          <span className={s.driverName}>{driver.name}</span>
        </div>
        <div>
          <span>Код драйвера:</span>
          <span className={s.driverName}>{driver.code}</span>
        </div>
        <div>
          <span>Тип:</span>
          <span className={s.driverName}>{driver.type}</span>
        </div>
        <div>
          <span>Статус:</span>
          <span className={s.driverName}>{driver.status==DRIVER_STATUS_WORK ? "работает" : "остановлен"}</span>
        </div>
      </span>
      <span>
        <button>delete</button>
        <button>edit</button>
        <button onClick={onRunStopDriver}>{driver.status===DRIVER_STATUS_WORK ? "stop" : "run"}</button>
      </span>
    </div>
  )
}

export default DriverItem;