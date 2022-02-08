import react from "react";
import Login from "./Login";
import {Redirect} from "react-router-dom";
import {inject, observer} from "mobx-react";
import {AuthData, Principal} from "../../def/client-types";
import {AuthApi} from "../../Api";
import Cookies from "universal-cookie";
import {AppStoreClass} from "../../state";

interface InjectedProps {
  driversStore: AppStoreClass
}

/**
 * Контейнерная компонента формы авторизации.
 */
@inject('driversStore')
@observer
class LoginContainer extends react.Component
{
  get injected() {
    return this.props as InjectedProps
  }

  /**
   * Конутруктор объекта, подготавливем основные методы и объекты контейнерной
   * компоненты.
   *
   * @param props
   *        пропсы передаваемые в компоненту.
   */
  constructor(props: any) {
    super(props);
    this.signIn = this.signIn.bind(this);
    this.signUp = this.signUp.bind(this);
  }

  // componentDidMount()
  // {
  //   APP_STORE.setIsFetching(false);
  //   AuthApi.GetUserInfo().then( response => {
  //     if (response && response.data && response.data.principal) {
  //       let respPrincipal: any = response.data.principal
  //       APP_STORE.setUserData({
  //             id: respPrincipal.id,
  //             username: respPrincipal.username,
  //             email: respPrincipal.email,
  //             roles: respPrincipal.roles
  //           }, true
  //       );
  //     }
  //   }).finally(() => {
  //     APP_STORE.setIsFetching(false);
  //   })
  // }

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
    const {driversStore} = this.injected;
    driversStore.setIsFetching(true);
    AuthApi.ApiSiginIn(username, password).then(response => {
      if (response && response.data) {
        console.log(response)
        let principal = response.data as Principal;
        driversStore.setUserData(principal, true);
      }
    }
    ).finally(() => {
      driversStore.setIsFetching(false)
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
    const {driversStore} = this.injected;
    driversStore.setIsFetching(true);
    AuthApi.ApiSignUp(authData).then(response => {
      if (response && response.data && callback) {
        callback();
        // this.props.setUserData(response.data, true)
      }
    }).finally(() => {
      driversStore.setIsFetching(false);
    })
  }

  /**
   * Возвращает JSX элемент для отображения формы авторизации/регистрации
   * пользователя.
   *
   * @returns JSX элемент формы авторизации/регистрации пользователя.
   */
  render() {
    const {driversStore} = this.injected;
    return driversStore.authState.isAuthorized
      ? <Redirect to="/drivers"/>
      : <Login
            authState={driversStore.authState}
            isFetching={driversStore.isFetching}
            signIn={this.signIn}
            signUp={this.signUp}/>
  }
}

export default LoginContainer;