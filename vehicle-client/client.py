import itertools as it
import json
import math
import random
import time

import numpy as np
import requests
import base64

LATLNG_BOUNDS = ((40.75, 40.77), (-111.90, -111.88))  # SLC
AUTH_HEADER = "Basic " + str(base64.b64encode(b"admin:admin"), encoding='utf-8')  # TODO vehicle credentials?
SPEED = 0.0005


def main():
    VEHICLE_COUNT = 5

    get_vehicles = requests.get(
        "http://localhost:8070/vehicle-service/vehicle",
        headers={"Content-Type": "application/json"}
    )
    vehicles = json.loads(get_vehicles.text)

    vehicles += [create_vehicle() for _ in range(VEHICLE_COUNT - len(vehicles))]

    assign_routes_to(vehicles)
    print(vehicles)

    while True:
        time.sleep(random.random() * 2.5 / VEHICLE_COUNT)
        idx = random.randint(0, len(vehicles) - 1)
        update_vehicle(vehicles[idx])


# Random point chosen uniformly in an ellipse of given width and height
def random_ellipse(width, height=None):
    height = width if height is None else height
    phi = 2.0 * math.pi * random.random()
    rho = math.sqrt(random.random())
    x, y = rho * math.cos(phi), rho * math.sin(phi)
    return x * width * 0.5, y * height * 0.5


def create_vehicle():
    vehicle = {
        "name": random.choice(["Bus", "Car", "Train"]) + f"{random.randint(0, 10 ** 9 - 1):09}",
        "routeId": 0,
        "routeIdx": 0,
        "lat": 0.5 * sum(LATLNG_BOUNDS[0]),
        "lng": 0.5 * sum(LATLNG_BOUNDS[1])
    }

    post_req = requests.post(
        "http://localhost:8070/vehicle-service/vehicle",
        headers={"Content-Type": "application/json", "Authorization": AUTH_HEADER},
        data=json.dumps(vehicle)
    )
    if post_req.status_code != 200:
        raise ConnectionError("Failed to create vehicle: " + post_req.text)
    print('Created Vehicle:', post_req.text)
    return json.loads(post_req.text)


def update_location(vehicle):
    target = vehicle["route"]["stops"][vehicle["routeIdx"]]
    if "lat" not in vehicle:
        vehicle["lat"] = target["lat"]
    if "lng" not in vehicle:
        vehicle["lng"] = target["lng"]

    dir_vector = np.array([target["lat"] - vehicle["lat"], target["lng"] - vehicle["lng"]])
    dir_vector /= np.linalg.norm(dir_vector)
    dir_vector *= random.random() * SPEED
    dir_vector += np.array([random.random() for _ in range(2)]) * 0.0001
    vehicle["lat"] += dir_vector[0]
    vehicle["lng"] += dir_vector[1]

    if math.hypot(vehicle["lat"] - target["lat"], vehicle["lng"] - target["lng"]) < SPEED:
        vehicle["routeIdx"] = (vehicle["routeIdx"] + 1) % len(vehicle["route"]["stops"])

    return {"lat": vehicle["lat"], "lng": vehicle["lng"]}


def update_vehicle(vehicle):
    if vehicle['route'] is None:
        return
    updates = update_location(vehicle)
    updates["routeIdx"] = vehicle["routeIdx"]
    updates["routeId"] = vehicle["routeId"]

    patch_req = requests.patch(
        f"http://localhost:8070/vehicle-service/vehicle/{vehicle['id']}",
        headers={"Content-Type": "application/json", "Authorization": AUTH_HEADER},
        data=json.dumps(updates)
    )
    if patch_req.status_code != 200:
        raise ConnectionError("Failed to update vehicle: " + patch_req.text)
    print('Updated Vehicle:', patch_req.text)
    return json.loads(patch_req.text)


def assign_routes_to(vehicles):
    get_req = requests.get(
        "http://localhost:8070/stops-service/route",
        headers={"Content-Type": "application/json"}
    )
    routes = json.loads(get_req.text)
    for vehicle in vehicles:
        route = None
        for rt in routes:
            if rt['id'] == vehicle['routeId']:
                route = rt
                break
        vehicle['route'] = route

    for vehicle, route in zip(vehicles, it.cycle(routes)):
        if 'stops' not in route or 'route' in vehicle:
            continue
        vehicle['route'] = route
        vehicle['routeIdx'] = random.randint(0, len(route) - 1)
        vehicle["routeId"] = route["id"]


if __name__ == "__main__":
    main()
