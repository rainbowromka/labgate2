import {
  actionCreatorAddDriver, actionCreatorSetCode,
  actionCreatorSetName, actionCreatorSetType
} from "../../../../redux/state";
import {connect} from "react-redux";
import PostDriver from "./PostDriver";

let mapStateToProps = (state) => ({
  drivers: state.drivers,
})

let mapDispatchToProps = (dispatch) => {
  return {
    addDriver: (driver) => {
      dispatch(actionCreatorAddDriver(driver));
    },
    setDriverName: (name) => {
      dispatch(actionCreatorSetName(name));
    },
    setDriverCode: (code) => {
      dispatch(actionCreatorSetCode(code));
    },
    setDriverType: (type) => {
      dispatch(actionCreatorSetType(type));
    },
  }
}

const PostDriverContainer = connect(mapStateToProps, mapDispatchToProps)(PostDriver)

export default PostDriverContainer;