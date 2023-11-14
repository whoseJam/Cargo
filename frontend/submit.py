import requests
from datamaker import makeData
import time
import json

def launchTask(data):
    url = "http://127.0.0.1:8080/cargo/solve/async"
    res = requests.post(url, json={
        "problem": data,
        "token": "FKkdfmiweo9fksF"
    })
    res = json.loads(res.text)
    if not res["success"]:
        raise Exception(res["message"])
    problemId = res["message"]
    return problemId

def querySolution(problemId):
    url = "http://127.0.0.1:8080/cargo/get/solution"
    res = requests.post(url, json={
        "problemId": problemId,
        "token": "FKkdfmiweo9fksF"
    })
    res = json.loads(res.text)
    if not res["success"]:
        raise Exception(res["message"])
    return json.loads(res["message"])

def stopTask(problemId):
    url = "http://127.0.0.1:8080/cargo/solve/stop"
    res = requests.post(url, json={
        "problemId": problemId,
        "token": "FKkdfmiweo9fksF"
    })
    res = json.loads(res.text)
    if not res["success"]:
        raise Exception(res["message"])
    return res["message"]

if __name__ == "__main__":
    problemId = launchTask(makeData(3, 3, 100))
    print("problemId =", problemId)
    try:
        print("start to solve problem")
        for i in range(50):
            time.sleep(1)
            solution = querySolution(problemId)
            print("iter =", i, "score =", solution["hard_score"], solution["medium_score"], solution["soft_score"])
        print("terminate solve problem")
        stopTask(problemId)
    except Exception as err:
        print("ternimate solve problem err =", err)
        stopTask(problemId)
    
    print(solution)