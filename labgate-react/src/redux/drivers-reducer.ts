import {DriverItem} from './driver-reducer';

/**
 * Константа - команда добавления драйвера в список драйверов.
 */
const ADD_DRIVER = "ADD_DRIVER";

/**
 * Константа - команда установки имени для нового драйвера.
 */
const SET_NAME = "SET_NAME";

/**
 * Константа - команда установки кода для нового драйвера.
 */
const SET_CODE = "SET_CODE";

/**
 * Константа - команда установки типа драйвера для нового драйвера.
 */
const SET_TYPE = "SET_TYPE";

/**
 * Константа - команда устанавливаем параметры страницы для загруженных
 * драйверов.
 */
const SET_DRIVERS = "SET_DRIVERS";

/**
 * Константа - команда остановки/запуска драйвера.
 */
const RUN_STOP_DRIVER = "RUN_STOP_DRIVER";

/**
 * Константа - команда изменения текущей страницы отображения драйверов.
 */
const SET_CURRENT_PAGE = "SET_CURRENT_PAGE";

/**
 * Константа - команда уведомления, что идет обмен с сервером.
 */
const SET_IS_FETCHING = "SET_IS_FETCHING"


/**
 * Константа - статус, драйвер работает.
 */
export const DRIVER_STATUS_WORK = "WORK";

/**
 * Константа - статус, драйвер остановлен.
 */
export const DRIVER_STATUS_STOP = "STOP";

/**
 * Состояние хранилища по умолчанию.
 */
let initialState =  {
  list: [] as Array<DriverItem>,
  name: "" as string | null,
  code: "" as string | null,
  type: "" as string | null,
  lastId: 0 as number,
  page: 0 as number,
  pageSize: 3 as number,
  totalElements: 0 as number,
  isFetching: true as boolean,
}
export type DriversState = typeof initialState;

/**
 * Выполняем основные операции с хранилищем состояния драйвера.
 *
 * @param state
 *        состояние хранилища.
 * @param action
 *        вид события, которое нужно обработать.
 */
const driversReducer = (state = initialState, action: any): DriversState => {
  switch (action.type) {
    case ADD_DRIVER: {
      state.lastId++;
      state.list.push({
        id: state.lastId,
        name: state.name,
        code: state.code,
        type: state.type,
        status: DRIVER_STATUS_STOP,
        parameters: [],
      });
      return {...state,
        list: [...state.list], name: "", code: "", type: "", lastId: state.lastId};
    }
    case SET_NAME: {
      state.name = action.value;
      return {...state};
    }
    case SET_CODE: {
      state.code = action.value;
      return {...state};
    }
    case SET_TYPE: {
      state.type = action.value;
      return {...state};
    }
    //TODO: Тут посмотреть и сделать все через пару ключ-значение.
    case RUN_STOP_DRIVER: {
      return {...state, list: state.list.map((item) => {
        if (item.id === action.value) {
          return {...item, status: item.status === DRIVER_STATUS_WORK
            ? DRIVER_STATUS_STOP
            : DRIVER_STATUS_WORK};
        } else {
          return item;
        }
      })}
//      return {...state}
    }
    case SET_DRIVERS: {
      let lastId = state.lastId;
      action.list.map((item: DriverItem) => {
        lastId = Math.max(lastId, item.id);
        return item;
      });
      return {
        ...state,
        list: [...action.list],
        lastId,
        page: action.page,
        pageSize: action.pageSize,
        totalElements: action.totalElements,
      }
    }
    case SET_CURRENT_PAGE: {
      return {...state, page: action.value}
    }
    case SET_IS_FETCHING: {
      return {...state, isFetching: action.value}
    }
    default: {
      return state;
    }
  }
}

/**
 * Возвращает Action Creator события добавления драйвера в список драйверов.
 */
export const addDriver = () => ({type: ADD_DRIVER});

/**
 * Объект Action Creator для событий устанавливающих параметры списка драйверов.
 */
export type SetDriversParamAction = {
  type: typeof SET_NAME | typeof SET_CODE | typeof SET_TYPE
      | typeof RUN_STOP_DRIVER | typeof SET_CURRENT_PAGE
      | typeof SET_IS_FETCHING
  value: string | number | boolean
}

/**
 * Возвращает Action Creator события изменения имени добавляемого драйвера.
 *
 * @param name
 *        новое имя добавляемого драйвера.
 */
export const setDriverName = (name: string): SetDriversParamAction =>
    ({type: SET_NAME, value: name});

/**
 * Возвращает Action Creator события изменения кода добавляемого драйвера.
 *
 * @param code
 *        код добавляемого драйвера.
 */
export const setDriverCode = (code: string): SetDriversParamAction =>
    ({type: SET_CODE, value: code});

/**
 * Возвращает Action Creator события изменения типа добавляемого драйвера.
 *
 * @param type
 *        тип добавляемого драйвера.
 */
export const setDriverType = (type: string): SetDriversParamAction =>
    ({type: SET_TYPE, value: type});

/**
 * Возвращает Action Creator события останавливающего/запускающего драйвер.
 *
 * @param id
 *        id драйвера
 */
export const runStopDriver = (id: number): SetDriversParamAction =>
    ({type: RUN_STOP_DRIVER, value: id});

/**
 * Action Creator события, изменяющего параметры страницы списка драйверов.
 */
type SetDriversPageParamsAction = {
  type: typeof SET_DRIVERS
  list: DriverItem[],
  page: number,
  pageSize: number,
  totalElements: number
}

/**
 * Возвращает Action Creator события изменяющего параметры страницы списка
 * драйвера.
 *
 * @param list
 *        список драйверов.
 * @param page
 *        номер страницы, на которой отображаются драйвера.
 * @param pageSize
 *        размер страницы, на которой отображаются драйвера.
 * @param totalElements
 *        общее количество драйверов в базе.
 */
export const setDrivers = (
    list: DriverItem[],
    page: number,
    pageSize: number,
    totalElements: number): SetDriversPageParamsAction =>
  ({type: SET_DRIVERS, list, page, pageSize, totalElements});

/**
 * Возвращает Action Creator события установки текущей страницы отображения
 * списка драйверов.
 *
 * @param page
 *        страница отображения списка драйверов.
 */
export const setCurrentPage = (page: number): SetDriversParamAction =>
    ({type: SET_CURRENT_PAGE, value: page});

/**
 * Возвращает Action Creator статуса загрузки данных с сервера.
 *
 * @param isFetching
 *        признак загрузки данных с сервера.
 */
export const setIsFetching = (isFetching: boolean): SetDriversParamAction =>
    ({type: SET_IS_FETCHING, value: isFetching})

export default driversReducer;
