import react from "react";
import {observer} from "mobx-react";
import {APP_STORE} from "../../state";
import Navbar from "./Navbar";

/**
 * Контейнерная компонента заголовка страницы.
 */
@observer
class NavbarContainer extends react.Component{

  /**
   * Вовращает JSX элемент отображения заголовка.
   * @returns JSX элемент отображения заголовка.
   */
  render() {
    return <Navbar
        open={APP_STORE.openLeftPanel}
        setOpen={APP_STORE.setOpenLeftPanel}/>
  }
}

/**
 * Оборачиваем JSX контейнер Redux компонентой для передачи необходимых данных
 * хранилища и объектов событий, которые нужно обрабатывать хранилищем
 * состояния.
 */
export default NavbarContainer;