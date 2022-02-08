import react from "react";
import Driver from "./Driver";
import Preloader from "../../Commons/Preloader/Preloader";
import {withRouter} from "react-router-dom";
import {DriverItem} from "../../../def/client-types";
import {RouteComponentProps} from "react-router/ts4.0";
import {inject, observer} from 'mobx-react';
import {DriversApi} from "../../../Api";
import {AppStoreClass} from "../../../state";

type AllPropsType = RouteComponentProps<any>

interface InjectedProps extends AllPropsType{
  driversStore: AppStoreClass
}



/**
 * Контейнерная компонента драйвера.
 */
@inject('driversStore')
@observer
class DriverContainer extends react.Component<AllPropsType>
{
  get injected() {
    return this.props as InjectedProps
  }

  /**
   * Вызывается при инициализации компонента, загружает информацию о драйвере.
   */
  componentDidMount() {
    const {driversStore} = this.injected;

    driversStore.setIsFetching(true);
    let driverId = this.props.match.params.driverId;
    DriversApi.getDriversById(driverId).then(response => {
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
      driversStore.setDriverItem(newItem)
    }).finally(() => {
      driversStore.setIsFetching(false)
    });
  }

  /**
   * Возвращает JSX элемент для отображения драйвера.
   * @returns JSX элемент драйвера.
   */
  render() {
    const {driversStore} = this.injected;

    return (
        driversStore.isFetching
        ? <Preloader/>
        : <Driver driver={driversStore.driver}/>
    );
  }
}

export default withRouter(DriverContainer);