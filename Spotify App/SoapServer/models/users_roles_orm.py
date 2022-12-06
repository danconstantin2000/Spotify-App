from sqlalchemy import Column, String, Integer, Date, Table, ForeignKey

from base.sql_base import Base

class UsersRoles(Base):
    __tablename__ = 'UsersRoles'
    URID = Column(Integer, primary_key=True)
    UID = Column(Integer,ForeignKey('Users.UID'))
    RID= Column(Integer, ForeignKey('Roles.RID'))

    def __init__(self, UID,RID):
        self.UID=UID
        self.RID=RID

user_roles_relationship = Table(
    'UsersRoles', Base.metadata,
    Column('UID', Integer, ForeignKey('Users.UID')),
    Column('RID', Integer, ForeignKey('Roles.RID')),
    extend_existing=True
)
