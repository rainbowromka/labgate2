import React from "react";
import s from "./DriverItem.module.css";

const DriverItem = (props) => {
  let driver = props.driver;
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
          <span className={s.driverName}>{driver.status}</span>
        </div>
      </span>
      <span>
        <button>delete</button>
        <button>edit</button>
        <button>run</button>
        <button>stop</button>
      </span>
    </div>
  )
}

export default DriverItem;