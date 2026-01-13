# Tennis Dubbel Backend

Spring Boot backend för Tennis Dubbel-applikationen.

## Krav

- Java 17+ installerat (se nedan om du inte har)

## Starta backend (utveckling med H2)

**Ingen Maven-installation behövs!** Använd Maven Wrapper:

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

Första gången laddas Maven ner automatiskt.

Servern startar på `http://localhost:8080`

## H2 Console (utveckling)

Öppna `http://localhost:8080/h2-console` i webbläsaren.

JDBC URL: `jdbc:h2:file:./data/tennisdb`
User: `sa`
Password: (tom)

## Starta med MySQL (produktion)

1. Skapa MySQL-databas:
```sql
CREATE DATABASE tennisdb;
CREATE USER 'tennis_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON tennisdb.* TO 'tennis_user'@'localhost';
```

2. Uppdatera `application-prod.properties` med rätt lösenord

3. Starta med prod-profil:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## API Endpoints

### Säsonger
- `GET /api/sasonger` - Hämta alla säsonger
- `GET /api/sasonger/{id}` - Hämta specifik säsong
- `GET /api/sasonger/aktiva` - Hämta aktiva säsonger
- `POST /api/sasonger` - Skapa ny säsong
- `PUT /api/sasonger/{id}` - Uppdatera säsong
- `PATCH /api/sasonger/{id}/avsluta` - Avsluta säsong
- `DELETE /api/sasonger/{id}` - Ta bort säsong

### Grupper
- `GET /api/grupper` - Hämta alla grupper
- `GET /api/grupper/{id}` - Hämta specifik grupp
- `GET /api/grupper/sasong/{sasongId}` - Hämta grupper för säsong
- `POST /api/grupper` - Skapa ny grupp
- `POST /api/grupper/{gruppId}/spelare/{spelareId}` - Lägg till spelare
- `DELETE /api/grupper/{gruppId}/spelare/{spelareId}` - Ta bort spelare

### Spelare
- `GET /api/spelare` - Hämta alla spelare
- `GET /api/spelare/aktiva` - Hämta aktiva spelare
- `GET /api/spelare/search?q=namn` - Sök spelare
- `POST /api/spelare` - Skapa ny spelare
- `PUT /api/spelare/{id}` - Uppdatera spelare

### Matcher
- `GET /api/matcher/sasong/{sasongId}` - Hämta matcher för säsong
- `GET /api/matcher/grupp/{gruppId}` - Hämta matcher för grupp
- `GET /api/matcher/sasong/{sasongId}/ospelade` - Hämta ospelade matcher
- `POST /api/matcher/{id}/resultat` - Rapportera resultat
- `POST /api/matcher/{id}/walkover?vinnare=1` - Sätt walkover

### Tabell
- `GET /api/tabell/grupp/{gruppId}` - Beräkna tabell för grupp
