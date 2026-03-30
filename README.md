# Nehb - Plateforme Éducative AlNaifer

**LMS & ERP pour écoles coraniques (Madrassas)**

## 📋 Description

Nehb est une plateforme éducative omnicanale conçue spécifiquement pour les établissements d'enseignement confessionnel islamique. Elle combine un Système de Gestion de l'Apprentissage (LMS) et un Progiciel de Gestion Intégré (ERP) pour digitaliser les méthodologies pédagogiques traditionnelles tout en tirant parti des innovations algorithmiques contemporaines.

## 🎯 Objectifs Stratégiques

- **Centralisation de l'Information** : Source unique de vérité pour l'administration, le corps professoral, les élèves et les tuteurs
- **Optimisation Opérationnelle** : Réduction de 80% du temps de création des emplois du temps grâce à un algorithme génétique
- **Continuité Pédagogique** : Apprentissage hybride (présentiel/distanciel) avec intégration native de Google Meet
- **Suivi Granulaire** : Digitalisation automatisée des cycles de mémorisation du Coran (Sabaq, Sabqi, Manzil)

## 👥 Rôles Utilisateurs

| Rôle | Description | Permissions Principales |
|------|-------------|------------------------|
| **Administrateur** | Gestion complète de l'établissement | CRUD complet sur tous les modules |
| **Enseignant (Ustad)** | Corps professoral | Gestion pédagogique, évaluations |
| **Élève (Talib)** | Apprenants | Accès à sa progression, cours |
| **Parent** | Tuteurs légaux | Suivi des enfants, justificatifs |

## 🏗️ Architecture Technique

### Backend
- **Framework** : Spring Boot 3.2.0 (Java 17)
- **Base de données** : PostgreSQL 15+
- **Cache** : Redis
- **Sécurité** : Spring Security + JWT
- **API** : RESTful

### Frontend (à implémenter)
- **Framework** : Angular 17+
- **Mobile** : Ionic/Capacitor (cross-platform)
- **PWA** : Support offline-first

## 🚀 Fonctionnalités Clés

### 1. Gestion du Hifz (Mémorisation du Coran)
- Cycle Sabaq / Sabqi / Manzil avec machine à états finis
- Évaluation tridimensionnelle du Tajwid (Lahn Jali / Lahn Khafi)
- Système de Répétition Espacée (SRS) basé sur la courbe d'Ebbinghaus
- Radar charts des erreurs par règle de Tajwid

### 2. Planification Intelligente
- Algorithme génétique pour la résolution du "Course Timetabling Problem"
- Détection de conflits en temps réel
- Contraintes strictes et souples configurables

### 3. Visioconférence Native
- Intégration Google Meet sans redirection (iframe sandboxée)
- OAuth 2.0 silencieux
- Gestion des co-hôtes automatique

### 4. Dashboard Minimaliste
- Interface en cartes (Card-based UI)
- Widget "Agenda Actif" avec bouton unique "Rejoindre la session"
- Jauges circulaires de progression par Juz
- Divulgation progressive des détails

## 📦 Installation

### Prérequis
- Java 17+
- PostgreSQL 15+
- Redis 7+
- Maven 3.8+
- Node.js 18+ (pour le frontend)

### 1. Configuration de la base de données

```bash
# Créer la base de données
createdb nehb_db

# Créer l'utilisateur
psql -c "CREATE USER nehb_user WITH PASSWORD 'votre_mot_de_passe';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE nehb_db TO nehb_user;"
```

### 2. Configuration des variables d'environnement

```bash
cd backend
cp .env.example .env
# Éditez .env avec vos valeurs réelles
```

### 3. Lancement du backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

L'application sera disponible sur `http://localhost:8080/api/v1`

### 4. Lancement du frontend (à implémenter)

```bash
cd frontend
npm install
ng serve
```

L'application sera disponible sur `http://localhost:4200`

## 🔐 Sécurité

- Chiffrement des mots de passe avec BCrypt
- Tokens JWT avec expiration configurable
- Protection CSRF et XSS
- Headers de sécurité HTTP
- Conformité OWASP Top 10
- RGPD : anonymisation, droit à l'oubli

## 📡 API Endpoints

### Authentification
```
POST   /api/v1/auth/login          - Connexion utilisateur
POST   /api/v1/auth/refresh        - Rafraîchissement de token
POST   /api/v1/auth/logout         - Déconnexion
GET    /api/v1/auth/validate       - Validation de token
```

### Gestion du Hifz
```
POST   /api/v1/hifz/assign-sabaq           - Assigner un nouveau Sabaq
POST   /api/v1/hifz/evaluate               - Évaluer un Sabaq
GET    /api/v1/hifz/student/{studentId}    - Progression d'un élève
GET    /api/v1/hifz/pending-evaluations    - Évaluations en attente
GET    /api/v1/hifz/sabqi-revision         - Révisions Sabqi (15 jours)
GET    /api/v1/hifz/manzil-revision        - Révisions Manzil (SRS)
GET    /api/v1/hifz/stats/by-juz           - Stats par Juz
GET    /api/v1/hifz/can-assign-new-sabaq   - Vérification seuil Sabqi
GET    /api/v1/hifz/{id}/evaluations       - Historique évaluations
GET    /api/v1/hifz/tajwid-radar/{id}      - Radar chart Tajwid
```

## 🧪 Tests

```bash
# Tests unitaires backend
mvn test

# Tests d'intégration
mvn verify

# Couverture de code
mvn jacoco:report
```

## 📄 Licence

Ce projet est propriétaire et confidentiel. Tous droits réservés © AlNaifer 2024.

## 📞 Contact

Pour toute question ou demande d'information :
- Email : contact@alnaifer.com
- Site web : https://alnaifer.com

---

**Projet Nehb** - Version 1.0.0-SNAPSHOT  
Développé avec ❤️ pour l'excellence éducative islamique
