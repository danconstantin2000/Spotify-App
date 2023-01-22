import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import { Navbar } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';

function SongInfo() {


    const params = useParams();
    const id = params.id;

    useEffect(() => {


        getSongData();
        getArtists(id);
    }, [])
    const requestOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    };
    const [song, setSong] = useState([]);
    const [artists, setArtists] = useState([]);

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
    const getArtists = async (id) => {
        const response = await fetch(`http://localhost:8082/api/gateway/songs/${id}/artists`, requestOptions)
            .then((response) => response.json())
            .then((data) => {

                setArtists(data._embedded.artistDTOList);

            })
            .catch((error) => {
                console.error('Error:', error);
            });
    }

    return (
        <>
            {song ? (

                <>
                    < div className="container d-flex flex-column mb-3 song p-4 bg-dark songInfo" >
                        <div class=" mb-3 container d-flex justify-content-around col-3">
                            <div>Nume:</div>
                            <div>{song.name}</div>
                        </div>

                        <div class=" mb-3 container d-flex justify-content-around col-3">
                            <div>Gen:</div>
                            <div>{song.gen}</div>
                        </div>
                        <div class=" mb-3 container d-flex justify-content-around col-3">
                            <div>An:</div>
                            <div>{song.year}</div>
                        </div>
                        <div class=" mb-3 container d-flex justify-content-around col-3">
                            <div>Tip:</div>
                            <div>{song.type}</div>
                        </div>
                        <div class=" mb-3 container d-flex justify-content-around col-3">
                            <div>Artisti:</div>
                            <ul>
                                {artists.map((artist) => (
                                    <li>{artist.name}</li>
                                ))}
                            </ul>

                        </div>

                    </div>



                </>
            )
                :
                (
                    <>
                        <div>User not found!</div>
                    </>
                )}

        </>
    );
}

export default SongInfo;