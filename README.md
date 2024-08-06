To start postgres DB:
docker run --name pg1 -p 5432:5432 -e POSTGRES_USER=tadaah -e POSTGRES_PASSWORD=pass@123 -e POSTGRES_DB=freelancedb -d postgres:15-alpine

docker compose up

mvn clean install

run appplication

postman