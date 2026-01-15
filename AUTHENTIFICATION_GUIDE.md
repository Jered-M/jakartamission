# 🚀 Guide d'Authentification - Jakarta Mission

## ✅ État du projet

### Composants implémentés :

1. **WelcomeBean** (`src/main/java/.../beans/WelcomeBean.java`)

   - ✅ Propriétés : `email` et `password`
   - ✅ Accesseurs/Mutateurs : `getEmail()`, `setEmail()`, `getPassword()`, `setPassword()`
   - ✅ Méthode : `sAuthentifier()` - Orchestre l'authentification

2. **UtilisateurEntrepriseBean** (`src/main/java/.../business/UtilisateurEntrepriseBean.java`)

   - ✅ Méthode : `authentifier(String email, String password)` - Valide les identifiants
   - ✅ Méthode existante : `verifierMotDePasse()` - Vérifie le hash bcrypt
   - ✅ Méthode existante : `obtenirUtilisateurParEmail()` - Trouve l'utilisateur

3. **SessionManager** (`src/main/java/.../business/SessionManager.java`)

   - ✅ Méthode : `createSession()` - Crée une session utilisateur
   - ✅ Méthode : `getValueFromSession()` - Récupère une valeur de session
   - ✅ Méthode : `invalidateSession()` - Termine la session

4. **Vue JSF** (`src/main/webapp/index.xhtml`)
   - ✅ Formulaire avec email et mot de passe
   - ✅ Validation côté client
   - ✅ Messages d'erreur/succès

---

## 🔧 Comment lancer le serveur

### Option 1 : Via NetBeans

1. Ouvrir le projet dans **NetBeans**
2. Clic droit sur le projet → **Run** (F6)
3. GlassFish démarre automatiquement
4. La base de données Derby se crée automatiquement

### Option 2 : Via ligne de commande

```powershell
cd C:\Users\HP\Documents\NetBeansProjects\jakartamission
# Compiler
mvn clean compile

# Lancer GlassFish
asadmin start-domain

# Déployer l'application
asadmin deploy target/jakartamission-1.0.war
```

---

## 🧪 Tester l'authentification

### Accès à l'application :

- **URL** : `http://localhost:8080/jakartamission/`
- Page d'accueil : `index.xhtml`

### Utilisateur par défaut (créé par init_db.sql) :

- **Email** : `admin@example.com`
- **Mot de passe** : `admin`

### Scénarios de test :

| Scénario               | Email             | Mot de passe | Résultat attendu                            |
| ---------------------- | ----------------- | ------------ | ------------------------------------------- |
| Connexion valide       | admin@example.com | admin        | ✅ Redirection vers home.xhtml              |
| Email vide             | [vide]            | admin        | ❌ Erreur : Email obligatoire               |
| Mot de passe vide      | admin@example.com | [vide]       | ❌ Erreur : Mot de passe obligatoire        |
| Email incorrect        | invalid@email.com | admin        | ❌ Erreur : Email ou mot de passe incorrect |
| Mot de passe incorrect | admin@example.com | wrong        | ❌ Erreur : Email ou mot de passe incorrect |

### Vérifier dans la console serveur :

```
========== DEBUG AUTHENTIFICATION ==========
[DEBUG] sAuthentifier() appelée
[DEBUG] Email reçu: 'admin@example.com'
[DEBUG] Password reçu: ***
[BEAN] authentifier() - Recherche utilisateur avec email: admin@example.com
[BEAN] Utilisateur trouvé: admin
[BEAN] Vérification mot de passe: CORRECT
[SUCCESS] Authentification réussie pour: admin
========== FIN DEBUG ==========
```

---

## ⚠️ LIMITES DE LA SOLUTION PROPOSÉE

### 1. **Absence de chiffrement du transport (HTTPS)**

- Les identifiants sont envoyés en clair par HTTP
- **Risque** : Interception des données par un attaquant
- **Solution** : Utiliser HTTPS en production

### 2. **Stockage des sessions en mémoire**

- Les sessions ne persistent pas après redémarrage du serveur
- **Risque** : Perte de session en cas de plantage
- **Solution** : Persister les sessions en base de données

### 3. **Pas de gestion des tentatives échouées**

- Pas de limite sur le nombre de tentatives de connexion
- **Risque** : Vulnérabilité aux attaques par force brute
- **Solution** : Implémenter un système de rate limiting (ex: 5 tentatives puis blocage 15 min)

### 4. **Pas d'expiration de session**

- Les sessions restent actives indéfiniment
- **Risque** : Vol de session si l'utilisateur oublie de se déconnecter
- **Solution** : Définir une durée de vie maximum pour chaque session

### 5. **Pas de double authentification (2FA)**

- Seuls email et mot de passe sont utilisés
- **Risque** : Compromission du compte si le mot de passe est volé
- **Solution** : Ajouter un code OTP (One-Time Password) par SMS ou email

### 6. **Pas de protection CSRF (Cross-Site Request Forgery)**

- Les formulaires ne sont pas protégés contre les attaques CSRF
- **Risque** : Un tiers peut forcer l'utilisateur à effectuer une action involontaire
- **Solution** : Ajouter un token CSRF dans les formulaires

### 7. **Pas de journalisation (Logging)**

- Aucune trace des tentatives de connexion
- **Risque** : Impossible de détecter une intrusion
- **Solution** : Logger toutes les tentatives (réussies et échouées)

### 8. **Mots de passe faibles**

- Pas de validation de la complexité du mot de passe
- **Risque** : Utilisateurs créent des mots de passe faciles à deviner
- **Solution** : Valider la complexité (min 8 caractères, majuscules, chiffres, symboles)

### 9. **Pas de gestion des rôles (RBAC)**

- Tous les utilisateurs ont les mêmes droits
- **Risque** : Pas de contrôle granulaire d'accès
- **Solution** : Implémenter un système de rôles et permissions

### 10. **Email en clair en session**

- L'email est stocké en variables de session sans chiffrement
- **Risque** : Lecture de données sensibles en mémoire
- **Solution** : Chiffrer les données sensibles en session

---

## 📋 Améliorations futures

```
[ ] Ajouter HTTPS obligatoire
[ ] Implémenter une limite de tentatives (rate limiting)
[ ] Ajouter une expiration de session
[ ] Implémenter 2FA (authenticator app ou SMS)
[ ] Ajouter protection CSRF avec tokens
[ ] Implémenter un système de logging complet
[ ] Valider la complexité des mots de passe
[ ] Ajouter un système de rôles/permissions (RBAC)
[ ] Chiffrer les données sensibles
[ ] Ajouter "Mot de passe oublié" avec réinitialisation par email
[ ] Ajouter "Se souvenir de moi" (avec tokens)
[ ] Implémenter une détection d'anomalies (IP, appareil, localisation)
```

---

## 🔐 Architecture de sécurité recommandée

```
Client                          Serveur
  |                                |
  |---- (HTTPS) email/password --->|
  |                                | WelcomeBean
  |                                |   ↓
  |                                | UtilisateurEntrepriseBean
  |                                |   ✓ Vérification email
  |                                |   ✓ BCrypt hash check
  |                                |   ✓ Rate limiting
  |                                |   ✓ Logging
  |                                |   ↓
  |                                | SessionManager
  |                                |   ✓ JWT Token (au lieu de session simple)
  |                                |   ✓ Expiration 30 min
  |<--- (HTTPS) JWT Token ---------|
  |                                |
```
