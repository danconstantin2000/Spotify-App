from models.user_orm import User
from base.sql_base import Session


def get_users():
    session = Session()
    users = session.query(User).all()
    return users

def get_user(UID):
    session=Session()
    user=session.query(User).get(UID)
    return user
    
def create_user(username, password):
    session = Session()
    user = User(username, password)
    try:
        session.add(user)
        session.commit()
    except Exception as exc:
        print(f"Failed to add user - {exc}")
        return None
    return user

def update_user_password(UID,password):
    session = Session()
    # if returns 0, user not updated
    ret_code=session.query(User).filter(User.UID == UID).update({'password':password})
    if ret_code == 0:
        return False
    else:
        session.commit()
        return True

def delete_user(UID):
    session=Session()
    user=session.query(User).get(UID)
    if user:     
        session.delete(user)
        session.commit()
        return True
    else:
        return False

def check_registered_user(username,password):
    session=Session()
    user=session.query(User).filter(User.username==username).filter(User.password==password).first()
    if user:
        return True
    return False