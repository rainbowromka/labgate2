import react from "react";
import {connect} from "react-redux";
import Login, {AuthData} from "./Login";
import axios from "axios";
import {
  AuthState,
  Principal,
  setIsFetching,
  setUserData
} from "../../redux/auth-reducer";
import {Redirect, withRouter} from "react-router-dom";
import {AppStateType} from "../../redux/redux-store";

type StatePropsType = {
  auth: AuthState
}

type DispatchPropsType = {
  setIsFetching: (isFetching: boolean) => void
  setUserData: (authState: Principal, isAuthorized: boolean) => void
}

type AllPropsType = DispatchPropsType & StatePropsType

/**
 * Контейнерная компонента формы авторизации.
 */
class LoginContainer extends react.Component<AllPropsType>
{
  /**
   * Конутруктор объекта, подготавливем основные методы и объекты контейнерной
   * компоненты.
   *
   * @param props
   *        пропсы передаваемые в компоненту.
   */
  constructor(props: AllPropsType) {
    super(props);
    this.signIn = this.signIn.bind(this);
    this.signUp = this.signUp.bind(this);
  }

  /**
   * Авторизация пользователя на сервере путем отправки REST-запроса.
   *
   * @param username
   *        имя пользователя.
   * @param password
   *        пароль.
   */
  signIn(username: string, password: string)
  {
    this.props.setIsFetching(true);
    axios.post(
      "http://localhost:8080/api/auth/signin",
      {username: username, password: password},
      {
        // withCredentials: true
      }
    ).then(response => {
      if (response && response.data) {
        this.props.setUserData(response.data, true);
      }
    }
    ).finally(() => {
      this.props.setIsFetching(false)
    });
  }

  /**
   * Регистрация пользователя на сервере, путем отправки REST-запроса.
   *
   * @param authData
   *        объект авторизации.
   * @param callback
   *        callback-метод, вызывается в случае удачной авторизации.
   */
  signUp(authData: AuthData, callback: () => void)
  {
    this.props.setIsFetching(true);
    axios.post(
      "http://localhost:8080/api/auth/signup",
      {
        username: authData.username,
        email: authData.email,
        password: authData.password,
        role: ["mod", "user"],
      },
      {}
    ).then(response => {
      if (response && response.data && callback) {
        callback();
        // this.props.setUserData(response.data, true)
      }
    }).finally(() => {
      this.props.setIsFetching(false);
    })
  }

  /**
   * Возвращает JSX элемент для отображения формы авторизации/регистрации
   * пользователя.
   *
   * @returns JSX элемент формы авторизации/регистрации пользователя.
   */
  render() {
    return this.props.auth.isAuthorized
      ? <Redirect to="/drivers"/>
      : <Login signIn={this.signIn}
               signUp={this.signUp}
               {...this.props}/>
  }

}

/**
 * Хранилища состояний, которые необходимо передать в пропсах контейнера.
 *
 * @param state
 *        состояние хранилища.
 */
const mapStateToProps = (stat: AppStateType) => ({
  auth: stat.auth
})

/**
 * Оборачиваем JSX контейнер Redux компонентой для передачи необходимых данных
 * хранилища и объектов событий, которые нужно обрабатывать хранилищем
 * состояния.
 */
export default connect<StatePropsType, DispatchPropsType, any, AppStateType>(
  mapStateToProps, {setIsFetching, setUserData}
)(LoginContainer);