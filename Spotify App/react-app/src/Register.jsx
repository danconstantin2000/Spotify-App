
import { redirect } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from 'react';
export const Register = (props) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [successRegistered, setSuccessRestered] = useState('');
    useEffect(() => {
        setSuccessRestered(null)
    }, [])
    const navigate = useNavigate();
    const redirect = () => {
        navigate('/')
    }
    const handleSubmit = (e) => {
        e.preventDefault()
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: username, password: password })
        };
        const fetchData = () => {
            return fetch('http://localhost:8082/api/gateway/register', requestOptions)
                .then((response) => {
                    response.json()
                    if (response.status == 409) {
                        setSuccessRestered(false)
                    }
                    else if (response.status == 200) {
                        setSuccessRestered(true)
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
                            {successRegistered == true ? (
                                <div class="alert alert-success">Inregistrare cu success!</div>
                            ) : null}
                            {successRegistered == false ? (
                                <div class="alert alert-danger">Inregistrarea a esuat!</div>
                            ) : null}
                        </div>
                        <h3 className="Auth-form-title">Register In</h3>
                        <div className="form-group mt-3">
                            <label for="username">Username</label>
                            <input
                                type="text"
                                className="form-control mt-1"
                                placeholder="Enter username"
                                name="username"
                                id="username"
                                required minLength={3} maxLength={30}
                                value={username} onChange={(e) => setUsername(e.target.value)}
                            />
                        </div>
                        <div className="form-group mt-3">
                            <label for="password">Password</label>
                            <input
                                type="password"
                                className="form-control mt-1"
                                placeholder="Enter password"
                                name="password"
                                id="password"
                                required minLength={3} maxLength={30}
                                value={password} onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                        <div className="form-group mt-3">
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
    )
}