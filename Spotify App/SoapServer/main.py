# python -m pip install mariadb sqlalchemy

from repositories.role_repository import get_roles
from repositories.user_repository import get_users, create_user, update_user_password,delete_user

if __name__ == "__main__":
    
    delete_user(2)
    

    
    # print("\nUsers:")
    # for user in get_users():
    #     print(f"{user.UID} - {user.username} - {user.password} - roles: ", end="")
    #     for r in user.roles:
    #         print(f"{r.role} ", end="")
    #     print()

    # print("\n\nRoles:")
    # for r in get_roles():
    #     print(f"{r.role}")
