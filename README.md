# Bootleg Transit
A small transit application made for a course in service-based software architecture. It's split up into various services, scripts, and sub-projects, all combining to make the full application.

# Building and Running
Clone the repository
```bash
git clone https://github.com/SwiddisZwei/BootlegTransit.git
```

Run the appropriate startup script for your platform. You'll need Docker.
```bash
cd BootlegTransit
.\docker.bat
.\docker.sh
```

Once the application starts up, the front-end should be available at `localhost:3000`.

To add vehicles and have them move, you'll need to run the python script for the vehicle client. In the vehicle-client folder, with python installed, install the requirements and run it. This should be done after routes are created using the stops-service API.
```bash
pip install -r requirements.txt
py client.py
```
