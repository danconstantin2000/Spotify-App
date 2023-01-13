from models.user_orm import User
from base.sql_base import Session
import jwt
import datetime
def generate_token(uid, role):
    try:
        payload = {
            # iss
            'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=1),
            'sub': uid,
            'jti': uid,
            'role': role
        }
        return jwt.encode(
            payload,
            "secret",
            algorithm='HS256'
        )
    except Exception as e:
        return e

def decode_jwt_token(jwt_token):
    try:
        payload = jwt.decode(jwt_token, "secret", algorithms='HS256')
        return payload['sub'], payload['role']
    except jwt.ExpiredSignatureError:
        return -1, "expired"
    except jwt.InvalidTokenError:
        return -2, "invalid"

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
    session.expire_on_commit = False
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
    roles=[]
    if user:
        for role in user.roles:
            roles.append(role.RID)
        jwt_token=generate_token(user.UID,roles)
        return jwt_token
    return str({"mesaj":"user not found."})