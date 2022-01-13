import react from "react";
import {connect} from "react-redux";
import DriversList from "./DriversList";
import {
  DriversState,
  runStopDriver,
  setCurrentPage,
  setDrivers,
  SetDriversParamAction,
  setIsFetching
} from "../../../redux/drivers-reducer";
import axios from "axios";
import Preloader from "../../Commons/Preloader/Preloader";
import {AuthState} from "../../../redux/auth-reducer";
import {DriverItem} from "../../../redux/driver-reducer";
import {AppStateType} from "../../../redux/redux-store";

type MapStatePropsType = {
  drivers: DriversState
  auth: AuthState
}

type MapDispatchPropsType = {
  setIsFetching: (isFetching: boolean) => void
  setDrivers: (
      driverItems: DriverItem[],
      page: number,
      pageSize: number,
      totalElements: number) => void
  setCurrentPage: (currentPage: number) => void
  runStopDriver: (id: number) => void
}

type AllPropsType = MapDispatchPropsType & MapStatePropsType;
/**
 * Контейнерная компонента списка драйверов.
 */
class DriversListContainer extends react.Component<AllPropsType>
{
  /**
   * Вызывается при инициализации компонента, загружает список драйверов.
   */
  componentDidMount() {
    this.props.setIsFetching(true);
    axios.get(`http://localhost:8080/services/drivers/list?page=${this.props.drivers.page}&size=${this.props.drivers.pageSize}`,
      {
        headers: {
          'Authorization': `${this.props.auth.principal.type} ${this.props.auth.principal.token}`,
          // 'Content-Type': 'application/x-www-form-urlencoded'
        },
      }
    ).then(response => {
      this.props.setDrivers(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
    }).finally(() => {
      this.props.setIsFetching(false);
    });
  }

  /**
   * Загрузить текущую страницу в списке драйверов.
   *
   * @param pageNumber
   *        номер страницы.
   */
  onSetCurrentPage = (pageNumber: number) => {
    this.props.setIsFetching(true);
    this.props.setCurrentPage(pageNumber);
    axios.get(`http://localhost:8080/services/drivers/list?page=${pageNumber}&size=${this.props.drivers.pageSize}`,
      {
        headers: {
          'Authorization': `${this.props.auth.principal.type} ${this.props.auth.principal.token}`,
          // 'Content-Type': 'application/x-www-form-urlencoded'
        },
      }
    ).then(response => {
      this.props.setDrivers(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
    }).finally(() => {
      this.props.setIsFetching(false);
    });
  }

  /**
   * Возвращает JSX элемент для отображения списка драйверов.
   * @returns JSX элемент списка драйверов.
   */
  render() {
    return ( this.props.drivers.isFetching
        ? <Preloader/>
        : <DriversList
            {...this.props}
            onSetCurrentPage={this.onSetCurrentPage}
            runStopDriver={this.props.runStopDriver}/>
    );
  }
}

let mapStateToProps = (state: AppStateType): MapStatePropsType => ({
  drivers: state.drivers,
  auth: state.auth,
})

export default
  connect<MapStatePropsType, MapDispatchPropsType, any, AppStateType>(
      mapStateToProps,
      { runStopDriver, setDrivers, setCurrentPage, setIsFetching})
  (DriversListContainer);