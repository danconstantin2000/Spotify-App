import { redirect } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from 'react';

export const AddPlaylist = (props) => {
    const [title, setTitle] = useState('');
    const [user, setUser] = useState('');
    const [sucessAdded, setSuccessAdded] = useState('');
    useEffect(() => {
        const auth = localStorage.getItem('user');
        let user = null
        if (auth) {
            user = JSON.parse(auth);
        }
        setUser(user)
        setSuccessAdded(null)

    }, [])
    const navigate = useNavigate();
    const redirect = () => {
        navigate('/')

    }

    const handleSubmit = (e) => {
        e.preventDefault()
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': "Bearer " + user.jwt_token },
            body: JSON.stringify({ title: title })
        };
        const fetchData = () => {
            return fetch('http://localhost:8082/api/gateway/addPlaylist', requestOptions)
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
                            {sucessAdded == true ? (
                                <div class="alert alert-success">Adaugare cu succes!</div>
                            ) : null}
                            {sucessAdded == false ? (
                                <div class="alert alert-danger">Adaugare esuata!</div>
                            ) : null}
                        </div>
                        <h3 className="Auth-form-title">Adauga playlist</h3>
                        <div className="form-group mt-3">
                            <label for="title">Titlu</label>
                            <input
                                type="text"
                                className="form-control mt-1"
                                placeholder="Enter the name:"
                                name="title"
                                id="title"
                                required minLength={3} maxLength={30}
                                value={title} onChange={(e) => setTitle(e.target.value)}
                            />
                        </div>


                        <div className="form-group mt-1">
                            <label for="terms">I accept the terms and conditions.</label>
                            <input
                                type="checkbox"
                                className="m-3 "
                                placeholder="Enter password"
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

