@echo off
cd /d "%~dp0"
echo Portail GUCE CI sur http://localhost:8090
echo Demarrez les microservices (8080-8089) avant les formulaires.
mvn spring-boot:run
