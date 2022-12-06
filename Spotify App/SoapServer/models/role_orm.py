from sqlalchemy import Column, String, Integer
from base.sql_base import Base


class Role(Base):
    __tablename__ = 'Roles'

    RID = Column(Integer, primary_key=True)
    role = Column(String)

    def __init__(self, role):
        self.role = role
