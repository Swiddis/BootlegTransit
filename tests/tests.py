import requests
import json

# Vehicle tests

sample_vehicles = [{
    "name": "Bus1",
    "routeId": 1,
    "routeIdx": 0,
    "lat": 0.0,
    "lng": 0.0
}]

post_req = requests.post(
    "http://localhost:8070/vehicle-service/vehicle",
    headers={"Content-Type": "application/json"},
    data = json.dumps(sample_vehicles[0])
)

try:
    assert post_req.status_code == 200
    sample_vehicles[0].update(json.loads(post_req.text))
except AssertionError:
    print(post_req.status_code)
    print(post_req.text)

# Stop tests

sample_routes = [{
    "stops": [
        {"address": "1234 North Lane"},
        {"address": "1234 South Lane"}
    ]
}]

post_req = requests.post(
    "http://localhost:8070/stops-service/route",
    headers={"Content-Type": "application/json"},
    data = json.dumps(sample_routes[0])
)

try:
    assert post_req.status_code == 200
    sample_routes[0].update(json.loads(post_req.text))
except AssertionError:
    print(post_req.status_code)
    print(post_req.text)
