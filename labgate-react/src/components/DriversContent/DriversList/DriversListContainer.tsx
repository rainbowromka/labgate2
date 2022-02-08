import react from "react";
import DriversList from "./DriversList";
import Preloader from "../../Commons/Preloader/Preloader";
import {
  DriversApi
} from "../../../Api";
import {
  DRIVER_STATUS_STARTING, DRIVER_STATUS_STOPPING,
  DriverItem,
  DriversState,
  Principal
} from "../../../def/client-types";
import {AppStoreClass} from "../../../state";
import {inject, observer} from "mobx-react";
import EP_CONNECTION from "../../../entryPoints";
import {IFrame} from "@stomp/stompjs";

type MapStatePropsType = {
  drivers: DriversState
  principal: Principal
}

type MapDispatchPropsType = {
  setIsFetching: (isFetching: boolean) => void
  setDrivers: (
      driverItems: DriverItem[],
      page: number,
      pageSize: number,
      totalElements: number) => void
  setCurrentPage: (currentPage: number) => void
  runDriver: (id: number) => void
  stopDriver: (id: number) => void
}

type AllPropsType = MapDispatchPropsType & MapStatePropsType;

interface InjectedProps extends AllPropsType {
  driversStore: AppStoreClass
}


/**
 * Контейнерная компонента списка драйверов.
 */
@inject('driversStore')
@observer
class DriversListContainer extends react.Component<AllPropsType>
{
  get injected() {
    return this.props as InjectedProps
  }

  constructor(props: AllPropsType) {
    super(props);
    this.onRunStopDriverEntryPoint = this.onRunStopDriverEntryPoint.bind(this);
  }

  /**
   * Вызывается при инициализации компонента, загружает список драйверов.
   */
  componentDidMount() {
    const {driversStore} = this.injected;

    driversStore.setIsFetching(true);
    DriversApi.getDriversListByPageNumber(
        driversStore.drivers.page, driversStore.drivers.pageSize
    ).then(response => {
      driversStore.setDriversList(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
    }).finally(() => {
      driversStore.setIsFetching(false);
    });

    EP_CONNECTION.registerEntryPoints(
      [{
        route: "/driver/onChangeStatus",
        callback: this.onRunStopDriverEntryPoint
      }]);
  }

  /**
   * Загрузить текущую страницу в списке драйверов.
   *
   * @param pageNumber
   *        номер страницы.
   */
  onSetCurrentPage = (pageNumber: number) => {
    const {driversStore} = this.injected;
    driversStore.setIsFetching(true);
    driversStore.setCurrentPage(pageNumber);
    DriversApi.getDriversListByPageNumber(
        pageNumber, driversStore.drivers.pageSize
    ).then(response => {
      driversStore.setDriversList(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
    }).finally(() => {
      driversStore.setIsFetching(false);
    });
  }

  onRunStopDriverEntryPoint = (messages: IFrame) => {
    const {driversStore} = this.injected;
    console.log("onChangeStatusEntryPoint: ")
    console.log(messages?.body);
    let driverItem: DriverItem = JSON.parse(messages?.body);

    if (driverItem)
    {
      driversStore.updateDriverItem(driverItem);
    }
    // {
    // "id":1,
    // "name":"KDLPrime3",
    // "code":"KDLPrime",
    // "type":"SOCKET",
    // "status":"STOP",
    // "parameters":{
    // "kdlprime.port.result":{"id":1,"name":"kdlprime.port.result","value":"2002"}},"driverName":"KDLPrime","driverId":1}
  }

  onRunStopDriver = (id: number, status: string) => {
    const {driversStore} = this.injected;
    driversStore.setStatus(id, status);
    // driversStore.setIsFetching(true);
    DriversApi.runStopDriver(id).then(response => {
      // здесь возможно будет обрабатываться запрос на логическую ошибку
      // отправки запроса серверу. Результат, запуска драйвера придет клиенту
      // по WebSocks.
    }).finally(() => {
      // driversStore.setIsFetching(false);
    });
  }

  onRefreshDriver = (id: number) => {
    const {driversStore} = this.injected;
    DriversApi.getDriversById(id).then(response => {
      let driverItem: DriverItem = response?.data;
      if (driverItem) {
        driversStore.updateDriverItem(driverItem);
      }
    });
  }

  /**
   * Возвращает JSX элемент для отображения списка драйверов.
   * @returns JSX элемент списка драйверов.
   */
  render() {
    const {driversStore} = this.injected;
    return (driversStore.isFetching
        ? <Preloader/>
        : <DriversList
            drivers={driversStore.drivers}
            onSetCurrentPage={this.onSetCurrentPage}
            runDriver={(id) => this.onRunStopDriver(id, DRIVER_STATUS_STARTING)}
            stopDriver={(id) => this.onRunStopDriver(id, DRIVER_STATUS_STOPPING)}
            refreshDriver={this.onRefreshDriver}/>
    );
  }
}

export default DriversListContainer;