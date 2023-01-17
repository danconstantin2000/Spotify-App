import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import { Navbar } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';

function Songs() {
    const auth = localStorage.getItem('user');
    useEffect(() => {
        getSongData(1);
    }, [])
    const requestOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    };
    const [songs, setSongs] = useState([]);
    const [currentPage, setCurrentPage] = useState([]);
    const [hasNext, setHasNext] = useState([]);
    const [hasPrev, setHasPrev] = useState([]);
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


    return (
        <>
            {auth ? (
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
                                <button className="col-2 btn btn-success">Adauga</button>
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
                                <a href="/songs/{song.id}" class="col-4">{song.name}</a>
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