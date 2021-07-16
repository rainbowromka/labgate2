import React from "react";
import DriverItem from "./DriverItem/DriverItem";
import PostDriverContainer from "./PostDriver/PostDriverContainer";
import * as axios from 'axios';
import s from "./DriversList.module.css";
import Preloader from "../../Commons/Preloader/Preloader";

class DriversList extends React.Component {

  componentDidMount() {
    this.props.setIsFetching(true);
    axios.get(`http://localhost:8080/services/drivers/list?page=${this.props.drivers.page}&size=${this.props.drivers.pageSize}`).then(response => {
      this.props.setDrivers(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
      this.props.setIsFetching(false);
    });
  }

  onSetCurrentPage = (pageNumber) => {
    this.props.setIsFetching(true);
    this.props.setCurrentPage(pageNumber);
    axios.get(`http://localhost:8080/services/drivers/list?page=${pageNumber}&size=${this.props.drivers.pageSize}`).then(response => {
      this.props.setDrivers(response.data.content, response.data.number,
        response.data.size, response.data.totalElements);
      this.props.setIsFetching(false);
    });
  }

  render() {
    let driverElements = this.props.drivers.list.map (
      item => <DriverItem
        key={item.id}
        driver={item}
        runStopDriver={this.props.runStopDriver}/>
    );

    let pagesCount = Math.ceil(this.props.drivers.totalElements /
      this.props.drivers.pageSize);

    let pagesList = [];
    for (let q = 0; q < pagesCount; q++)
    {
      pagesList.push(<span key={q}
        className={q === this.props.drivers.page && s.selectedPage}
        onClick={() => {this.onSetCurrentPage(q)}}
      >
        {q + 1}
      </span>)
    }

    return (
      this.props.drivers.isFetching
        ? <Preloader/>
        : <>
          <div>{pagesList}</div>
          <PostDriverContainer store={this.props}/>
          {driverElements}
        </>
    )
  }
}

export default DriversList;