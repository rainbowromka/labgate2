import React from "react";
import Driver from "./Driver";
import axios from "axios";
import {connect} from "react-redux";
import {setDriver, setIsFetching} from "../../../redux/driver-reducer";
import Preloader from "../../Commons/Preloader/Preloader";
import {withRouter} from "react-router-dom";

/**
 * Контейнерная компонента драйвера.
 */
class DriverContainer extends React.Component
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
          'Authorization': `${this.props.auth.type} ${this.props.auth.token}`,
          // 'Content-Type': 'application/x-www-form-urlencoded'
        },
      }
    ).then(response => {
      this.props.setDriver(response.data)
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
const mapStateToProps = (state) => ({
  driver: state.driver,
  auth: state.auth
})

/**
 * Оборачиваем JSX контейнер Redux компонентой для передачи необходимых данных
 * хранилища и объектов событий, которые нужно обрабатывать хранилищем
 * состояния.
 */
export default connect(mapStateToProps, {setDriver, setIsFetching})(
  withRouter(DriverContainer));