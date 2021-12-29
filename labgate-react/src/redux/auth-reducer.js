/**
 * Redux - редьюсер хранилища авторизации.
 */

/**
 * Константа - команда установки данных пользователя.
 */
const SET_USER_DATA = "SET_USER_DATA";

/**
 * Константа - команда уведомления, что идет обмен с сервером.
 */
const SET_IS_FETCHING = "SET_IS_FETCHING";

/**
 * Состояние хранилища по умолчанию.
 */
let initialState =  {
  token: "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtb2QiLCJpYXQiOjE2Mzk0NzE1MzgsImV4cCI6MTYzOTU1NzkzOH0.rrzSJ8hvC01u8yEvLALel7MtWRAI4Ri6McKjV9XwNeOt0QlYi4TBw3Ik55qtoVpuU1KGELYSE6vKBFeYQPlNDQ",
  type: "Bearer",
  id: null,
  username: "",
  email: "",
  roles: [],
  isFetching: false,
  isAuthorized: false
}

/**
 * Редьюссер, выполняющий основные операции с хранилищем состояния. После
 * выполнения данной функции, происходит обновление компонентов, в случае если
 * произошло изменение состояния хранилища.
 *
 * @param state
 *        состояние хранилища.
 * @param action
 *        вид события, которое нужно обработать.
 * @returns обновленное состояние хранилища, либо старое, если не требуется
 *          обновление.
 *
 */
const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case SET_USER_DATA: {
      let principal = action.data.principal;
      principal.isFetching = state.isFetching;
      principal.isAuthorized = action.data.authorized;
      return principal;
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
 * Создает объект события, когда необходимо обновить данные пользователя в
 * хранилище состояния.
 *
 * @param principal
 *        данные пользователя.
 * @param authorized
 *        признак того, что пользователь авторизирован на сервере.
 * @returns объект события.
 */
export const setUserData = (principal, authorized) => ({
  type: SET_USER_DATA,
  data: {principal, authorized}
});

/**
 * Создает объект события, когда необходимо сохранить состояние о том, что идет
 * обмен данными с сервером.
 *
 * @param isFetching
 *        признак того что идет обмен данными с сервером.
 * @returns объект события.
 */
export const setIsFetching = (isFetching) => ({type: SET_IS_FETCHING, isFetching});

export default authReducer;