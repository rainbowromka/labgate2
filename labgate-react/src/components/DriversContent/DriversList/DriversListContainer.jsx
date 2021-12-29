import React from "react";
import {connect} from "react-redux";
import DriversList from "./DriversList";
import {
  runStopDriver, setCurrentPage, setDrivers, setIsFetching
} from "../../../redux/drivers-reducer";
import * as axios from "axios";
import Preloader from "../../Commons/Preloader/Preloader";

/**
 * Контейнерная компонента списка драйверов.
 */
class DriversListContainer extends React.Component
{
  /**
   * Вызывается при инициализации компонента, загружает список драйверов.
   */
  componentDidMount() {
    this.props.setIsFetching(true);
    axios.get(`http://localhost:8080/services/drivers/list?page=${this.props.drivers.page}&size=${this.props.drivers.pageSize}`,
      {
        headers: {
          'Authorization': `${this.props.auth.type} ${this.props.auth.token}`,
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
  onSetCurrentPage = (pageNumber) => {
    this.props.setIsFetching(true);
    this.props.setCurrentPage(pageNumber);
    axios.get(`http://localhost:8080/services/drivers/list?page=${pageNumber}&size=${this.props.drivers.pageSize}`,
      {
        headers: {
          'Authorization': `${this.props.auth.type} ${this.props.auth.token}`,
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
        : <DriversList {...this.props} onSetCurrentPage={this.onSetCurrentPage}/>
    );
  }
}

let mapStateToProps = (state) => ({
  drivers: state.drivers,
  auth: state.auth,
})

export default connect(mapStateToProps,
  { runStopDriver, setDrivers, setCurrentPage, setIsFetching}
)(DriversListContainer);