# Atelier 4 – Notes sur la couche Service, l'IoC et l'injection de dépendances

Packages : `com.example.demo.service` (interfaces `I…Service`), `com.example.demo.service.implementation` (classes `…ServiceImpl`), `com.example.demo.exception` (`ResourceNotFoundException`).

Remarque : les constructeurs sont écrits à la main (équivalent exact de ce que génère `@RequiredArgsConstructor`), car l'extension Java de VS Code signale à tort cette annotation (« Flexible Constructor Bodies ») alors que Maven compile sans problème.

## A. Modes d'injection comparés

| Critère | Constructeur | Attribut (`@Autowired` sur le champ) |
|---|---|---|
| Champ `final` possible ? | Oui | Non |
| Dépendance visible dans la signature de la classe ? | Oui | Non (cachée dans un champ privé) |
| Classe utilisable avec `new` hors conteneur sans dépendance `null` ? | Oui : le constructeur impose de fournir le repository | Non : le champ reste `null` tant que Spring n'injecte pas |
| Signalé par SonarQube for IDE ? | Non | Oui (règle S6813 d'après l'énoncé, à vérifier dans le panneau) |

Convention retenue pour les neuf services : injection par constructeur, champ `final`, dépendance typée par une interface. L'expérience d'injection par attribut n'a pas été conservée dans le code.

## B. Services

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| AgenceServiceImpl | IAgenceRepository | Constructeur | Une seule dépendance, obligatoire. Champ `final`, service sans état. |
| EmployeServiceImpl | IEmployeRepository | Constructeur | Idem. |
| VehiculeServiceImpl | IVehiculeRepository | Constructeur | Idem. |
| EquipementServiceImpl | IEquipementRepository | Constructeur | Idem. |
| ClientServiceImpl | IClientRepository | Constructeur | Idem. |
| ReservationServiceImpl | IReservationRepository | Constructeur | Idem. |
| ContratServiceImpl | IContratRepository | Constructeur | Idem. La suppression d'un contrat supprime ses paiements par cascade. |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Idem. Lecture seule : `findById` et `findAll` uniquement. |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | Idem. |

Règles de gestion implémentées (création et modification, `IllegalArgumentException` avec un message clair) :

| Service | Règle |
|---|---|
| Agence | Le nom est obligatoire. |
| Employe | Le nom et le prénom sont obligatoires (le modèle n'a pas d'attribut « poste » : il a `role`, une énumération qui ne peut pas être vide de texte). |
| Vehicule | Le tarif journalier ne peut pas être négatif (le modèle n'a pas de kilométrage). |
| Equipement | Le libellé est obligatoire (le modèle n'a pas de quantité). |
| Client | Le nom et l'e-mail sont obligatoires. |
| Reservation | La date de fin ne peut pas être antérieure à la date de début. |
| Contrat | Le montant total ne peut pas être négatif. |
| Paiement | Lecture seule : aucune création, modification ni suppression. |
| Maintenance | La date de fin ne peut pas être antérieure à la date de début (le modèle n'a pas de coût). |

Autres choix communs : `create` refuse un identifiant non null ; `update` charge l'entité existante puis recopie les attributs modifiables ; `findById` et `deleteById` lèvent `ResourceNotFoundException` si l'identifiant n'existe pas. Pour `Reservation` et `Contrat`, la suppression coupe d'abord le lien d'association (one-to-one) entre les deux, sinon Hibernate refuse le flush.

## C. Messages d'erreur du conteneur

**Message 1 – `ContratController` : bean de type `IContratService` introuvable** (`NoSuchBeanDefinitionException`, enveloppée dans `UnsatisfiedDependencyException`).
- Causes probables : l'annotation `@Service` est absente de `ContratServiceImpl`, ou la classe est placée hors du package de scan (hors du package racine de `DemoApplication`).
- Correction : ajouter `@Service` sur `ContratServiceImpl` et vérifier qu'elle est bien dans un sous-package du package racine.

**Message 2 – `NotificationController` : un seul bean attendu, 2 trouvés** (`NoUniqueBeanDefinitionException` : `emailNotificateur` et `smsNotificateur`).
- Correction 1 : `@Primary` sur la classe du bean qu'on veut par défaut (par exemple `EmailNotificateur`).
- Correction 2 : `@Qualifier("emailNotificateur")` sur le paramètre du constructeur du contrôleur, pour choisir explicitement.

**Message 3 – cycle entre `clientServiceImpl` et `reservationServiceImpl`** (`BeanCurrentlyInCreationException`).
- Avec l'injection par constructeur, chaque bean a besoin de l'autre pour être *construit* : il n'existe aucun ordre de création possible, et on ne peut pas créer l'objet à moitié. Le conteneur doit donc refuser de démarrer (avec l'injection par attribut, le cycle passerait en créant les objets puis en les reliant, ce qui masque le défaut de conception).
- Piste de conception : extraire la responsabilité commune dans un troisième service, ou faire en sorte qu'un seul des deux dépende de l'autre (par exemple `ReservationService` utilise `IClientRepository` directement au lieu de `ClientService`).

## D. Anomalies SonarQube for IDE

Relevées par relecture et d'après l'énoncé : à confirmer avec le plugin dans IntelliJ.

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| Injection par attribut (`@Autowired` sur un champ) | S6813 : champ non `final`, dépendance cachée, `null` hors conteneur. | Injection par constructeur avec champ `final` dans les neuf services. |
| `throw new RuntimeException(...)` dans `findById` | S112 : on ne doit pas lever une exception générique, le code appelant ne peut pas la distinguer. | `ResourceNotFoundException` (exception métier dédiée). |
| `jakarta.persistence.EntityNotFoundException` levée par la couche Service dans la première version | Mélange la couche métier avec une exception du framework de persistance. | Remplacée par `ResourceNotFoundException`, qui ne dépend d'aucun framework. |

## E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?** Il n'est pas dans notre code : c'est le conteneur Spring qui l'exécute au démarrage, après avoir détecté la classe grâce à `@Service` et au scan. Il fournit au constructeur le proxy de `IContratRepository`.
2. **Pourquoi un contrôleur dépend-il de `IContratService` et non de `ContratServiceImpl` ?** Pour dépendre d'une abstraction : on peut changer ou remplacer l'implémentation (ou la simuler dans un test) sans modifier le contrôleur. Le couplage reste faible.
3. **Pourquoi un service singleton doit-il rester sans état ?** Une seule instance est partagée par tous les threads (toutes les requêtes). Un attribut modifiable propre à un appel est donc partagé. Exemple de bug : un attribut `contratCourant` rempli par une requête et écrasé par une autre requête en parallèle : un utilisateur modifie ou voit le contrat d'un autre.
4. **Pourquoi `update` charge-t-il l'entité existante avant `save` ?** Passer directement l'objet reçu à `save` (avec un id) déclencherait un `merge` qui écraserait toutes les colonnes, y compris celles non envoyées (elles passeraient à `null`) et, avec `orphanRemoval`, pourrait vider la collection de paiements. En chargeant puis en recopiant seulement les attributs modifiables, on garde le reste intact et on contrôle ce qui change.
5. **Différences :**
   - `@Component` se pose sur une classe que l'on écrit et que le scan détecte ; `@Bean` se pose sur une méthode d'une classe `@Configuration` et déclare explicitement un bean (utile pour une classe de bibliothèque ou du JDK qu'on ne peut pas annoter).
   - `@Primary` se pose sur la classe : c'est le candidat par défaut quand plusieurs beans ont le même type ; `@Qualifier("nom")` se pose à l'injection et choisit explicitement un bean.
   - `IPaiementService` n'expose ni création, ni modification, ni suppression car `Paiement` est une composition de `Contrat` : l'enfant n'est créé ou retiré qu'à travers son parent (le contrat, par cascade et `orphanRemoval`). Le service de paiement reste en lecture seule.
