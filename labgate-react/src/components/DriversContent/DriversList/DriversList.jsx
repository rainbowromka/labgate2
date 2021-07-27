import React from "react";
import DriverItem from "./DriverItem/DriverItem";
import PostDriverContainer from "./PostDriver/PostDriverContainer";
import s from "./DriversList.module.css";


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
        <div>{pagesList}</div>
        <PostDriverContainer store={props}/>
        {driverElements}
      </>
    )
}


export default DriversList;