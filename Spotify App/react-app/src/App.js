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
import { AddContentManager } from "./AddContentManager";
import { useEffect, useState } from 'react';
import { AddArtist } from "./AddArtists";
import { AddSong } from "./AddSong";
import GetSongsFromArtist from "./GetSongsFromArtist";
function App() {
  const [user, setUser] = useState('');
  useEffect(() => {
    const auth = localStorage.getItem('user');
    let user = null
    if (auth) {
      user = JSON.parse(auth);
    }
    setUser(user)

  }, [])
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

          {user ? (

            user.roles.includes(4) ? (
              <>

                <Route path="/addContentManager" element={<AddContentManager />} />
              </>
            ) : (

              user.roles.includes(1) ? (
                <>

                  <Route path="/addArtist" element={<AddArtist />} />
                </>
              ) : (
                user.roles.includes(2) ? (
                  <>

                    <Route path="/addSong" element={<AddSong />} />
                    <Route path="/mySongs" element={<GetSongsFromArtist />} />
                  </>
                ) : null
              )
            )


          ) : null}

        </Routes>

      </div>


    </>
  );
}

export default App;
