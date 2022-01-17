import axios from "axios";
import {AuthData} from "./def/client-types";
import {APP_STORE} from "./state";

const baseUrl = "http://localhost:8080";
const servicesUrls = baseUrl + "/services";
const apiDriversUrl = servicesUrls + "/drivers";
const apiAuth = baseUrl + "/api/auth";

const driversApiInstance = axios.create({
    baseURL: apiDriversUrl,
})

const authApiInstance = axios.create({
    baseURL: apiAuth,
})

export const DriversApi = {
    getDriversById(driverId: any) {
        return driversApiInstance.get("/list/" + driverId,
            {
                headers: {
                    'Authorization': `${APP_STORE.auth.principal.type} ${APP_STORE.auth.principal.token}`,
                },
            })
    },
    getDriversListByPageNumber(pageNumber: number) {
        return driversApiInstance.get(`/list?page=${pageNumber}&size=${APP_STORE.drivers.pageSize}`,
            {
                headers: {
                    'Authorization': `${APP_STORE.auth.principal.type} ${APP_STORE.auth.principal.token}`,
                },
            }
        )
    }
}



export const GetUserInfo = (token: string | null) => {
    return axios.get(apiAuth + "/info",
        {
            headers: {
                Authorization: "Bearer " + token
            }
        }
    )
}

export const ApiSiginIn = (username: string, password: string) => {
    return authApiInstance.post("/signin",
        {username: username, password: password},
        {
            // withCredentials: true
        }
    )
}

export const ApiSignUp = (authData: AuthData) => {
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

