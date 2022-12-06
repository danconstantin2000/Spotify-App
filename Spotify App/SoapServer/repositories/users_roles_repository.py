from base.sql_base import Session
from sqlalchemy.dialects.mysql import insert
from models.users_roles_orm import UsersRoles

def assign_role(userID,roleID):
    session=Session()
    users_roles=UsersRoles(userID,roleID)
    size_users_roles=len(session.query(UsersRoles).filter(UsersRoles.UID == userID).filter(UsersRoles.RID==roleID).all())
    print(size_users_roles)
    if size_users_roles!=0:
        return None
    try:
        session.add(users_roles)
        session.commit()
    except Exception as exc:
        print(f"Failed to add users_roles - {exc}")
        return None
    return users_roles




def delete_role(userID,roleID):
    session=Session()
    ret_code=session.query(UsersRoles).filter(UsersRoles.UID == userID).filter(UsersRoles.RID==roleID).delete()
    if ret_code==0:
        return False
    session.commit()
    return True

def edit_role(userID,old_roleID,new_roleID):
    session=Session()
    try:
        ret_code=session.query(UsersRoles).filter(UsersRoles.UID == userID).filter(UsersRoles.RID==old_roleID).update({'RID':new_roleID})
    except:
        return False
    if ret_code == 0:
        return False
    else:
        session.commit()
        return True
