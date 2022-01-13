/**
 * Redux - редьюсер хранилища авторизации.
 */

/**
 * Константа - команда установки данных пользователя.
 */
const SET_USER_DATA = "SET_USER_DATA" as string;

/**
 * Константа - команда уведомления, что идет обмен с сервером.
 */
const SET_IS_FETCHING = "SET_IS_FETCHING" as string;

/**
 * Прнципал - информация о пользователе.
 */
export type Principal = {
  token: string | null,
  type: string | null,
  id: number | null,
  username: string | null,
  email: string | null,
  roles: string[] | null[] | null,
}

/**
 * Состояние хранилища по умолчанию.
 */
let initialState =  {
  principal: {
    token: "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb2QiLCJpYXQiOjE2Mzk0NzE1MzgsImV4cCI6MTYzOTU1NzkzOH0.rrzSJ8hvC01u8yEvLALel7MtWRAI4Ri6McKjV9XwNeOt0QlYi4TBw3Ik55qtoVpuU1KGELYSE6vKBFeYQPlNDQ",
    type: "Bearer",
    id: null,
    username: "",
    email: "",
    roles: [],
  } as Principal,
  isFetching: false as boolean,
  isAuthorized: false as boolean | null,
}
export type AuthState = typeof initialState;

/**
 * Выполняем основные операции с хранилищем авторизации/регистрации
 * пользователя.
 *
 * @param state
 *        состояние хранилища.
 * @param action
 *        вид события, которое нужно обработать.
 */
const authReducer = (
    state = initialState,
    action: any): AuthState =>
{
  switch (action.type) {
    case SET_USER_DATA: {
      let newState: AuthState = {
        principal: action.data.principal,
        isFetching: state.isFetching,
        isAuthorized: action.data.authorized
      };
      return newState;
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
 * Объект события установки данных пользователя.
 */
type SetUserDateAction = {
  type: typeof SET_USER_DATA
  data: {
    principal: Principal
    authorized: boolean
  }
}

/**
 * Создает объект события, когда необходимо обновить данные пользователя в
 * хранилище состояния.
 *
 * @param principal
 *        данные пользователя.
 * @param authorized
 *        признак того, что пользователь авторизирован на сервере.
 * @returns объект события.
 */
export const setUserData = (
    principal: Principal,
    authorized: boolean): SetUserDateAction =>
({
  type: SET_USER_DATA,
  data: {principal: principal, authorized: authorized}
});

/**
 * Объект события начала/окончания передачи данных на сервер.
 */
type SetIsFetchingAction = {
  type: typeof SET_IS_FETCHING
  isFetching: boolean
}
/**
 * Создает объект события, когда необходимо сохранить состояние о том, что идет
 * обмен данными с сервером.
 *
 * @param isFetching
 *        признак того что идет обмен данными с сервером.
 * @returns объект события.
 */
export const setIsFetching = (isFetching: boolean):SetIsFetchingAction =>
    ({type: SET_IS_FETCHING, isFetching});

export default authReducer;