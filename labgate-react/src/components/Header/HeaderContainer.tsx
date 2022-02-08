import react from "react";
import {inject, observer} from "mobx-react";
import {AuthApi} from "../../Api";
import Header from "./Header";
import {AppStoreClass} from "../../state";
import Cookies from 'universal-cookie';

interface InjectedProps {
    driversStore: AppStoreClass
}

/**
 * Контейнерная компонента заголовка страницы.
 */
@inject('driversStore')
@observer
class HeaderContainer extends react.Component {
    get injected() {
        return this.props as InjectedProps
    }

    /**
     * Вызывается когда компонента была создана и первый раз пытается
     * отобразиться. Загружает данные об авторизированном пользователе.
     */
    componentDidMount() {
        let cookies = new Cookies();
        let token = cookies.get("Token");
        const {driversStore} = this.injected;
        if (token) {
            console.log("Token: " + token)
        } else {
            console.log("Not Authorized");
        }
        AuthApi.GetUserInfo().then(response => {
            if (response && response.data)
            {
                let respPrincipal: any = response.data
                driversStore.setUserData({
                        // token: respPrincipal.token,
                        // type: respPrincipal.type,
                        id: respPrincipal.id,
                        username: respPrincipal.username,
                        email: respPrincipal.email,
                        roles: respPrincipal.roles
                    }, Boolean(token)
                );
            }
        }).finally(() => {
            driversStore.setIsFetching(false);
        });
    }

  /**
   * Вовращает JSX элемент отображения заголовка.
   * @returns JSX элемент отображения заголовка.
   */
  render() {
      const {driversStore} = this.injected

      return <Header
          authState={driversStore.authState}
          open={driversStore.openLeftPanel}
          setOpen={driversStore.setOpenLeftPanel}/>
  }
}

/**
 * Оборачиваем JSX контейнер Redux компонентой для передачи необходимых данных
 * хранилища и объектов событий, которые нужно обрабатывать хранилищем
 * состояния.
 */
export default HeaderContainer;