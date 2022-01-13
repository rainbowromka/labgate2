import react from "react";
import {connect} from "react-redux";
import PostDriver from "./PostDriver";
import {
  addDriver, DriversState, setDriverCode,
  setDriverName, setDriverType
} from "../../../../redux/drivers-reducer";
import {AppStateType} from "../../../../redux/redux-store";


type MapStatePropsType = {
  drivers: DriversState
}

type MapDispatchPropsType = {
  addDriver: () => void
  setDriverName: (name: string) => void
  setDriverCode: (code: string) => void
  setDriverType: (type: string) => void
}

type AllProps = MapStatePropsType & MapDispatchPropsType

class PostDriverContainer extends react.Component<AllProps> {

  componentDidMount() {
  }

  render() {
    return (
      <PostDriver {...this.props}/>
    );
  }

}

const mapStateToProps = (state: AppStateType): MapStatePropsType => ({
  drivers: state.drivers,
})


export default
  connect<MapStatePropsType, MapDispatchPropsType, any, AppStateType>(
      mapStateToProps,
      {addDriver, setDriverName, setDriverCode, setDriverType})
  (PostDriverContainer);