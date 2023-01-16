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
        navigate("/")
    }
    return (
        <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
            <Container>
                <Navbar.Brand href="/">Spotify App</Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="me-auto">

                        <Nav.Link href="/about">About</Nav.Link>
                        {auth ? (
                            <>
                                <Nav.Link href="/user_profile">{user.username}</Nav.Link>
                                <Nav.Link onClick={logout}>Logout</Nav.Link>
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