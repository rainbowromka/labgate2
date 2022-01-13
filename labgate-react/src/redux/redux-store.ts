import {combineReducers, createStore} from "redux";
import driversReducer from "./drivers-reducer";
import driverReducer from "./driver-reducer";
import authReducer from "./auth-reducer";

let rootReducers = combineReducers({
  drivers: driversReducer,
  driver: driverReducer,
  auth: authReducer,
});

type RootReducerType = typeof rootReducers;
export type AppStateType = ReturnType<RootReducerType>;

/**
 * Объект хранилища состояний Redux.
 */
let store = createStore(rootReducers)

export default store;