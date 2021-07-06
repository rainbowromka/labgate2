import driversReducer from "./drivers-reducer";

const ADD_DRIVER = "ADD_DRIVER";
const SET_NAME = "SET_NAME";
const SET_CODE = "SET_CODE";
const SET_TYPE = "SET_TYPE";

let renderEntireTree = () => {
}

let store ={
  _driversState: {
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
  },
  getDriversState () {
    return this._driversState;
  },
  subscribe: function (observer) {
    renderEntireTree = observer;
  },
  dispatch(action) {
    this._driversState = driversReducer(this._driversState, action);
    renderEntireTree(this);
  },
};


export default store;