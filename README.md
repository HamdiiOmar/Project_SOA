# Project SOA – Car Rental Management

Spring Boot back-end for a car rental company: agencies, vehicles, clients, reservations, contracts and payments, persisted in MySQL with Spring Data JPA.

## Tech stack

- Java 17
- Spring Boot 4.0.8 (Web MVC, Data JPA)
- MySQL 8
- Lombok
- Maven (wrapper included)

## Domain model

| Entity | Description |
|---|---|
| `Agence` | A rental agency |
| `Employe` | Employee of an agency (role: `AGENT`, `MANAGER`) |
| `Vehicule` | A vehicle (category, status) belonging to an agency |
| `Equipement` | An optional equipment (GPS, baby seat, ...) |
| `Maintenance` | A maintenance operation on a vehicle |
| `Client` | A customer |
| `Reservation` | A client's booking of a vehicle (`EN_ATTENTE`, `CONFIRMEE`, `ANNULEE`, `TERMINEE`) |
| `Contrat` | The contract generated from a reservation |
| `Paiement` | A payment (`CARTE`, `ESPECES`, `VIREMENT`) made on a contract |

Enums: `CategorieVehicule` (`CITADINE`, `BERLINE`, `SUV`, `UTILITAIRE`), `StatutVehicule` (`DISPONIBLE`, `LOUEE`, `MAINTENANCE`), `RoleEmploye`, `StatutReservation`, `ModePaiement`.

### Relationships

| Relationship | Type |
|---|---|
| Agence → Employe | One-to-many |
| Agence → Vehicule | One-to-many |
| Vehicule → Maintenance | One-to-many |
| Vehicule ↔ Equipement | Many-to-many (join table `vehicule_equipement`, owned by `Vehicule`) |
| Client → Reservation | One-to-many |
| Vehicule → Reservation | One-to-many |
| Reservation ↔ Contrat | One-to-one |
| Contrat → Paiement | One-to-many, composition (cascade + orphan removal) |

## Project structure

```
demo/src/main/java/com/example/demo
├── entity/       JPA entities
├── enums/        Enumerations
├── repository/   Spring Data JPA repositories
└── DemoApplication.java
```

## Getting started

### Prerequisites

- JDK 17+
- MySQL running on `localhost:3306`

### Configuration

Edit `demo/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/OmarTheBoss?createDatabaseIfNotExist=true
spring.datasource.username=<your-user>
spring.datasource.password=<your-password>
spring.jpa.hibernate.ddl-auto=update
server.port=8081
```

The database is created automatically and the tables are generated from the entities on startup.

### Run

```bash
cd demo
./mvnw spring-boot:run
```

The application starts on <http://localhost:8081>.

## Author

Omar Hamdi
