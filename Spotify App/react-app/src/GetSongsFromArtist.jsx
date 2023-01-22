import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import { Navbar } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';

function GetSongsFromArtist() {
    const [songs, setSongs] = useState([]);
    const [user, setUser] = useState([]);


    useEffect(() => {
        const auth = localStorage.getItem('user');
        let user = null
        if (auth) {
            user = JSON.parse(auth);
        }
        setUser(user)

        getSongData(user)



    }, [])

    const navigate = useNavigate();

    const getSongData = (user) => {
        const requestOptions = {
            method: 'GET',
            headers: { 'Content-Type': 'application/json', 'Authorization': "Bearer " + user.jwt_token }
        };

        return fetch('http://localhost:8082/api/gateway/artistsSongs', requestOptions)
            .then((response) => response.json())
            .then((data) => {
                setSongs(data)

            })

            .catch((error) => {
                console.error('Error:', error);
            });
    }

    const deleteSong = (user, songId) => {
        const requestOptions = {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json', 'Authorization': "Bearer " + user.jwt_token },

        };

        return fetch('http://localhost:8082/api/gateway/artistsSongs/' + songId, requestOptions)
            .then((response) => response.json())
            .then((data) => {

            })

            .catch((error) => {
                console.error('Error:', error);
            });
    }

    return (
        <>
            {user ? (
                <>
                    <div class="container d-flex flex-column mt-3">
                        <div class="container d-flex flex-row mb-3 header bg-dark">
                            <div class="col-4 ">Melodie</div>
                            <div class="col-2">Gen</div>
                            <div class="col-2">An</div>
                            <div className="col-2">Tip</div>

                        </div>
                        {songs.map((song) => (

                            < div className="container d-flex flex-row mb-3 song p-4 bg-dark" >
                                <a href={"/songs/" + song.id} class="col-4">{song.name}</a>
                                <div class="col-2">{song.gen}</div>
                                <div class="col-2">{song.year}</div>
                                <div className="col-2">{song.type}</div>
                                <button className="col-2 btn btn-success" onClick={() => {
                                    deleteSong(user, song.id).then(
                                        window.location.reload(true)

                                    )

                                }}>Delete</button>
                            </div>

                        ))
                        }


                    </div>
                </>

            ) : null
            }



        </>
    );
}

export default GetSongsFromArtist;