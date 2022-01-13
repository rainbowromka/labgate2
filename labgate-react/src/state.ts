import react from "react"
import {DriverItem, Parameter} from "./redux/driver-reducer";
import {DRIVER_STATUS_STOP} from "./redux/drivers-reducer";
import {observable} from "mobx";

export class AppState {

    @observable driver: DriverItem = {
        id: 0,
        code: "",
        name: "",
        parameters: [],
        type: "",
        status: DRIVER_STATUS_STOP,
    };

    @observable isFetching: boolean = true;

    constructor() {
    }
}