# Atelier 8 – Affectation, désaffectation et ajout en cascade

Packages : `entity` (méthodes de synchronisation), `service` / `service.implementation` (opérations transactionnelles), `controller` (endpoints documentés SpringDoc), `exception` (`ResourceNotFoundException`, `BusinessRuleException`, `GlobalExceptionHandler`).

Codes HTTP : 404 identifiant inexistant (`ResourceNotFoundException`), 409 opération refusée par une règle de gestion (`BusinessRuleException`, ou contrainte d'intégrité), 400 données invalides (`IllegalArgumentException`, JSON illisible), 201 création, 204 suppression sans contenu.

## 1. Relations traitées

| Relation | Association | Côté propriétaire | `mappedBy` | Cascade | `orphanRemoval` | Chargement |
|---|---|---|---|---|---|---|
| Agence – Employe (obligatoire) | OneToMany / ManyToOne | `Employe` (`agence_id`) | `Agence.employes` → `"agence"` | ALL (déjà en place sur `Agence.employes`) | non | `employes` LAZY, `agence` EAGER (défauts JPA) |
| Vehicule – Equipement (obligatoire) | ManyToMany | `Vehicule` (`@JoinTable vehicule_equipement`) | `Equipement.vehicules` → `"equipements"` | aucune | sans objet | LAZY des deux côtés (défaut) |
| Reservation – Contrat (obligatoire) | OneToOne | `Reservation` (`contrat_id`, unique) | `Contrat.reservation` → `"contrat"` | aucune | non | EAGER (défaut) |
| Contrat – Paiement (obligatoire) | OneToMany / ManyToOne, composition | `Paiement` (`contrat_id`) | `Contrat.paiements` → `"contrat"` | ALL | **oui** | `paiements` LAZY, `contrat` EAGER |
| Agence – Vehicule (complémentaire) | OneToMany / ManyToOne | `Vehicule` (`agence_id`) | `Agence.vehicules` → `"agence"` | ALL | non | `vehicules` LAZY, `agence` EAGER |
| Client, Vehicule – Reservation (complémentaire) | ManyToOne | `Reservation` (`client_id`, `vehicule_id`) | `Client.reservations`, `Vehicule.reservations` | aucune | non | EAGER côté `Reservation` |
| Vehicule – Maintenance (complémentaire) | OneToMany / ManyToOne | `Maintenance` (`vehicule_id`) | `Vehicule.maintenances` → `"vehicule"` | ALL | non | `maintenances` LAZY |

Cohérence des deux côtés : les entités parentes portent les méthodes de synchronisation (`Agence.addEmploye/removeEmploye/addVehicule/removeVehicule`, `Vehicule.addEquipement/removeEquipement/addMaintenance/addReservation`, `Client.addReservation`, `Reservation.etablirContrat/retirerContrat`, `Contrat.addPaiement/removePaiement`). Les services ne modifient jamais directement une collection (ils ne font que la lire).

Composition `Contrat` – `Paiement` : `cascade = ALL` et `orphanRemoval = true` vérifiés. `IPaiementService` reste en lecture seule. `ContratServiceImpl.create` passe aussi par `addPaiement` : un paiement ne peut donc jamais être enregistré sans contrat (aucun paiement orphelin).

JSON sans boucle infinie : `@JsonIgnoreProperties` sur chaque côté (par exemple `Agence.employes` ignore `agence`, `Employe.agence` ignore `employes` et `vehicules`), `@JsonIgnore` sur les collections inverses non utiles.

## 2. Services et endpoints

| Service | Opération | Endpoint |
|---|---|---|
| IAgenceService | `affecterEmploye` / `desaffecterEmploye` | `POST` / `DELETE /api/agences/{agenceId}/employes/{employeId}` |
| IAgenceService | `affecterVehicule` / `desaffecterVehicule` | `POST` / `DELETE /api/agences/{agenceId}/vehicules/{vehiculeId}` |
| IAgenceService | `transfererVehicule` | `POST /api/agences/{agenceCibleId}/vehicules/{vehiculeId}/transfert` |
| IVehiculeService | `affecterEquipement` / `desaffecterEquipement` | `POST` / `DELETE /api/vehicules/{id}/equipements/{equipementId}` |
| IContratService | `creerContrat` | `POST /api/reservations/{id}/contrat` |
| IContratService | `ajouterPaiement` / `retirerPaiement` | `POST /api/contrats/{id}/paiements`, `DELETE /api/contrats/{id}/paiements/{paiementId}` |
| IReservationService | `creerReservation` | `POST /api/reservations/client/{clientId}/vehicule/{vehiculeId}` |
| IMaintenanceService | `planifierMaintenance` | `POST /api/maintenances/vehicule/{vehiculeId}` |

Règles : un employé déjà affecté à une agence (la même ou une autre) ne peut pas être affecté à nouveau, il faut d'abord le désaffecter (409). Désaffecter un lien inexistant, affecter deux fois un équipement ou créer un second contrat pour une réservation est refusé (409). Un identifiant inexistant donne 404. Désaffecter ne supprime jamais l'employé, l'équipement ou le véhicule ; retirer un paiement le supprime (`orphanRemoval`) ; supprimer un contrat supprime ses paiements (cascade).

Transactions : les services sont annotés `@Transactional` au niveau de la classe (écritures et affectations dans une seule transaction, annulée sur `RuntimeException`) et `@Transactional(readOnly = true)` sur les lectures (`findById`, `findAll`).

## 3. Résultats des dix scénarios

Les scénarios ont été rejoués sur les mêmes endpoints (appels HTTP en ligne de commande, base MySQL réelle `OmarTheBoss`) et les codes ci-dessous ont été relevés. **Les captures Swagger UI sont à ajouter dans `docs/swagger/`** (`http://localhost:8081/api/swagger-ui/index.html`).

| N° | Scénario | Code HTTP obtenu | Résultat observé | Capture |
|---|---|---|---|---|
| 1 | Affecter un employé à une agence, puis lire l'agence | 200, 200 | `GET /agences/{id}` liste l'employé et `GET /employes/{id}` renvoie l'agence : lien visible des deux côtés | à ajouter |
| 2 | Affecter deux fois le même employé | 409 | « L'employé 3 est déjà affecté à l'agence 5 » ; une seule ligne en base | à ajouter |
| 3 | Désaffecter puis lire l'employé | 200, 200 (puis 409 si on le refait) | L'employé existe toujours, `agence: null` | à ajouter |
| 4 | Affecter puis retirer un équipement | 200, 200 (409 si on le refait) | Ligne ajoutée puis supprimée dans `vehicule_equipement` ; équipement conservé | à ajouter |
| 5 | Créer un contrat avec 3 paiements en un appel | 201 | Un `INSERT contrat`, trois `INSERT paiement`, un `UPDATE reservation` | à ajouter |
| 6 | Retirer un paiement | 204 (404 si on le refait) | `DELETE paiement`, 2 paiements restants | à ajouter |
| 7 | Second contrat pour la même réservation | 409 | « La réservation 3 a déjà un contrat » ; aucun nouveau contrat | à ajouter |
| 8 | Supprimer un contrat | 200 | Trois `DELETE paiement` puis `DELETE contrat` ; la réservation existe toujours | à ajouter |
| 9 | Contrat à 3 paiements dont le 2e est invalide (montant négatif) | 400 | Rollback complet : 0 contrat, 0 paiement, `contrat_id` de la réservation resté `NULL` | à ajouter |
| 10 | Identifiants inexistants | 404 | Messages « Agence/Employe/Vehicule/Reservation/Contrat/Paiement introuvable avec l'identifiant 9999 » | à ajouter |

Contrôle final : aucun paiement orphelin en base (`SELECT COUNT(*) FROM paiement WHERE contrat_id IS NULL` = 0).

## 4. Requêtes SQL observées

**Scénario 4 – équipement affecté puis retiré**
```
select ... from vehicule ... where id_vehicule=?                 -- charge le véhicule
select ... from equipement where id_equipement=?                 -- charge l'équipement
select ... from vehicule_equipement ... where vehicule_id=?      -- charge la collection (LAZY)
insert into vehicule_equipement (vehicule_id,equipement_id) values (?,?)
...
delete from vehicule_equipement where vehicule_id=?              -- retrait du dernier équipement
```
Interprétation : une seule ligne est ajoutée dans la table de jointure (côté propriétaire `Vehicule`), au commit, sans `save` (dirty checking). Au retrait, la table de jointure perd la ligne et la table `equipement` n'est pas touchée : l'équipement est conservé. Comme la collection devenait vide, Hibernate supprime toutes les lignes du véhicule ; avec deux équipements, retirer l'un exécute `delete from vehicule_equipement where vehicule_id=? and equipement_id=?` (vérifié).

**Scénario 5 – contrat avec trois paiements**
```
insert into contrat (date_signature,montant_total,valide) values (?,?,?)
insert into paiement (contrat_id,date_paiement,mode_paiement,montant) values (?,?,?,?)   (x3)
update reservation set client_id=?,contrat_id=?,date_debut=?,date_fin=?,statut=?,vehicule_id=? where id_reservation=?
```
Interprétation : le contrat est inséré d'abord (son identifiant est nécessaire à la clé étrangère), puis chaque paiement (cascade `ALL`), puis la réservation est mise à jour : elle est propriétaire de `contrat_id`.

**Scénario 6 – retrait d'un paiement**
```
select ... from paiement where contrat_id=?                      -- charge la collection du contrat
delete from paiement where id_paiement=?
```
Interprétation : `removePaiement` retire le paiement de la collection et met `contrat` à `null` ; grâce à `orphanRemoval = true`, Hibernate supprime la ligne au commit. Sans `orphanRemoval`, le paiement resterait en base avec `contrat_id` à `NULL`.

**Scénario 9 – rollback**
```
insert into contrat (date_signature,montant_total,valide) values (?,?,?)
insert into paiement (contrat_id,date_paiement,mode_paiement,montant) values (?,?,?,?)   (1er paiement)
-- le 2e paiement (montant négatif) lève IllegalArgumentException : ROLLBACK
```
Interprétation : le contrat et le premier paiement ont bien été envoyés à la base, mais l'exception non contrôlée annule la transaction : après l'appel il y a 0 contrat et 0 paiement en plus, et la réservation n'a pas de contrat. C'est l'atomicité garantie par `@Transactional`.

## 5. Questions

1. **Pourquoi synchroniser les deux côtés d'une relation bidirectionnelle ?** La base ne connaît que le côté propriétaire (la clé étrangère), mais l'objet en mémoire a deux références. Si on n'en met à jour qu'une, le graphe d'objets est incohérent : `agence.getEmployes()` ne contient pas l'employé alors que `employe.getAgence()` pointe vers l'agence. Les méthodes `add…` / `remove…` mettent les deux côtés à jour en un seul appel.
2. **Différence entre désaffecter et supprimer ?** Désaffecter ne coupe que le lien (clé étrangère mise à `NULL` ou ligne de jointure supprimée) : l'entité continue d'exister. Supprimer efface la ligne de l'entité. La seule exception est la composition : retirer un paiement du contrat le supprime (`orphanRemoval`) et supprimer un contrat supprime ses paiements (cascade).
3. **Que garantit `@Transactional` ?** Toute la méthode du service s'exécute dans une seule transaction : tout est enregistré ou rien (scénario 9), les entités chargées sont suivies et les modifications envoyées au commit (dirty checking), et les collections LAZY peuvent être lues dans la méthode. Le rollback est automatique sur `RuntimeException`.
4. **Quel côté possède le ManyToMany et quelle table en résulte ?** `Vehicule` est propriétaire (`@ManyToMany` + `@JoinTable`) ; `Equipement` est le côté inverse (`mappedBy = "equipements"`). Il en résulte la table de jointure `vehicule_equipement (vehicule_id, equipement_id)` avec une clé primaire composite. Seul le côté propriétaire écrit dans cette table.

## 6. Anomalies SonarQube for IDE

Relevées par relecture du code ajouté selon les règles SonarQube for IDE : **à confirmer avec le plugin dans IntelliJ** (non exécuté ici).

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| `Optional.get()` après un test `isEmpty()` dans `ContratServiceImpl.retirerPaiement` | S3655 : accès à un `Optional` sans garantie qu'il contient une valeur ; code plus long que nécessaire. | Remplacé par `orElseThrow(() -> paiementAbsent(...))` et une méthode privée qui choisit l'exception (404 ou 409). |
| Écriture directe dans le champ `contrat` des deux entités dans `ReservationServiceImpl` / `ContratServiceImpl` (`getContrat().setReservation(null)`) | Logique de synchronisation dupliquée dans les services, contraire à la consigne « aucune écriture directe ». | Remplacée par `Reservation.retirerContrat()` et `Reservation.etablirContrat(...)`. |
| Paiements transmis dans le corps d'un `POST /contrats` enregistrés sans contrat | Risque de paiements orphelins (`contrat_id` à `NULL`). | `ContratServiceImpl.create` recrée le contrat et rattache chaque paiement avec `addPaiement`. |
| Aucun import inutilisé dans le code ajouté | Vérifié par script. | Rien à corriger. |
