import React from "react";
import s from "./Navbar.module.css";
import {NavLink} from "react-router-dom";

const Navbar = () => {
  return (
    <div className={s.nav}>
      <div>
        <NavLink to="/drivers" className={s.item} activeClassName={s.activeLink}>Список драйверов</NavLink>
      </div>
      <div>
        <NavLink to="/devices" className={s.item} activeClassName={s.activeLink}>Приборы</NavLink>
      </div>
    </div>
  )
}

export default Navbar;