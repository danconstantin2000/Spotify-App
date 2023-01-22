import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import { Navbar } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';

function GetPlaylist() {
    const [playlists, setPlaylists] = useState([]);
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

        return fetch('http://localhost:8082/api/gateway/getPlaylists', requestOptions)
            .then((response) => response.json())
            .then((data) => {
                setPlaylists(data)

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
                            <div class="col-6 ">Playlist</div>
                            <div class="col-6">Titlu</div>


                        </div>
                        {playlists.map((playlist) => (

                            < div className="container d-flex flex-row mb-3 song p-4 bg-dark" >

                                <a href={"/playlists/" + playlist.id} class="col-6">{playlist.id}</a>
                                <div class="col-6">{playlist.title}</div>


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

export default GetPlaylist;