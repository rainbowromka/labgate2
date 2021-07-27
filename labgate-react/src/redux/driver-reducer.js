import {DRIVER_STATUS_STOP} from "./drivers-reducer";

const SET_DRIVER = "SET_DRIVER";
const SET_IS_FETCHING = "SET_IS_FETCHING";

let initialState =  {
  id: null,
  code: "",
  name: "",
  parameters: [],
  type: "",
  status: DRIVER_STATUS_STOP,

  isFetching: true,
}

const driverReducer = (state = initialState, action) => {
  switch (action.type) {
    case SET_DRIVER: {
      return {
        id: action.driverItem.id,
        code: action.driverItem.code,
        name: action.driverItem.name,
        parameters: {...action.driverItem.parameters},
        type: action.driverItem.name,
        state: action.driverItem.status
      }
    }
    case SET_IS_FETCHING: {
      return {...state, isFetching: action.isFetching}
    }
    default: {
      return state;
    }
  }
}

export const setDriver = (driverItem) => ({type: SET_DRIVER, driverItem});
export const setIsFetching = (isFetching) =>
  ({type: SET_IS_FETCHING, isFetching});


export default driverReducer;