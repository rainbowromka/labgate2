import react from "react";
import Driver from "./Driver";
import Preloader from "../../Commons/Preloader/Preloader";
import {withRouter} from "react-router-dom";
import {DriverItem} from "../../../def/client-types";
import {RouteComponentProps} from "react-router/ts4.0";
import {observer} from 'mobx-react';
import {DriversApi} from "../../../Api";
import {APP_STORE} from "../../../state";

type AllPropsType = RouteComponentProps<any>

/**
 * Контейнерная компонента драйвера.
 */
@observer
class DriverContainer extends react.Component<AllPropsType>
{
  /**
   * Вызывается при инициализации компонента, загружает информацию о драйвере.
   */
  componentDidMount() {
    APP_STORE.setIsFetching(true);
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
      APP_STORE.setDriver(newItem)
    }).finally(() => {
      APP_STORE.setIsFetching(false)
    });
  }

  /**
   * Возвращает JSX элемент для отображения драйвера.
   * @returns JSX элемент драйвера.
   */
  render() {
    return (
        APP_STORE.isFetching
        ? <Preloader/>
        : <Driver driver={APP_STORE.driver}/>
    );
  }
}

export default withRouter(DriverContainer);