import react from "react";
import Header, {OpenPanelType} from "./Header";
import axios from "axios";
import {connect} from "react-redux";
import {
  setUserData,
  setIsFetching,
  AuthState,
  Principal
} from "../../redux/auth-reducer";
import {AppStateType} from "../../redux/redux-store";

type MapStatePropsType = {
  auth: AuthState
}

type MapDispatchType = {
  setUserData: (principal: Principal, authorized: boolean) => void
  setIsFetching: (isFetching: boolean) => void
}

type AllProps = MapStatePropsType & MapDispatchType & OpenPanelType

/**
 * Контейнерная компонента заголовка страницы.
 */
class HeaderContainer extends react.Component<AllProps> {
  /**
   * Вызывается когда компонента была создана и первый раз пытается
   * отобразиться. Загружает данные об авторизированном пользователе.
   */
  componentDidMount() {
    if (this.props.auth.isAuthorized) {
      this.props.setIsFetching(true);
      let principal: Principal = this.props.auth.principal;

      axios.get(`http://localhost:8080/api/auth/info`,
        {
          headers: {
            Authorization: "Bearer " + principal.token
          }
        }
      ).then(response => {
        if (response && response.data && response.data.principal) {
          let respPrincipal: any = response.data.principal
          this.props.setUserData({
                token: respPrincipal.token,
                type: respPrincipal.type,
                id: respPrincipal.id,
                username: respPrincipal.username,
                email: respPrincipal.email,
                roles: respPrincipal.roles
              }, true
          );
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
const mapStateToProps = (state: AppStateType): MapStatePropsType => ({
  auth: state.auth,
})

/**
 * Оборачиваем JSX контейнер Redux компонентой для передачи необходимых данных
 * хранилища и объектов событий, которые нужно обрабатывать хранилищем
 * состояния.
 */
export default
  connect<MapStatePropsType, MapDispatchType, OpenPanelType, AppStateType>(
      mapStateToProps,
      {setUserData, setIsFetching})
  (HeaderContainer);