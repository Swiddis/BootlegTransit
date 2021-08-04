CALL mvn package -f parent/build-all -DskipTests && docker-compose up --build & docker-compose down & docker image prune -f

pause