const ADD_DRIVER = "ADD_DRIVER";
const SET_NAME = "SET_NAME";
const SET_CODE = "SET_CODE";
const SET_TYPE = "SET_TYPE";
const SET_DRIVERS = "SET_DRIVERS";
const RUN_STOP_DRIVER = "RUN_STOP_DRIVER";
// const STOP_DRIVER = "STOP_DRIVER";

export const DRIVER_STATUS_WORK = "WORK";
export const DRIVER_STATUS_STOP = "STOP";

let initialState =  {
  list: [],
  name: "",
  code: "",
  type: "",
  lastId: 0,
}

const driversReducer = (state = initialState, action) => {
  switch (action.type) {
    case ADD_DRIVER: {
      state.lastId++;
      state.list.push({
        id: state.lastId,
        name: state.name,
        code: state.code,
        type: state.type,
        status: DRIVER_STATUS_STOP,
      });
      return {...state,
        list: [...state.list], name: "", code: "", type: "", lastId: state.lastId};
    }
    case SET_NAME: {
      state.name = action.value;
      return {...state};
    }
    case SET_CODE: {
      state.code = action.value;
      return {...state};
    }
    case SET_TYPE: {
      state.type = action.value;
      return {...state};
    }
    //TODO: Тут посмотреть и сделать все через пару ключ-значение.
    case RUN_STOP_DRIVER: {
      return {...state, list: state.list.map((item) => {
        if (item.id === action.id) {
          return {...item, status: item.status === DRIVER_STATUS_WORK
            ? DRIVER_STATUS_STOP
            : DRIVER_STATUS_WORK};
        } else {
          return item;
        }
      })}
//      return {...state}
    }
    case SET_DRIVERS: {
      let lastId = state.lastId;
      action.list.map(item => {
        lastId = Math.max(lastId, item.id);
        return item;
      });
      return {...state, list: [...action.list], lastId}
    }
    default: {
      return state;
    }
  }
}

export const actionCreatorAddDriver = () => ({type: ADD_DRIVER});

export const actionCreatorSetName = (name) => ({type: SET_NAME,value: name})

export const actionCreatorSetCode = (code) => ({type: SET_CODE,value: code})

export const actionCreatorSetType = (type) => ({type: SET_TYPE,value: type})

export const acRunStopDriver = (id) => ({type: RUN_STOP_DRIVER, id})

export const acSetDrivers = (list) => ({type: SET_DRIVERS, list})

export default driversReducer;
