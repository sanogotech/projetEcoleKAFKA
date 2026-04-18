@echo off
REM Lancement local — profil dev (H2, simulation Kafka sans broker)
set SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
