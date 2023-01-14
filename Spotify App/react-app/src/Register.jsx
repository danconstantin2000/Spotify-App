import { useState } from "react";

export const Register = (props) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [aggreed, setAgreed] = useState('');
    const handleSubmit = (e) => {
        e.preventDefault()
        console.log(username)
        console.log(password)
        console.log(e.confirmPassword);

    }
    return (
        <>
            <div class="auth-form-container" >
                <form onSubmit={handleSubmit} class="register-form">
                    <label for="username">Username:</label>
                    <input type="text" placeholder="username" id="username" name="username" value={username} onChange={(e) => setUsername(e.target.value)} />
                    <label for="password">Password:</label>
                    <input type="password" placeholder="password" id="password" name="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                    <label for="confirmPassword">Password:</label>
                    <input type="password" placeholder="password" id="confirmPassword" name="confirmPassword" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} />

                    <label for="agreed">I agree terms and conditions.</label>
                    <input
                        type="checkbox"
                        id="agreed"
                        name="agreed"
                        value={aggreed} onChange={(e) => setAgreed(e.target.value)} />

                    <button>Log In</button>
                </form>
                <button class="link-btn" onClick={() => props.onFormSwitch('login')}>Already have an account? Login here.  </button>
            </div>
        </>
    )
}