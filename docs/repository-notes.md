# Atelier 3 – Notes sur la couche Repository

## 1. Choix des interfaces

Les neuf interfaces sont dans le package `com.example.demo.repository`, préfixées par `I`, et étendent toutes `JpaRepository<Entité, Long>`.

| Interface | Étend | Justification |
|---|---|---|
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet, `findAll` renvoie une `List`, tri, pagination, `saveAndFlush`. |
| IEmployeRepository | JpaRepository<Employe, Long> | Idem. |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | Idem. |
| IEquipementRepository | JpaRepository<Equipement, Long> | Idem. |
| IClientRepository | JpaRepository<Client, Long> | Idem. |
| IReservationRepository | JpaRepository<Reservation, Long> | Idem. |
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, `findAll` renvoie une `List`, `saveAndFlush` disponible. Les suppressions passent par le contexte de persistance : la cascade et l'`orphanRemoval` sur les paiements s'appliquent. |
| IPaiementRepository | JpaRepository<Paiement, Long> | Créé pour lire les paiements. Le code métier crée ou retire un paiement en passant par le `Contrat` (composition). |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | Idem. |

Pourquoi `JpaRepository` : il regroupe `ListCrudRepository`, `ListPagingAndSortingRepository` et `QueryByExampleExecutor`, et ajoute `flush`, `saveAndFlush`, `getReferenceById` et les suppressions en lot. Les résultats sont des `List` (et non des `Iterable` comme avec `CrudRepository`).

Attention : `deleteAllInBatch` et `deleteAllByIdInBatch` contournent le contexte de persistance. La cascade et l'`orphanRemoval` ne s'appliquent pas : supprimer des contrats ainsi laisserait des paiements orphelins ou échouerait sur la clé étrangère.

Vérification : au démarrage, les logs indiquent `Found 9 JPA repository interfaces.`

## 2. Anomalies de qualité relevées et corrigées

Relevées par relecture du code selon les règles SonarQube for IDE (SonarLint) ; à confirmer avec le plugin dans IntelliJ.

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| `import jakarta.persistence.*;` dans les 9 entités | `java:S2208` : les imports avec joker sont à éviter, on ne voit plus d'où vient chaque classe et des conflits de noms peuvent apparaître. | Remplacé par des imports explicites (`Entity`, `Id`, `GeneratedValue`, `ManyToOne`…). |
| `import lombok.*;` dans les 9 entités | `java:S2208` (même règle). Lombok ciblé demandé : uniquement les annotations utilisées. | Remplacé par `Getter`, `Setter`, `NoArgsConstructor`. Aucun `@Data` sur les entités. |
| Test `contextLoads()` vide dans `DemoApplicationTests` | `java:S2699` : un test doit contenir au moins une assertion. | Injection de `IVehiculeRepository` et `assertNotNull(vehiculeRepository)` : le test vérifie maintenant que la couche Repository est bien créée par Spring. |
