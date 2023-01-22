import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import { Navbar } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';

function PlaylistInfo() {


    const params = useParams();
    const id = params.id;

    useEffect(() => {

        const auth = localStorage.getItem('user');
        let user = null
        if (auth) {
            user = JSON.parse(auth);
        }
        setUser(user)
        getSongData();
        getPlaylistData(user);

    }, [])
    const requestOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    };
    const [song, setSong] = useState([]);
    const [artists, setArtists] = useState([]);
    const [user, setUser] = useState([]);
    const [playlistSongs, setPlaylistSongs] = useState([]);

    const getSongData = async () => {
        const response = await fetch(`http://localhost:8082/api/gateway/songs/${id}`, requestOptions)
            .then((response) => response.json())
            .then((data) => {


                setSong(data);


            })
            .catch((error) => {
                console.error('Error:', error);
            });
    }

    const getPlaylistData = async (user) => {
        const requestOptions = {
            method: 'GET',
            headers: { 'Content-Type': 'application/json', 'Authorization': "Bearer " + user.jwt_token }
        };
        const response = await fetch('http://localhost:8082/api/gateway/getPlaylist/' + id, requestOptions)
            .then((response) => response.json())
            .then((data) => {
                console.log(data)
                setPlaylistSongs(data.songs);


            })
            .catch((error) => {
                console.error('Error:', error);
            });

    }
    return (
        <>

            <div class="container d-flex flex-column mt-3">
                <a class="btn btn-success m-3 col-3 " href={"/playlists/" + id + "/songs"}>Adauga melodii</a>
                <div class="container d-flex flex-row mb-3 header bg-dark">
                    <div class="col-6 ">Id</div>
                    <div class="col-6">Melodie</div>


                </div>

                {playlistSongs.map((song) => (

                    < div className="container d-flex flex-row mb-3 song p-4 bg-dark" >
                        <a href={"/songs/" + song.id} class="col-6">{song.id}</a>
                        <div class="col-6">{song.name}</div>



                    </div>

                ))
                }
            </div>



        </>
    )
}

export default PlaylistInfo;