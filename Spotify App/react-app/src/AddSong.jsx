import { redirect } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from 'react';

export const AddSong = (props) => {
    const [name, setName] = useState('');
    const [year, setYear] = useState('');
    const [gen, setGen] = useState('');
    const [type, setType] = useState('');
    const [user, setUser] = useState('');
    const [successAdded, setSuccessAdded] = useState('');
    useEffect(() => {
        const auth = localStorage.getItem('user');
        let user = null
        if (auth) {
            user = JSON.parse(auth);
        }
        setUser(user)
        setGen("ROCK")
        setType("SONG")
        setSuccessAdded(null)
    }, [])
    const navigate = useNavigate();


    const handleSubmit = (e) => {
        e.preventDefault()
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': "Bearer " + user.jwt_token },
            body: JSON.stringify({ name: name, year: year, gen: gen, type: type })
        };
        const fetchData = () => {

            return fetch('http://localhost:8082/api/gateway/addSong', requestOptions)
                .then((response) => {

                    if (response.status == 406 || response.status == 401 || response.status == 409 || response.statust == 403) {
                        setSuccessAdded(false)
                    }
                    else if (response.status == 201) {
                        setSuccessAdded(true)

                    }

                })
                .then((data) => {




                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        }
        fetchData()



    }

    return (
        <>
            <div className="Auth-form-container">

                <form className="Auth-form" onSubmit={handleSubmit}>
                    <div className="Auth-form-content">
                        <div>
                            {successAdded == true ? (
                                <div class="alert alert-success">Adaugare cu succes!</div>
                            ) : null}
                            {successAdded == false ? (
                                <div class="alert alert-danger">Adaugare esuata!</div>
                            ) : null}
                        </div>
                        <h3 className="Auth-form-title">Add Song:</h3>
                        <div className="form-group mt-3">
                            <label for="name">The name of the song:</label>
                            <input
                                type="text"
                                className="form-control mt-1"
                                placeholder="Enter the name:"
                                name="name"
                                id="name"
                                required minLength={3} maxLength={30}
                                value={name} onChange={(e) => setName(e.target.value)}
                            />
                        </div>
                        <div className="form-group mt-3">
                            <label for="gen" class="m-2">Choose the genre:</label>

                            <select name="gen" id="gen" value={gen} onChange={(e) => setGen(e.target.value)}>
                                <option value="ROCK">ROCK</option>
                                <option value="METAL">METAL</option>
                                <option value="RAP">RAP</option>
                                <option value="TRAP">TRAP</option>
                                <option value="JAZZ">JAZZ</option>
                                <option value="POP">POP</option>
                            </select>
                        </div>
                        <div className="form-group mt-3">
                            <label for="type" class="m-2">Choose the type:</label>
                            <select name="type" id="type" value={type} onChange={(e) => setType(e.target.value)}>
                                <option value="SINGLE">SINGLE</option>
                                <option value="ALBUM">ALBUM</option>
                                <option value="SONG">SONG</option>
                            </select>
                        </div>

                        <div className="form-group mt-3">
                            <label for="year">Choose the year: </label>
                            <input
                                type="number"
                                className="form-control mt-1"
                                placeholder="Enter the year:"
                                name="year"
                                id="year"
                                min={1900} max={2023}

                                value={year} onChange={(e) => setYear(e.target.value)}
                            />
                        </div>
                        <div className="form-group mt-3">
                            <label for="terms">I accept the terms and conditions.</label>
                            <input
                                type="checkbox"
                                className="m-3 "
                                name="terms"
                                id="terms"
                                required

                            />
                        </div>
                        <div className="d-grid gap-2 mt-3">
                            <button type="submit" className="btn btn-success" >
                                Submit
                            </button>
                        </div>

                    </div>
                </form>
            </div >
        </>
    );
}

