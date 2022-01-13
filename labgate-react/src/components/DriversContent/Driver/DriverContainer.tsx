import react from "react";
import Driver from "./Driver";
import axios from "axios";
import {connect} from "react-redux";
import {
  DriverItem,
  DriverState, setDriver,
  setIsFetching
} from "../../../redux/driver-reducer";
import Preloader from "../../Commons/Preloader/Preloader";
import {withRouter} from "react-router-dom";
import {AuthState} from "../../../redux/auth-reducer";
import {AppStateType} from "../../../redux/redux-store";
import {RouteComponentProps} from "react-router/ts4.0";


type MapStatePropsType = {
  auth: AuthState
  driver: DriverState
}

type MapDispatchPropsType = {
  setIsFetching: (isFetching: boolean) => void
  setDriver: (driver: DriverItem) => void
}

type AllPropsType = MapDispatchPropsType & MapStatePropsType & RouteComponentProps<any>

/**
 * Контейнерная компонента драйвера.
 */
class DriverContainer extends react.Component<AllPropsType>
{
  /**
   * Вызывается при инициализации компонента, загружает информацию о драйвере.
   */
  componentDidMount() {
    this.props.setIsFetching(true);
    let driverId = this.props.match.params.driverId;
    axios.get(`http://localhost:8080/services/drivers/list/${driverId}`,
      {
        headers: {
          'Authorization': `${this.props.auth.principal.type} ${this.props.auth.principal.token}`,
          // 'Content-Type': 'application/x-www-form-urlencoded'
        },
      }
    ).then(response => {
      let item = response.data;
      let newItem: DriverItem = {
        id: item.id,
        name: item.name,
        code: item.code,
        type: item.type,
        status: item.status,
        parameters: []
      }
      let parameters = newItem.parameters;

      for (let key in item.parameters) {
        parameters.push(item.parameters[key]);
      }
      this.props.setDriver(newItem)
    }).finally(() => {
      this.props.setIsFetching(false)
    });
  }

  /**
   * Возвращает JSX элемент для отображения драйвера.
   * @returns JSX элемент драйвера.
   */
  render() {
    return (
      this.props.driver.isFetching
        ? <Preloader/>
        : <Driver {...this.props}/>
    );
  }
}

/**
 * Хранилища состояний, которые необходимо передать в пропсах контейнера.
 *
 * @param state
 *        состояние хранилища.
 */
const mapStateToProps = (state: AppStateType): MapStatePropsType => ({
  driver: state.driver,
  auth: state.auth
})

/**
 * Оборачиваем JSX контейнер Redux компонентой для передачи необходимых данных
 * хранилища и объектов событий, которые нужно обрабатывать хранилищем
 * состояния.
 */
export default
  connect<MapStatePropsType, MapDispatchPropsType, any, AppStateType> (
      mapStateToProps,{setDriver, setIsFetching})
  (withRouter(DriverContainer));