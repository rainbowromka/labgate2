import react from "react";
import DriversList from "./DriversList";
import Preloader from "../../Commons/Preloader/Preloader";
import {
  DriversApi
} from "../../../Api";
import {AuthState, DriverItem, DriversState} from "../../../def/client-types";
import {APP_STORE} from "../../../state";
import {observer} from "mobx-react";

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
@observer
class DriversListContainer extends react.Component<AllPropsType>
{
  /**
   * Вызывается при инициализации компонента, загружает список драйверов.
   */
  componentDidMount() {
    APP_STORE.setIsFetching(true);
    DriversApi.getDriversListByPageNumber(APP_STORE.drivers.page).then(response => {
      APP_STORE.setDrivers(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
    }).finally(() => {
      APP_STORE.setIsFetching(false);
    });
  }

  /**
   * Загрузить текущую страницу в списке драйверов.
   *
   * @param pageNumber
   *        номер страницы.
   */
  onSetCurrentPage = (pageNumber: number) => {
    APP_STORE.setIsFetching(true);
    APP_STORE.setCurrentPage(pageNumber);
    DriversApi.getDriversListByPageNumber(pageNumber).then(response => {
      APP_STORE.setDrivers(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
    }).finally(() => {
      APP_STORE.setIsFetching(false);
    });
  }

  onRunStopDriver = (id: number) => {
    // DriversApi.runStopDriver(id).then(response => {
    //   // APP_STORE.runStopDriver(id, response.)
    // })
  }

  /**
   * Возвращает JSX элемент для отображения списка драйверов.
   * @returns JSX элемент списка драйверов.
   */
  render() {
    return (APP_STORE.isFetching
        ? <Preloader/>
        : <DriversList
            drivers={APP_STORE.drivers}
            onSetCurrentPage={this.onSetCurrentPage}
            runStopDriver={APP_STORE.runStopDriver}/>
    );
  }
}

export default DriversListContainer;