import React, { useState } from "react"
import { useNavigate } from "react-router-dom";
export const Login = (props) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
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
            return fetch('http://localhost:8082/api/gateway/login', requestOptions)
                .then((response) => response.json())
                .then((data) => {

                    let user = {
                        jwt_token: data.jwt_token,
                        uid: data.uid,
                        roles: data.roles,
                        username: data.username
                    }
                    localStorage.setItem("user", JSON.stringify(user))
                    redirect()



                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        }
        fetchData()

    }
    return (
        <>
            {/* <div class="auth-form-container">
                <form onSubmit={handleSubmit} class="login-form">
                    <label for="username">Username:</label>
                    <input type="text" placeholder="username" id="username" name="username" value={username} onChange={(e) => setUsername(e.target.value)} />
                    <label for="password">Password:</label>
                    <input type="password" placeholder="password" id="password" name="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                    <button>Log In</button>
                </form>
                <button class="link-btn" onClick={() => props.onFormSwitch('register')}>Dont have an account? Register here.</button>

            </div> */}

            <div className="Auth-form-container">
                <form className="Auth-form" onSubmit={handleSubmit}>
                    <div className="Auth-form-content">
                        <h3 className="Auth-form-title">Sign In</h3>
                        <div className="form-group mt-3">
                            <label for="username">Username</label>
                            <input
                                type="text"
                                className="form-control mt-1"
                                placeholder="Enter username"
                                name="username"
                                id="username"
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
                                value={password} onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>
                        <div className="d-grid gap-2 mt-3">
                            <button type="submit" className="btn btn-success" >
                                Submit
                            </button>
                        </div>

                    </div>
                </form>
            </div>
        </>
    )
}