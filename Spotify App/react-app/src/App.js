import React, { useState } from "react";
import { Routes } from "react-router-dom";
import { Route } from "react-router-dom";
import Home from "./Home";
import About from "./About";
import './App.css';
import NavigationBar from "./NavigationBar";
import { Login } from './Login';
import { Register } from './Register';
function App() {

  return (
    <>

      <NavigationBar />
      <div className="App">

        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/about" element={<About />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
        </Routes>

      </div>


    </>
  );
}

export default App;