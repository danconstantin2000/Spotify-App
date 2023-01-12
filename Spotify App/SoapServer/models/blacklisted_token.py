from sqlalchemy import Column, String, Integer, DateTime
from base.sql_base import Base
import datetime
from base.sql_base import Session
class BlacklistToken(Base):
    __tablename__ = 'blacklistTokens'

    id = Column(Integer, primary_key=True, autoincrement=True)
    token = Column(String(500), unique=True, nullable=False)
    blacklisted_on = Column(DateTime, nullable=False)

    def __init__(self, token):
        self.token = token
        self.blacklisted_on = datetime.datetime.now()

    @staticmethod
    def checkIfBlackListed(jwt_token):
        session=Session()

        token = session.query(BlacklistToken).filter_by(token=str(jwt_token)).first()
        if token:
            return True
        else:
            return False