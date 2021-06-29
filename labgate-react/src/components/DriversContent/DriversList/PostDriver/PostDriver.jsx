import react from "react";
import s from "./PostDriver.module.css"
import {
  actionCreatorAddDriver, actionCreatorSetCode,
  actionCreatorSetName, actionCreatorSetType
} from "../../../../redux/state";

const PostDriver = (props) => {
  let store = props.store;
  let driversState = store.getState().drivers;


  let driverNameRef = react.createRef();
  let driverCodeRef = react.createRef();
  let driverTypeRef = react.createRef();


  let onAddDriver = () => {
    store.dispatch(actionCreatorAddDriver({
      name: driverNameRef.current.value,
      code: driverCodeRef.current.value,
      type: driverTypeRef.current.value,
      status: "Остановлен",
    }))
  }

  let onSetDriverName = () => {
    store.dispatch(actionCreatorSetName(driverNameRef.current.value));
  }

  let onSetDriverCode = () => {
    store.dispatch(actionCreatorSetCode(driverCodeRef.current.value));
  }

  let onSetDriverType = () => {
    store.dispatch(actionCreatorSetType(driverTypeRef.current.value));
  }

  return <div className={s.form}>
    <h4>Добавить новый драйвер</h4>
    <div>
      <label>Имя драйвера</label>
      <input onChange={onSetDriverName} ref={driverNameRef} placeholder="Код драйвера" value={driversState.name}/>
    </div>
    <div>
      <label>Код драйвера</label>
      <input onChange={onSetDriverCode} ref={driverCodeRef} rows="1" placeholder="Код драйвера" value={driversState.code}/>
    </div>
    <div>
      <label>Тип соединения:</label>
      <input onChange={onSetDriverType} ref={driverTypeRef} rows="1" placeholder="Код драйвера" value={driversState.type}/>
    </div>
    {/*<div style={{textAlign:"end"}}>*/}
    <div>
      <button onClick={onAddDriver}><b>Добавить драйвер</b></button>
    </div>
  </div>
}

export default PostDriver;