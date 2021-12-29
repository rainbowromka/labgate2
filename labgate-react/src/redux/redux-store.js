import {combineReducers, createStore} from "redux";
import driversReducer from "./drivers-reducer";
import driverReducer from "./driver-reducer";
import authReducer from "./auth-reducer";

/**
 * Объект хранилища состояний Redux.
 */
let store = createStore(combineReducers({
  drivers: driversReducer,
  driver: driverReducer,
  auth: authReducer,
}))

export default store;