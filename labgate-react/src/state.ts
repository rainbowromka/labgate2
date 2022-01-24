import {DRIVER_STATUS_STOP, DRIVER_STATUS_WORK} from "./def/client-types";
import {action, makeObservable, observable} from "mobx";
import {
    AuthState,
    DriverItem,
    DriversState,
    Principal
} from "./def/client-types";

export interface IAppStoreProps
{
    appStore: AppStoreClass;
}

export class AppStoreClass {

    @observable
    driver: DriverItem = {
        id: 0,
        code: "",
        name: "",
        parameters: [],
        type: "",
        status: DRIVER_STATUS_STOP,
    };

    @observable
    auth: AuthState = {
        principal: {
            token: "",
            type: "Bearer",
            id: null,
            username: "",
            email: "",
            roles: [],
        } as Principal,
        isAuthorized: false,
    }

    @observable
    drivers: DriversState = {
        list: [],
        name: "",
        code: "",
        type: "",
        lastId: 0,
        page: 0,
        pageSize: 3,
        totalElements: 0
    }

    @observable isFetching: boolean = false;

    @observable openLeftPanel: boolean = false;

    constructor() {
        makeObservable(this);
        this.runStopDriver = this.runStopDriver.bind(this);
        this.setOpenLeftPanel = this.setOpenLeftPanel.bind(this);
    }

    setDriver(
        driver: DriverItem)
    {
        this.driver = driver;
    }

    setIsFetching(
        isFetching: boolean)
    {
        this.isFetching = isFetching;
    }

    setUserData(
        principal: Principal,
        authorized: boolean)
    {
        this.auth.principal = principal;
        this.auth.isAuthorized = authorized;
    }

    @action
    addDriver()
    {
        let state = this.drivers;
        state.lastId++;
        state.list.push({
            id: state.lastId,
            name: state.name,
            code: state.code,
            type: state.type,
            status: DRIVER_STATUS_STOP,
            parameters: []
        })
    }

    @action
    setDriverName(
        name: string)
    {
        this.drivers.name = name;
    }

    setDriverCode(
        code: string)
    {
        this.drivers.code = code;
    }

    setDriverType(
        type: string)
    {
        this.drivers.type = type;
    }

    setDrivers(
        list: DriverItem[],
        page: number,
        pageSize: number,
        totalElements: number)
    {
        let lastId = this.drivers.lastId;
        list.forEach((item) => {
            lastId = Math.max(lastId, item.id)
        })
        this.drivers = {...this.drivers, list, lastId, page, pageSize,
            totalElements};
    }

    setCurrentPage(
        page: number)
    {
        this.drivers.page = page;
    }

    runStopDriver(
        id: number, driverStatus: string)
    {
        this.drivers.list.forEach((item) => {
            if (item.id === id) {
                item.status = driverStatus;
            }
        })
    }

    setOpenLeftPanel(
        open: boolean)
    {
        this.openLeftPanel = open;
    }
}

export const APP_STORE = new AppStoreClass();