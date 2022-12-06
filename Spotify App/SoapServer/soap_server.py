from spyne import Application, rpc, ServiceBase, Integer, Double, String, Boolean, Iterable,ComplexModel
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
from repositories.role_repository import get_roles
from repositories.user_repository import get_users, create_user,update_user_password,delete_user,get_user,check_registered_user
from repositories.users_roles_repository import assign_role,delete_role,edit_role
import hashlib

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
    @rpc(String, String, _returns=Boolean)
    def add_user(ctx, username, password):
        print("Creating user:")
        hashed_password=hashlib.sha256(str.encode(password))
        hashed_password_hex=hashed_password.hexdigest()
        new_user = create_user(username, hashed_password_hex)
        if new_user:
            return True
        else:
            return False

    @rpc(Integer,String,_returns=Boolean)
    def change_password(ctx,UID,password):
        hashed_password=hashlib.sha256(str.encode(password))
        hashed_password_hex=hashed_password.hexdigest()
        return update_user_password(UID,hashed_password_hex)
        
    @rpc(Integer,Integer,_returns=Boolean)   
    def assign_user_role(ctx,UID,RID):
        users_roles=assign_role(UID,RID)
        if users_roles:
            return True
        else:
            return False

    @rpc(Integer, _returns=Boolean)
    def delete_user_service(ctx,UID):
        return delete_user(UID)

    @rpc(Integer,Integer,_returns=Boolean)
    def delete_user_role(ctx,UID,RID):
        return delete_role(UID,RID)


    @rpc(Integer,Integer,Integer,_returns=Boolean)
    def edit_user_role(ctx,UID,RID,new_RID):
        return edit_role(UID,RID,new_RID)


    @rpc(Integer,_returns=[Integer,String,Iterable(ComplexModelRole)],_out_variable_names=['UID', 'username','roles'])
    def get_user(ctx,UID):
        user=get_user(UID)
        return user.UID,user.username,toComplexModelRole(user.roles)

    @rpc(_returns=Iterable(ComplexModelUser))
    def get_users(ctx):
        return toComplexModelUser(get_users())
        
    @rpc(_returns=Iterable(ComplexModelRole))
    def get_roles(ctx):
        return toComplexModelRole(get_roles())

    @rpc(String,String, _returns=Boolean)
    def login(ctx,username,password):
        hashed_password=hashlib.sha256(str.encode(password))
        hashed_password_hex=hashed_password.hexdigest()
        return check_registered_user(username,hashed_password_hex)
    
    @rpc(Integer,Integer,_returns=Boolean)
    def check_user_role(ctx,UID,RID):
        user=get_user(UID)
        for role in user.roles:
            if role.RID == RID:
                return True
        return False

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
