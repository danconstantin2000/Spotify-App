

import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import { Navbar } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

function NavigationBar() {
    const auth = localStorage.getItem('user');
    const navigate = useNavigate()
    let user = ""
    if (auth) {
        user = JSON.parse(auth);
    }
    const logout = () => {
        localStorage.clear();
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
        };
        const fetchData = () => {
            return fetch('http://localhost:8082/api/gateway/logout', requestOptions)
                .then((response) => response.json())
                .then((data) => {


                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        }
        fetchData()
        navigate("/")
    }
    return (
        <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
            <Container>
                <Navbar.Brand href="/">Spotify App</Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="me-auto">

                        <Nav.Link href="/songs">Songs</Nav.Link>
                        <Nav.Link href="/artists">Artists</Nav.Link>
                        {auth ? (
                            <>
                                <Nav.Link href="/user_profile">{user.username}</Nav.Link>
                                <Nav.Link onClick={logout}>Logout</Nav.Link>

                                {user.roles.includes(4) ? (
                                    <Nav.Link href="/addContentManager" >Add Content Manager</Nav.Link>

                                ) : (
                                    user.roles.includes(1) ? (
                                        <Nav.Link href="/addArtist" >Add artist</Nav.Link>

                                    ) : (
                                        user.roles.includes(2) ? (
                                            <>
                                                <Nav.Link href="/addSong" >Add Song</Nav.Link>
                                                <Nav.Link href="/mySongs" >My Songs</Nav.Link>
                                            </>
                                        ) : null
                                    ))}
                            </>

                        ) : (<>
                            <Nav.Link href="/login">Login</Nav.Link>
                            <Nav.Link href="/register">Register</Nav.Link>
                        </>

                        )}


                    </Nav>

                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavigationBar;