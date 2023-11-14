import random

locId = 0
repId = 0
carId = 0

def rand(l, r):
    return random.uniform(l, r)

def randLocation():
    global locId
    locId = locId + 1
    return {
        "latitude": rand(0, 100),
        "longitude": rand(0, 100),
        "name": "loc." + str(locId)
    }

def randRecipient():
    global repId
    repId = repId + 1
    return {
        "id": repId,
        "size": rand(1, 3),
        "location": randLocation()
    }

def randRecipients(n):
    recipients = []
    for i in range(n):
        recipients.append(randRecipient())
    return recipients

def randCar():
    global carId
    carId = carId + 1
    return {
        "id": carId,
        "capacity": 50,
        "location": randLocation()
    }

def randCars(n):
    cars = []
    for i in range(n):
        cars.append(randCar())
    return cars

def randStorage():
    return {
        "size": 50000,
        "location": randLocation()
    }

def randStorages(n):
    storages = []
    for i in range(n):
        storages.append(randStorage())
    return storages

def makeData(ncar, nstore, nrep):
    return {
        "cars": randCars(ncar),
        "storages": randStorages(nstore),
        "recipients": randRecipients(nrep)
    }