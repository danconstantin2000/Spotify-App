

function UserInfo() {
    const auth = localStorage.getItem('user');
    let user = ""
    if (auth) {
        user = JSON.parse(auth);
    }
    user.roles.map((role) => {
        console.log(role)
    })
    function renderRoles(param) {
        switch (param) {
            case 1: return 'CONTENT-MANAGER';
            case 2: return 'ARTIST';
            case 3: return 'CLIENT';
            case 4: return "ADMIN";
        }
    }
    return (
        <>
            {auth ? (
                <>
                    <div class="container info">

                        <div>Username:{user.username}</div>
                        <div>Id:{user.uid}</div>
                        <div>Roluri</div>
                        {user.roles.map((role) => (
                            <li >{renderRoles(role)}</li>
                        ))}
                    </div>

                </>

            ) : (
                <>

                </>
            )}
        </>
    );
}

export default UserInfo;