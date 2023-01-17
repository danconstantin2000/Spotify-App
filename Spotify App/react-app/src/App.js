import React, { useState } from "react";
import { Routes } from "react-router-dom";
import { Route } from "react-router-dom";
import Home from "./Home";
import './App.css';
import NavigationBar from "./NavigationBar";
import { Login } from './Login';
import { Register } from './Register';
import UserInfo from "./UserInfo";
import Songs from "./Songs";
import Artists from "./Artists";
import SongInfo from "./SongInfo";
function App() {

  return (
    <>

      <NavigationBar />
      <div className="App">

        <Routes>
          <Route path="/" element={<Home />} />

          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/user_profile" element={<UserInfo />} />

          <Route path="/songs" element={<Songs />} />
          <Route path="/artists" element={<Artists />} />
          <Route path="/songs/:id" element={<SongInfo />} />
        </Routes>

      </div>


    </>
  );
}

export default App;
