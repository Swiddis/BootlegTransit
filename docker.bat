CALL mvn package -f build-all -DskipTests && docker-compose up --build & docker-compose down & docker image prune -f

pause