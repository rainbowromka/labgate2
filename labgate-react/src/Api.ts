import axios from "axios";
import {AuthData} from "./def/client-types";

axios.defaults.withCredentials = true;

const baseUrl = "http://localhost:3002";
const apiUrl = baseUrl + "/api";
const apiDriversUrl = apiUrl + "/drivers";
const apiAuth = apiUrl + "/auth";

/**
 * Базовый экземпляр доступа к Rest API сервиса драйверов.
 */
const driversApiInstance = axios.create({
    baseURL: apiDriversUrl,
    withCredentials: true,
})

/**
 * Базовый экземпляр доступа к Rest API сервиса авторизации.
 */
const authApiInstance = axios.create({
    baseURL: apiAuth,
    withCredentials: true
})

/**
 * Объект, содержащий обращения Rest API к сервису драйверов.
 */
export const DriversApi = {
    /**
     * Получить экземпляр драйвера по id.
     * @param driverId
     *        id - экземпляра драйвера.
     */
    getDriversById(driverId: any) {
        return driversApiInstance.get("/list/" + driverId,
            {
                // headers: {
                //     'Authorization': `${APP_STORE.auth.principal.type} ${APP_STORE.auth.principal.token}`,
                // },
            })
    },

    /**
     * Получить список драйверов по номеру страницы.
     *
     * @param pageNumber
     *        номер страницы в списке.
     */
    getDriversListByPageNumber(pageNumber: number, pageSize: number) {
        return driversApiInstance.get(`/list?page=${pageNumber}&size=${pageSize}`,
            {
            }
        )
    },

    /**
     * Остановить/запустить драйвер по ID экземпляра драйвера.
     * @param id
     *        id - экземпляра драйвера.
     */
    runStopDriver(id: number) {
        return driversApiInstance.get('/runStopDriver/' + id,
            {
                // headers: {
                //     'Authorization': `${APP_STORE.auth.principal.type} ${APP_STORE.auth.principal.token}`
                // }
            }
        )
    }
}

/**
 * Объект, содержащий обращения Rest API к сервису авторизации.
 */

export const AuthApi = {
    /**
     * Получить информацию о пользователе.
     * @param token
     *        токен пользователя.
     */
    GetUserInfo() {
        return authApiInstance.get("/info",{})
    },

    /**
     * Авторизоваться в системе.
     *
     * @param username
     *        имя пользователя.
     * @param password
     *        пароль.
     */
    ApiSiginIn(username: string, password: string) {
        return authApiInstance.post("/signin",
            {username: username, password: password},{}
        )
    },
    /**
     * Зарегистрировать пользователя.
     *
     * @param authData
     *        Объект, содержащий данные пользователя.
     */
    ApiSignUp(authData: AuthData) {
        return authApiInstance.post("/signup",
            {
                username: authData.username,
                email: authData.email,
                password: authData.password,
                role: ["mod", "user"],
            },
            {}
        )
    }
}
