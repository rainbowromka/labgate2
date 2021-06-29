import react from "react";
import s from "./PostDriver.module.css";

const PostDriver = (props) => {
  let drivers = props.drivers;

  let driverNameRef = react.createRef();
  let driverCodeRef = react.createRef();
  let driverTypeRef = react.createRef();


  let onAddDriver = () => {
    props.addDriver()
  }

  let onSetDriverName = () => {
    props.setDriverName(driverNameRef.current.value);
  }

  let onSetDriverCode = () => {
    props.setDriverCode(driverCodeRef.current.value);
  }

  let onSetDriverType = () => {
    props.setDriverType(driverTypeRef.current.value);
  }

  return <div className={s.form}>
    <h4>Добавить новый драйвер</h4>
    <div>
      <label>Имя драйвера</label>
      <input onChange={onSetDriverName} ref={driverNameRef} placeholder="Код драйвера" value={drivers.name}/>
    </div>
    <div>
      <label>Код драйвера</label>
      <input onChange={onSetDriverCode} ref={driverCodeRef} rows="1" placeholder="Код драйвера" value={drivers.code}/>
    </div>
    <div>
      <label>Тип соединения:</label>
      <input onChange={onSetDriverType} ref={driverTypeRef} rows="1" placeholder="Код драйвера" value={drivers.type}/>
    </div>
    {/*<div style={{textAlign:"end"}}>*/}
    <div>
      <button onClick={onAddDriver}><b>Добавить драйвер</b></button>
    </div>
  </div>
}

export default PostDriver;