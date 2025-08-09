from flask import Flask

app = Flask(__name__)

@app.route("/")
def mainpage():
    return "안녕하세요, 이음마켓의 Flask 서버 메인 페이지입니다."

if __name__ == "__main__":
    app.run(debug=True)