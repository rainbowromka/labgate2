import react from "react";
import Login from "./Login";
import {Redirect} from "react-router-dom";
import {observer} from "mobx-react";
import {AuthData} from "../../def/client-types";
import {APP_STORE, IAppStoreProps} from "../../state";
import {AuthApi} from "../../Api";

/**
 * Контейнерная компонента формы авторизации.
 */
@observer
class LoginContainer extends react.Component<IAppStoreProps>
{
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
    APP_STORE.setIsFetching(true);
    AuthApi.ApiSiginIn(username, password).then(response => {
      if (response && response.data) {
        APP_STORE.setUserData(response.data, true);
      }
    }
    ).finally(() => {
      APP_STORE.setIsFetching(false)
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
    APP_STORE.setIsFetching(true);
    AuthApi.ApiSignUp(authData).then(response => {
      if (response && response.data && callback) {
        callback();
        // this.props.setUserData(response.data, true)
      }
    }).finally(() => {
      APP_STORE.setIsFetching(false);
    })
  }

  /**
   * Возвращает JSX элемент для отображения формы авторизации/регистрации
   * пользователя.
   *
   * @returns JSX элемент формы авторизации/регистрации пользователя.
   */
  render() {
    return APP_STORE.auth.isAuthorized
      ? <Redirect to="/drivers"/>
      : <Login
            auth={APP_STORE.auth}
            isFetching={APP_STORE.isFetching}
            signIn={this.signIn}
            signUp={this.signUp}/>
  }

}

export default LoginContainer;