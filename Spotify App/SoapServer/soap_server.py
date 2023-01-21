from spyne import Application, rpc, ServiceBase, Integer, Double, String, Boolean, Iterable,ComplexModel
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
from repositories.role_repository import get_roles
from repositories.user_repository import get_users, create_user,update_user_password,delete_user,get_user,check_registered_user, decode_jwt_token
from repositories.users_roles_repository import assign_role,delete_role,edit_role
import hashlib
from base.sql_base import Session
from models.blacklisted_token import  BlacklistToken
import json


def toComplexModelRole(roles):
    returned_roles=[]
    for r in roles:
        returned_roles.append(ComplexModelRole(r.RID,r.role))
    return returned_roles
def toComplexModelUser(users):
    returned_users=[]
    for user in users:
        returned_users.append(ComplexModelUser(user.UID,user.username,toComplexModelRole(user.roles)))
    return returned_users

class ComplexModelRole(ComplexModel):
    _type_info = {
        'RID': Integer,
        'role': String,
    }
    def __init__(self, RID,role):
        self.RID=RID
        self.role = role
class ComplexModelUser(ComplexModel):
    _type_info = {
        'UID': Integer,
        'username': String,
        'roles':Iterable(ComplexModelRole)
    }
    def __init__(self, UID,username,roles):
        self.UID=UID
        self.username=username
        self.roles=roles

class IDMService(ServiceBase):
    @rpc(String, String, _returns=Integer)
    def add_user(ctx, username, password):

        print("Creating user:")
        hashed_password=hashlib.sha256(str.encode(password))
        hashed_password_hex=hashed_password.hexdigest()
        new_user = create_user(username, hashed_password_hex)
        if new_user:
            users_roles = assign_role(new_user.UID, 3)
            if users_roles:
                return new_user.UID;
            else:
                return -1
            return new_user.UID
        else:
            return -1

    @rpc(Integer,String,String,_returns=Boolean)
    def change_password(ctx,UID,password,jwt_token):
        output = IDMService.authorize(ctx, jwt_token)
        if output == "ERROR!":
            return False
        user_json = json.loads(output)
        if(user_json['uid']!=UID):
            return False
        hashed_password=hashlib.sha256(str.encode(password))
        hashed_password_hex=hashed_password.hexdigest()
        return update_user_password(UID,hashed_password_hex)
        
    @rpc(Integer,Integer,String,_returns=Boolean)
    def assign_user_role(ctx,UID,RID,jwt_token):
        output = IDMService.authorize(ctx, jwt_token)
        if output == "ERROR!":
            return False
        user_json = json.loads(output)
        roles = user_json["roles"]
        # if CONTENT_MANAGER/CLIENT <-ADMIN

        if 4 in roles:
            users_roles=assign_role(UID,RID)
            if users_roles:
                return True
            else:
                return False
        return False



    @rpc(Integer,String, _returns=Boolean)
    def delete_user_service(ctx,UID,jwt_token):
        output = IDMService.authorize(ctx, jwt_token)
        if output == "ERROR!":
            return False
        user_json = json.loads(output)
        roles = user_json["roles"]
        # if admin
        if 4 in roles:
            return delete_user(UID)
        return False

    @rpc(Integer,Integer,String,_returns=Boolean)
    def delete_user_role(ctx,UID,RID,jwt_token):
        output = IDMService.authorize(ctx, jwt_token)
        if output == "ERROR!":
            return False
        user_json = json.loads(output)
        roles = user_json["roles"]
        if 4 in roles:
            return delete_role(UID,RID)
        return False


    @rpc(Integer,Integer,Integer,String,_returns=Boolean)
    def edit_user_role(ctx,UID,RID,new_RID,jwt_token):
        output = IDMService.authorize(ctx, jwt_token)
        if output == "ERROR!":
            return False
        user_json = json.loads(output)
        roles = user_json["roles"]
        print(roles)
        if new_RID == 2:
            if 1 in roles:
                return edit_role(UID,RID,new_RID)
        if 4 in roles:
            return edit_role(UID,RID,new_RID)
        return False

    @rpc(Integer,_returns=[Integer,String,Iterable(ComplexModelRole)],_out_variable_names=['UID', 'username','roles'])
    def get_user(ctx,UID):
        user=get_user(UID)
        return user.UID,user.username,toComplexModelRole(user.roles)

    @rpc(String,_returns=Iterable(ComplexModelUser))
    def get_users(ctx,jwt_token):

        output=IDMService.authorize(ctx,jwt_token)
        if output == "ERROR!":
            return None
        user_json=json.loads(output)
        roles=user_json["roles"]
        #if admin
        if 4 in roles:
            return toComplexModelUser(get_users())
        else:
            return None

        
    @rpc(String, _returns=Iterable(ComplexModelRole))
    def get_roles(ctx,jwt_token):
        output = IDMService.authorize(ctx, jwt_token)
        if output == "ERROR!":
            return None
        user_json = json.loads(output)
        roles = user_json["roles"]
        # if admin
        if 4 in roles:
            return toComplexModelRole(get_roles())
        else:
            return None

    @rpc(String,String, _returns=String)
    def login(ctx,username,password):
        hashed_password=hashlib.sha256(str.encode(password))
        hashed_password_hex=hashed_password.hexdigest()
        return check_registered_user(username,hashed_password_hex)


    @rpc(String,_returns=String)
    def authorize(ctx, jwt_token):
        blacklist = BlacklistToken(jwt_token)


        uid,roles=decode_jwt_token(jwt_token)
        if uid == -1 or uid == -2:
            return "ERROR!"
        user=get_user(uid)
        dataBaseRoles=[]
        for role in user.roles:
            dataBaseRoles.append(role.RID)
        roles.sort()
        dataBaseRoles.sort()
        if roles!=dataBaseRoles:
            session=Session()
            session.add(blacklist)
            session.commit()
            return "ERROR!"

        user={"uid":uid,"roles":roles}
        return json.dumps(user)

    @rpc(String, _returns=String)
    def logout(ctx,jwt_token):
        blacklist = BlacklistToken(jwt_token)
        session = Session()
        session.add(blacklist)
        session.commit()
        return "SUCCESS"

application = Application([IDMService], 'services.IDM.soap',
                          in_protocol=Soap11(validator='lxml'),
                          out_protocol=Soap11())

wsgi_application = WsgiApplication(application)

if __name__ == '__main__':
    import logging

    from wsgiref.simple_server import make_server

    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:8000")
    logging.info("wsdl is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, wsgi_application)
    server.serve_forever()
