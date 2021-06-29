const ADD_DRIVER = "ADD_DRIVER";
const SET_NAME = "SET_NAME";
const SET_CODE = "SET_CODE";
const SET_TYPE = "SET_TYPE";

let initialState =  {
  list: [{
    name: "KDLPrime3",
    code: "KDLPrime",
    type: "SOCKET",
    status: "Работает"
  }, {
    name: "KDLMax2",
    code: "KDLMax",
    type: "SOCKET",
    status: "Остановлен"
  }, {
    name: "CITM1",
    code: "CITM",
    type: "TTY",
    status: "Работает"
  }],
  name: "",
  code: "",
  type: "",
}

const driversReducer = (state = initialState, action) => {
  switch (action.type) {
    case ADD_DRIVER:
      state.list.push(action.driver);
      return state;
    case SET_NAME:
      state.name = action.value;
      return state;
    case SET_CODE:
      state.code = action.value;
      return state;
    case SET_TYPE:
      state.type = action.value;
      return state;
    default:
      return state;
  }
}

export default driversReducer;
