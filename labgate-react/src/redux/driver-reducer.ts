/**
 * Redux - редьюсер хранилища состояния драйвера.
 */
import {DRIVER_STATUS_STOP} from "./drivers-reducer";

/**
 * Константа - команда установки состояния драйвера.
 */
const SET_DRIVER: string = "SET_DRIVER";

/**
 * Константа - команда уведомления, что идет обмен сервером.
 */
const SET_IS_FETCHING: string = "SET_IS_FETCHING";

/**
 * Параметры драйвера.
 */
export type Parameter = {
  name: string | null
  value: string | null
}

/**
 * Объект драйвера.
 */
export type DriverItem = {
  id: number
  name: string | null
  code: string | null
  type: string | null
  parameters: Parameter[]
  status: string | null
}

/**
 * Состояние хранилища по умолчанию.
 */
let initialState =  {
  driver: {
    id: null as number | null,
    code: "" as string | null,
    name: "" as string | null,
    parameters: [] as Parameter[] | null[],
    type: "" as string | null,
    status: DRIVER_STATUS_STOP as string,
  } as DriverItem,
  isFetching: true as boolean,
}
export type DriverState = typeof initialState;

/**
 * Выполняем основные операции с хранилищем состояния драйвера.
 *
 * @param state
 *        состояние хранилища.
 * @param action
 *        вид события, которое нужно обработать.
 */
const driverReducer = (state: DriverState = initialState, action: any) => {
  switch (action.type) {
    case SET_DRIVER: {
      return {
        ...state,
        driver: action.driverItem,
      }
    }
    case SET_IS_FETCHING: {
      return {...state, isFetching: action.isFetching}
    }
    default: {
      return state;
    }
  }
}

/**
 * Action Creator события установки драйвера.
 */
type SetDriverAction = {
  type: typeof SET_DRIVER
  driverItem: DriverItem
}
/**
 * Возвращает Action Creator события установки драйвера.
 */
export const setDriver = (driverItem: DriverItem): SetDriverAction =>
  ({type: SET_DRIVER, driverItem});

/**
 * Action Creator статуса загрузки данных с сервера.
 */
type SetIsFetchingAction = {
  type: typeof SET_IS_FETCHING,
  isFetching: boolean
}

/**
 * Возвращает Action Creator статуса загрузки данных с сервера.
 */
export const setIsFetching = (isFetching: boolean): SetIsFetchingAction  =>
  ({type: SET_IS_FETCHING, isFetching});

export default driverReducer;