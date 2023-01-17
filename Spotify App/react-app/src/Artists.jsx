import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import { Navbar } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';

function Artists() {
    const auth = localStorage.getItem('user');
    useEffect(() => {
        getSongData();
    }, [])
    const requestOptions = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    };
    const [artists, setArtists] = useState([]);
    const getSongData = async () => {
        const response = await fetch('http://localhost:8082/api/gateway/artists', requestOptions)
            .then((response) => response.json())
            .then((data) => {
                setArtists(data._embedded.artistDTOList)
                console.log(data._embedded.artistDTOList[0].uid)

            })
            .catch((error) => {
                console.error('Error:', error);
            });
    }


    return (


        <>
            <div class="container d-flex flex-column mt-3">
                <div class="container d-flex flex-row mb-3 header bg-dark">
                    <div class="col-6 ">Nume</div>
                    <div class="col-6">Activ</div>


                </div>
                {artists.map((artist) => (

                    < div className="container d-flex flex-row mb-3 song p-4 bg-dark" >
                        <div class="col-6">{artist.name}</div>
                        <div class="col-6">{artist.active.toString()}</div>
                    </div>

                ))
                }
            </div>
        </>




    );
}

export default Artists;