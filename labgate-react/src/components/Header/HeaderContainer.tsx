import react from "react";
import Header, {OpenPanelType} from "./Header";
import {Principal} from "../../def/client-types";
import {observer} from "mobx-react";
import {GetUserInfo} from "../../Api";
import {APP_STORE} from "../../state";

/**
 * Контейнерная компонента заголовка страницы.
 */
@observer
class HeaderContainer extends react.Component{

    /**
     * Вызывается когда компонента была создана и первый раз пытается
     * отобразиться. Загружает данные об авторизированном пользователе.
     */
    componentDidMount() {
        if (APP_STORE.auth.isAuthorized) {
            APP_STORE.setIsFetching(true);
            let principal: Principal = APP_STORE.auth.principal;

            GetUserInfo(principal.token).then(response => {
                if (response && response.data && response.data.principal) {
                    let respPrincipal: any = response.data.principal
                    APP_STORE.setUserData({
                            token: respPrincipal.token,
                            type: respPrincipal.type,
                            id: respPrincipal.id,
                            username: respPrincipal.username,
                            email: respPrincipal.email,
                            roles: respPrincipal.roles
                        }, true
                    );
                }
                APP_STORE.setIsFetching(false);
            });
        }
    }

  /**
   * Вовращает JSX элемент отображения заголовка.
   * @returns JSX элемент отображения заголовка.
   */
  render() {
    return <Header
        open={APP_STORE.openLeftPanel}
        setOpen={APP_STORE.setOpenLeftPanel}/>
  }
}

/**
 * Оборачиваем JSX контейнер Redux компонентой для передачи необходимых данных
 * хранилища и объектов событий, которые нужно обрабатывать хранилищем
 * состояния.
 */
export default HeaderContainer;