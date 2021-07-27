import {combineReducers, createStore} from "redux";
import driversReducer from "./drivers-reducer";
import driverReducer from "./driver-reducer";

let store = createStore(combineReducers({
  drivers: driversReducer,
  driver: driverReducer,
}))

export default store;