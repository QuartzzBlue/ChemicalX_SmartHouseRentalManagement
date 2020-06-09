#-*-coding: utf-8 -*-

# flask 에서 Flask 라는 클래스를 불러온다
from flask import Flask

from flask_restful import reqparse, abort, Api, Resource

# json 정의
USERS = {
    'USER1' : {'name' : 'Yeojin'},
    'USER2' : {'name' : 'Seul'},
    'USER3' : {'name' : 'HM'},
}

# 예외처리
def abort_if_users_doesnt_exist(userID):
    if userID not in USERS:
        abort(404,message="USER{} doesn't exist".format(userID))

parser = reqparse.RequestParser()
parser.add_argument('name')

# Flask 라는 클래스의 객체를 생성하고 인수로 __name__ 을 입력한다
# 해당 객체의 이름은 app 이 된다.
app = Flask(__name__) 
api = Api(app)

#USER . get, delete, put
class User(Resource):
    def get(self,userID):
        abort_if_users_doesnt_exist(userID)
        return USERS[userID]

    def delete(self,userID):
        abort_if_users_doenst_exist(userID)
        del USERS[userID]
        return '',204

    def put(self,userID):
        args = parser.parse_args()
        name = {'name':args['name']}
        USERS[userID] = name
        return name,201

#users get,post 정의
class UserList(Resource):
    def get(self) : 
        return USERS

    def post(self): 
        args = parser.parse_args()
        userID = 'user%d' % (len(USERS)+1)
        USERS[userID] = {'name':args['name']}
        return USERS[userID],201

# URL Router 에 맵핑 (Rest URL 정의)
api.add_resource(UserList,'/users/')
api.add_resource(User,'/users/<string:userID>')


####다른 예제
# 생성한 객체의 route (URL) 을 설정한다.
@app.route("/") 
# 함수를 만들고 기능을 정의한다
def hello(): 
    return "hello, world!"
####


# 객체의 run 함수를 이용하여 로컬 서버에서 어플리케이션을 실행하도록 한다.
if __name__ == '__main__':
    app.run(host='0.0.0.0')
