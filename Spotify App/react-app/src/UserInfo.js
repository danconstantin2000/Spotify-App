

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
                    <div class="container d-flex flex-column mb-3 song p-4 bg-dark songInfo ">

                        <div class="mb-3 container d-flex justify-content-around col-3"> <div>Username:</div> <div>{user.username}</div></div>
                        <div class="mb-3 container d-flex justify-content-around col-3"><div>Id:</div><div>{user.uid}</div></div>
                        <div class="mb-3 container d-flex justify-content-around col-3">Roluri</div>
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