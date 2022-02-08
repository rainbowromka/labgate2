import {
    DRIVER_STATUS_STARTING,
    DRIVER_STATUS_STOP,
    DRIVER_STATUS_WORK
} from "./def/client-types";
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
    authState: AuthState = {
        principal: {
            id: null,
            username: "",
            email: "",
            roles: [],
        },
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
        this.setStatus = this.setStatus.bind(this);
        this.setOpenLeftPanel = this.setOpenLeftPanel.bind(this);
    }

    @action
    setDriverItem(
        driver: DriverItem)
    {
        this.driver = driver;
    }

    @action
    setIsFetching(
        isFetching: boolean)
    {
        this.isFetching = isFetching;
    }

    @action
    setUserData(
        principal: Principal,
        authorized: boolean)
    {
        this.authState = {
            principal: principal,
            isAuthorized: authorized
        }
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

    @action
    setDriverCode(
        code: string)
    {
        this.drivers.code = code;
    }

    @action
    setDriverType(
        type: string)
    {
        this.drivers.type = type;
    }

    @action
    setDriversList(
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

    @action
    setStatus(
        id: number,
        status: string)
    {
        this.drivers.list.forEach((item) => {
            if (item.id === id) {
                item.status = status;
            }
        })
    }

    @action
    setOpenLeftPanel(
        open: boolean)
    {
        this.openLeftPanel = open;
    }

    @action
    updateDriverItem(driverItem: DriverItem)
    {
        let list = this.drivers.list.map(value => {
            if (value.id === driverItem.id) {
                return driverItem
            }
            return value;
        });
        this.drivers = {...this.drivers, list: list};
    }
}

// const APP_STORE = new AppStoreClass()
//
// export default APP_STORE;