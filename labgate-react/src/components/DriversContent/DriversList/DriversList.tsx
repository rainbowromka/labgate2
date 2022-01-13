import react, {FC} from "react";
import Driver from "./DriverItem/Driver";
import PostDriverContainer from "./PostDriver/PostDriverContainer";
import s from "./DriversList.module.css";
import Grid from "@material-ui/core/Grid";
import {DriversState} from "../../../redux/drivers-reducer";
import {SetDriversParamAction} from "../../../redux/drivers-reducer"

type Props = {
    drivers: DriversState
    runStopDriver: (id: number) => void
    onSetCurrentPage: (page: number) => void
}

let DriversList: FC<Props> = (props) => {

    let driversState: DriversState = props.drivers;

    let driverElements = driversState.list.map (
      item => <Driver
        key={item.id}
        driver={item}
        runStopDriver={props.runStopDriver}/>
    );

    let pagesCount = Math.ceil(driversState.totalElements /
        driversState.pageSize);

    let pagesList = [];
    for (let q = 0; q < pagesCount; q++) {
        pagesList.push(<span key={q}
            className={q === driversState.page ? s.selectedPage : ""}
            onClick={() => {
                props.onSetCurrentPage(q)
            }}
        >
        {q + 1}
      </span>)
    }

    return (
      <>
        <Grid container spacing={2}>
          <div>{pagesList}</div>
          {/*<PostDriverContainer store={props}/>*/}
          <Grid item container spacing={2} alignItems={"center"}>
            <PostDriverContainer/>
            {driverElements}
          </Grid>
        </Grid>
      </>
    )
}


export default DriversList;