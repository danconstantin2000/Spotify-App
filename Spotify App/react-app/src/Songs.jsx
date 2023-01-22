import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import { Navbar } from 'react-bootstrap';
import { redirect, useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';

function Songs() {
    const params = useParams();
    const id = params.id;

    useEffect(() => {
        const auth = localStorage.getItem('user');
        let user = null
        if (auth) {
            user = JSON.parse(auth);
        }
        setUser(user)
        getSongData(1);
        setSuccessfullAdded(null)
    }, [])
    const requestOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    };
    const [songs, setSongs] = useState([]);
    const [currentPage, setCurrentPage] = useState([]);
    const [hasNext, setHasNext] = useState([]);
    const [hasPrev, setHasPrev] = useState([]);
    const [user, setUser] = useState([]);
    const [successfullAdded, setSuccessfullAdded] = useState([])
    const getSongData = async (page) => {
        const response = await fetch('http://localhost:8082/api/gateway/songs?page=' + page, requestOptions)
            .then((response) => response.json())
            .then((data) => {
                setSongs(data._embedded.songDTOList)
                setCurrentPage(page)
                if (data._links.next) {

                    setHasNext(true);
                }
                else {
                    setHasNext(false);
                }
                if (data._links.prev) {

                    setHasPrev(true);
                }
                else {
                    setHasPrev(false);

                }


            })
            .catch((error) => {
                console.error('Error:', error);
            });
    }

    const addSongToPlaylist = async (songId) => {
        const requestOptions = {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json', 'Authorization': "Bearer " + user.jwt_token }
        };

        const response = await fetch('http://localhost:8082/api/gateway/playlists/' + id + "/songs/" + songId, requestOptions)
            .then((response) => response.json())
            .then((data) => {


            })
            .catch((error) => {

            });
        setSuccessfullAdded(true)

    }


    return (
        <>
            {user ? (
                <>

                    <div class="container d-flex flex-column mt-3">
                        <div>
                            {successfullAdded == true ? (

                                <div class="alert alert-success">Successfull added!</div>
                            ) : null}
                        </div>
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
                                {id ? (
                                    <button className="col-2 btn btn-success" onClick={() => {
                                        addSongToPlaylist(song.id)
                                    }}>Adauga</button>
                                ) : null}

                            </div>

                        ))
                        }
                        <div>
                            {hasPrev ? (
                                <a onClick={() => { getSongData(currentPage - 1) }} class="p-3">Prev</a>
                            ) : (
                                <a class="p-3 disabled">Prev</a>
                            )}
                            {hasNext ? (
                                <a onClick={() => { getSongData(currentPage + 1) }} class="p-3">Next</a>
                            ) : (
                                <a class="p-3 disabled">Next</a>
                            )}


                        </div>

                    </div>
                </>

            ) : (
                <>
                    <div class="container d-flex flex-column  mt-3">
                        <div class="container d-flex flex-row mb-3 header bg-dark ">
                            <div class="col-4 ">Melodie</div>
                            <div class="col-3">Gen</div>
                            <div class="col-3">An</div>
                            <div className="col-2">Tip</div>
                        </div>
                        {songs.map((song) => (

                            < div className="container d-flex flex-row mb-3 song p-4 bg-dark" >
                                <a href={"/songs/" + song.id} class="col-4">{song.name}</a>
                                <div class="col-3">{song.gen}</div>
                                <div class="col-3">{song.year}</div>
                                <div className="col-2">{song.type}</div>
                            </div>

                        ))
                        }
                        <div>
                            {hasPrev ? (
                                <a onClick={() => { getSongData(currentPage - 1) }} class="p-3">Prev</a>
                            ) : (
                                <a class="p-3 disabled">Prev</a>
                            )}
                            {hasNext ? (
                                <a onClick={() => { getSongData(currentPage + 1) }} class="p-3">Next</a>
                            ) : (
                                <a class="p-3 disabled">Next</a>
                            )}


                        </div>
                    </div>
                </>
            )
            }



        </>
    );
}

export default Songs;