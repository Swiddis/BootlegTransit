import requests
import time
import random
import math
import json

def main():
    VEHICLE_COUNT = 5

    get_vehicles = requests.get(
        "http://localhost:8070/vehicle-service/vehicle",
        headers = {"Content-Type": "application/json"}
    )
    vehicles = json.loads(get_vehicles.text)

    vehicles += [create_vehicle() for _ in range(VEHICLE_COUNT - len(vehicles))]

    while True:
        time.sleep(random.random())
        idx = random.randint(0, len(vehicles) - 1)
        vehicles[idx] = update_vehicle(vehicles[idx])

# Random point chosen uniformly in an ellipse of given width and height
def random_ellipse(width, height=None):
    height = width if height is None else height
    phi = 2.0 * math.pi * random.random()
    rho = math.sqrt(random.random())
    x, y = rho * math.cos(phi), rho * math.sin(phi)
    return (x * width * 0.5, y * height * 0.5)

def create_vehicle():
    vehicle = {
        "name": random.choice(["Bus", "Car", "Train"]) + str(random.randint(10**8, 10**9-1)),
        "routeId": 0,
        "routeIdx": 0,
        "lat": 0.0,
        "lng": 0.0
    }

    post_req = requests.post(
        "http://localhost:8070/vehicle-service/vehicle",
        headers={"Content-Type": "application/json"},
        data = json.dumps(vehicle)
    )
    if post_req.status_code != 200:
        raise ConnectionError("Failed to create vehicle: " + post_req.text)
    print('Created Vehicle:', post_req.text)
    return json.loads(post_req.text)

def update_vehicle(vehicle):
    update_lat, update_lng = random_ellipse(5)
    updates = {
        "lat": max(min(round(vehicle["lat"] + update_lat, 4), 180), -180),
        "lng": max(min(round(vehicle["lng"] + update_lng, 4), 90), -90)
    }

    patch_req = requests.patch(
        f"http://localhost:8070/vehicle-service/vehicle/{vehicle['id']}",
        headers={"Content-Type": "application/json"},
        data = json.dumps(updates)
    )
    if patch_req.status_code != 200:
        raise ConnectionError("Failed to update vehicle: " + patch_req.text)
    print('Updated Vehicle:', patch_req.text)
    return json.loads(patch_req.text)

if __name__ == "__main__":
    main()
