import react from "react";
import DriversList from "./DriversList";
import Preloader from "../../Commons/Preloader/Preloader";
import {
  DriversApi
} from "../../../Api";
import {AuthState, DriverItem, DriversState} from "../../../def/client-types";
import {APP_STORE} from "../../../state";
import {observer} from "mobx-react";
import {registerEntryPoints} from "../../../entryPoints";
import {IFrame} from "@stomp/stompjs";

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
  constructor(props: AllPropsType) {
    super(props);
    this.onRunStopDriverEntryPoint = this.onRunStopDriverEntryPoint.bind(this);
  }

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

    registerEntryPoints([
      {route: "/driver", callback: this.onRunStopDriverEntryPoint}
    ]);
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

  onRunStopDriverEntryPoint = (messages: IFrame) => {
    console.log(messages.body);
  }

  onRunStopDriver = (id: number) => {
    DriversApi.runStopDriver(id).then(response => {
      APP_STORE.runStopDriver(id, response.data)
    })
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
            runStopDriver={this.onRunStopDriver}/>
    );
  }
}

export default DriversListContainer;