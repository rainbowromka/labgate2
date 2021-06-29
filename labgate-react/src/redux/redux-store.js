import {combineReducers, createStore} from "redux";
import driversReducer from "./drivers-reducer";

let store = createStore(combineReducers({
  drivers: driversReducer,
}))

export default store;