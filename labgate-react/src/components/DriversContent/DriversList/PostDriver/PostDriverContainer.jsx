import React from "react";
import {connect} from "react-redux";
import PostDriver from "./PostDriver";
import {
  addDriver, setDriverCode,
  setDriverName, setDriverType
} from "../../../../redux/drivers-reducer";

let mapStateToProps = (state) => ({
  drivers: state.drivers,
})

class PostDriverContainer extends React.Component {

  componentDidMount() {
  }

  render() {
    return (
      <PostDriver {...this.props}/>
    );
  }

}

export default connect(mapStateToProps,
  {addDriver, setDriverName, setDriverCode, setDriverType}
)(PostDriverContainer);