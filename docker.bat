CALL mvn package -f build-all -DskipTests
CALL docker-compose up --build
PAUSE
CALL docker-compose down
CALL docker image prune -f
