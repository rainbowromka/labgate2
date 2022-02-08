import react from "react";
import {inject, observer} from "mobx-react";
import Navbar from "./Navbar";
import {AppStoreClass} from "../../state";

interface InjectedProps {
  driversStore: AppStoreClass
}

/**
 * Контейнерная компонента заголовка страницы.
 */
@inject('driversStore')
@observer
class NavbarContainer extends react.Component{
  get injected() {
    return this.props as InjectedProps
  }

  /**
   * Вовращает JSX элемент отображения заголовка.
   * @returns JSX элемент отображения заголовка.
   */
  render() {
    const {driversStore} = this.injected;
    return <Navbar
        open={driversStore.openLeftPanel}
        setOpen={driversStore.setOpenLeftPanel}/>
  }
}

/**
 * Оборачиваем JSX контейнер Redux компонентой для передачи необходимых данных
 * хранилища и объектов событий, которые нужно обрабатывать хранилищем
 * состояния.
 */
export default NavbarContainer;