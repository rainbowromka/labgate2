import React from "react";
import DriverItem from "./DriverItem/DriverItem";
import PostDriverContainer from "./PostDriver/PostDriverContainer";
import s from "./DriversList.module.css";
import Grid from "@material-ui/core/Grid";


let DriversList = (props) => {

    let driverElements = props.drivers.list.map (
      item => <DriverItem
        key={item.id}
        driver={item}
        runStopDriver={props.runStopDriver}/>
    );

    let pagesCount = Math.ceil(props.drivers.totalElements /
      props.drivers.pageSize);

    let pagesList = [];
    for (let q = 0; q < pagesCount; q++)
    {
      pagesList.push(<span key={q}
        className={q === props.drivers.page && s.selectedPage}
        onClick={() => {props.onSetCurrentPage(q)}}
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