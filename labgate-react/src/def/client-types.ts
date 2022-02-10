/**
 * Константа - статус, драйвер работает.
 */
export const DRIVER_STATUS_WORK = "WORK";

/**
 * Константа - статус, драйвер остановлен.
 */
export const DRIVER_STATUS_STOP = "STOP";

/**
 * Константа - статус, драйвер запускается.
 */
export const DRIVER_STATUS_STARTING = "STARTING";

/**
 * Константа - статус, драйвер останавливается.
 */
export const DRIVER_STATUS_STOPPING = "STOPPING";

/**
 * Константа - статус, драйвер перезагружается.
 */
export const DRIVER_STATUS_RESTART = "RESTART";

/**
 * Прнципал - информация о пользователе.
 */
export type Principal = {
    // token: string | null,
    // type: string | null,
    id: number | null,
    username: string | null,
    email: string | null,
    roles: string[] | null[] | null,
}

/**
 * Состояние пользователя.
 */
export type AuthState = {
    principal: Principal,
    isAuthorized: boolean,
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
 * Параметры драйвера.
 */
export type Parameter = {
    name: string | null
    value: string | null
}

/**
 * Состояние списка драйверов.
 */
export type DriversState = {
    list: Array<DriverItem>
    name: string | null
    code: string | null
    type: string | null
    lastId: number
    page: number
    pageSize: number
    totalElements: number
};

/**
 * Вид формы - вход в систему.
 */
export const AUTH_MODE_LOGIN = 'login';

/**
 * Вид формы - регистрация в системе.
 */
export const AUTH_MODE_SIGNUP = 'signup';

/**
 * Данные авторизации.
 */
export type AuthData = {
    authMode: typeof AUTH_MODE_LOGIN | typeof AUTH_MODE_SIGNUP
    username: string,
    email: string,
    password: string,
    checkPassword: string,
    showPassword: boolean,
    signInDisabled: boolean,
}

/**
 * Состояние изменяющее включение/выключение панели меню справа.
 */
export type OpenPanelType = {
    open: boolean
    setOpen: (isOpen: boolean) => void
}
