import React from "react";
import Header from "./Header";
import * as axios from "axios";
import {connect} from "react-redux";
import {setUserData, setIsFetching} from "../../redux/auth-reducer";

/**
 * Контейнерная компонента заголовка страницы.
 */
class HeaderContainer extends React.Component {
  /**
   * Вызывается когда компонента была создана и первый раз пытается
   * отобразиться. Загружает данные об авторизированном пользователе.
   */
  componentDidMount() {
    if (this.props.auth.isAuth) {
      this.props.setIsFetching(true);
      axios.get(`http://localhost:8080/api/auth/info`,
        {
          headers: {
            Authorization: "Bearer " + this.props.auth.token
          }
        }
      ).then(response => {
        if (response && response.data && response.data.principal) {
          this.props.setUserData(response.data.principal);
        }
        this.props.setIsFetching(false);
      });
    }
  }

  /**
   * Вовращает JSX элемент отображения заголовка.
   * @returns JSX элемент отображения заголовка.
   */
  render() {
    return <Header {...this.props}/>
  }
}

/**
 * Хранилища состояний, которые необходимо передать в пропсах контейнера.
 * @param state
 *        состояние хранилища.
 */
const mapStateToProps = (state) => ({
  auth: state.auth,
})

/**
 * Оборачиваем JSX контейнер Redux компонентой для передачи необходимых данных
 * хранилища и объектов событий, которые нужно обрабатывать хранилищем
 * состояния.
 */
export default connect(mapStateToProps, {setUserData, setIsFetching})(HeaderContainer);