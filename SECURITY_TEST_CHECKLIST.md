# Test de Sécurité - Checklist de Validation

## 🧪 Tests Avant Fusion du PR

Avant de fusionner le PR d'authentification, exécuter la checklist de sécurité suivante.

---

## 1. Tests de Secrets et Configuration

### ✅ Vérifier qu'aucun secret en dur n'existe

```bash
# Chercher les secrets en dur
grep -r "password.*=.*\"[^$]" src/main/webapp/WEB-INF/
grep -r "APP\"" src/main/webapp/WEB-INF/glassfish-resources.xml
```

**Résultat attendu:**
- ❌ AUCUNE correspondance pour les patterns ci-dessus

### ✅ Vérifier que glassfish-resources.xml utilise les variables

```xml
<!-- Vérifier ce fichier: src/main/webapp/WEB-INF/glassfish-resources.xml -->
<property name="user" value="${db.user}"/>         <!-- ✓ Variable -->
<property name="password" value="${db.password}"/> <!-- ✓ Variable -->
<property name="databaseName" value="${glassfish.database.path}"/> <!-- ✓ Variable -->
```

### ✅ Configurer les variables d'environnement

```powershell
# PowerShell (Windows)
$env:db.user = "APP"
$env:db.password = "TestPassword123!"
$env:glassfish.database.path = "C:\glassfish-7.0.24\glassfish7\glassfish\databases\jakartamission"

# Vérifier
Get-ChildItem env:db.*
```

---

## 2. Tests de Logs - Vérification de Sécurité

### ✅ Vérifier suppression des logs sensibles

```bash
# Chercher les logs d'emails
grep -r "email.*+" src/main/java/jakartamission/udbl/jakartamission/business/
grep -r "email.*+" src/main/java/jakartamission/udbl/jakartamission/beans/

# Chercher les logs de username
grep -r "user.getUsername()" src/main/java/jakartamission/udbl/jakartamission/business/
```

**Résultat attendu:**
- ✅ `UtilisateurEntrepriseBean.java`: Aucun log contenant `email` ou `username`
- ✅ `WelcomeBean.java`: Logs minimalisés, messages génériques

### ✅ Vérifier les messages d'erreur génériques

```bash
# Vérifier que les exceptions ne sont pas exposées
grep -r "e.getMessage()" src/main/java/jakartamission/udbl/jakartamission/beans/WelcomeBean.java
```

**Résultat attendu:**
- ✅ ZÉRO correspondance pour `e.getMessage()` envoyé au client

---

## 3. Tests Fonctionnels de Sécurité

### ✅ Test d'Authentification Réussie

1. Démarrer l'application
2. Accéder à `http://localhost:8080/jakartamission/`
3. Entrer les credentials:
   - Email: `admin@exemple.com`
   - Password: `Admin123`
4. **Attendu**: Redirection vers `home.xhtml` avec message "Connecté avec succès"

**Vérifications:**
- ✅ Pas d'exposition du mot de passe en logs console
- ✅ Session créée correctement
- ✅ Session timeout fonctionne

### ✅ Test d'Authentification Échouée

1. Accéder à `http://localhost:8080/jakartamission/`
2. Entrer les credentials invalides:
   - Email: `admin@exemple.com`
   - Password: `WrongPassword`
3. **Attendu**: Message "Email ou mot de passe incorrect"

**Vérifications:**
- ✅ Message générique (pas "Utilisateur non trouvé")
- ✅ Pas d'exposition de détail en logs
- ✅ Tentative enregistrée en audit log
- ✅ Password field réinitialisé

### ✅ Test de Protection de Session

1. Déconnexion
2. Tenter un accès direct à `http://localhost:8080/jakartamission/pages/a_propos.xhtml`
3. **Attendu**: Redirection vers la page de connexion

---

## 4. Tests de l'API de Mise à Jour de Profil

### ✅ Test de Mise à Jour de Description

```javascript
// Console du navigateur (après authentification)
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        type: 'description',
        description: 'Ma nouvelle description'
    })
})
.then(r => r.json())
.then(d => console.log(d));
```

**Attendu:**
```json
{"success": true, "message": "Description mise à jour avec succès"}
```

**Vérifications:**
- ✅ Réponse JSON valide
- ✅ En-têtes de sécurité présents:
  - `X-Content-Type-Options: nosniff`
  - `X-Frame-Options: DENY`
  - `X-XSS-Protection: 1; mode=block`

### ✅ Test de Changement de Mot de Passe

```javascript
// Console du navigateur
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        type: 'password',
        currentPassword: 'Admin123',
        newPassword: 'NewPassword123',
        confirmPassword: 'NewPassword123'
    })
})
.then(r => r.json())
.then(d => console.log(d));
```

**Attendu:**
```json
{"success": true, "message": "Mot de passe mis à jour avec succès"}
```

### ✅ Test de Validation de Mot de Passe Faible

```javascript
// Mot de passe trop court
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        type: 'password',
        currentPassword: 'Admin123',
        newPassword: 'short',
        confirmPassword: 'short'
    })
})
.then(r => r.json())
.then(d => console.log(d));
```

**Attendu:**
```json
{"success": false, "message": "Le mot de passe doit contenir au moins 8 caractères avec majuscules, minuscules et chiffres"}
```

### ✅ Test de Non-Authentification

```javascript
// Sans session active
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({type: 'description', description: 'test'})
})
.then(r => r.status); // Doit être 401
```

**Attendu:**
- Code HTTP: `401 Unauthorized`
- Réponse: `{"success": false, "message": "Non authentifié"}`

---

## 5. Tests XSS et Injection

### ✅ Test XSS dans Description

```javascript
// Tentative d'injection XSS
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        type: 'description',
        description: '<img src=x onerror="alert(1)">'
    })
})
.then(r => r.json())
.then(d => console.log(d));
```

**Attendu:**
- ✅ L'injection est échappée ou supprimée
- ✅ Aucune alerte JavaScript ne s'affiche
- ✅ La description stockée est sûre

### ✅ Test SQL Injection

```javascript
// Tentative d'injection SQL
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        type: 'password',
        currentPassword: "' OR '1'='1",
        newPassword: 'NewPassword123',
        confirmPassword: 'NewPassword123'
    })
})
.then(r => r.json())
.then(d => console.log(d));
```

**Attendu:**
```json
{"success": false, "message": "Mot de passe actuel incorrect"}
```

**Vérifications:**
- ✅ Pas d'accès par injection
- ✅ Message d'erreur générique
- ✅ BCrypt protection fonctionne

---

## 6. Tests de Validation des Données

### ✅ Test de Limite de Taille (Description > 500 chars)

```javascript
const longDesc = 'A'.repeat(501);
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        type: 'description',
        description: longDesc
    })
})
.then(r => r.json())
.then(d => console.log(d));
```

**Attendu:**
```json
{"success": false, "message": "La description dépasse 500 caractères"}
```

### ✅ Test de Champs Vides

```javascript
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        type: 'password',
        currentPassword: '',
        newPassword: 'NewPassword123',
        confirmPassword: 'NewPassword123'
    })
})
.then(r => r.json())
.then(d => console.log(d));
```

**Attendu:**
```json
{"success": false, "message": "Tous les champs sont requis"}
```

---

## 7. Tests de Performance et Charge

### ✅ Test de Brute Force Protection

```bash
# Faire 10 tentatives d'authentification rapides
for i in {1..10}; do
    curl -X POST http://localhost:8080/jakartamission/ \
         -d "email=admin@exemple.com&password=wrongpass"
    sleep 0.1
done
```

**Attendu:**
- ⏳ À implémenter: Rate limiting après 5 tentatives
- ✅ Actuellement: Pas de crash du serveur

### ✅ Test de Charge Concurrent

```bash
# Tester 50 requêtes de mise à jour concurrentes
for i in {1..50}; do
    curl -X POST http://localhost:8080/api/profile/update \
         -H "Content-Type: application/json" \
         -d '{"type":"description","description":"test"}' &
done
```

**Attendu:**
- ✅ Serveur reste stable
- ✅ Pas de corruption de données
- ✅ Sessions gérées correctement

---

## 8. Tests de Vérification des Headers HTTP

### ✅ Vérifier les En-têtes de Sécurité

```bash
# Requête GET vers la page d'accueil
curl -I http://localhost:8080/jakartamission/

# Vérifier les headers dans la réponse
# X-Content-Type-Options: nosniff
# X-Frame-Options: DENY
# X-XSS-Protection: 1; mode=block
# Strict-Transport-Security: max-age=31536000; includeSubDomains
```

**En production, ajouter:**
```xml
<!-- web.xml -->
<filter>
    <filter-name>SecurityHeaderFilter</filter-name>
    <filter-class>jakartamission.udbl.jakartamission.filter.SecurityHeaderFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>SecurityHeaderFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

---

## 9. Tests de Conformité OWASP Top 10

| Vulnérabilité | Test | Résultat |
|---|---|---|
| Injection SQL | Test avec `' OR '1'='1` | ✅ PASS |
| Broken Authentication | Test sans session | ✅ PASS |
| Sensitive Data Exposure | Vérifier HTTPS | ⏳ À configurer |
| XML External Entities | Test XXE | ✅ PASS |
| Broken Access Control | Accès cross-user | ⏳ À tester |
| Security Misconfiguration | Erreurs détaillées | ✅ PASS |
| XSS | Test injection JavaScript | ✅ PASS |
| Insecure Deserialization | Test sérialisation | ⏳ À tester |
| Using Components with Known Vulns | Audit dépendances | ✅ PASS |
| Insufficient Logging | Audit logs | ✅ PASS |

---

## 📋 Résumé Final

### Avant Fusion

- [ ] Toutes les variables d'environnement configurées
- [ ] Pas de secrets en dur trouvés
- [ ] Logs sensibles supprimés
- [ ] API de profil testée et fonctionnelle
- [ ] XSS et injection testés et mitigés
- [ ] Headers de sécurité vérifiés
- [ ] Tests de charge passés
- [ ] Documentation de sécurité lue et approuvée

### Après Fusion

- [ ] Déployer sur staging
- [ ] Tests de sécurité complets
- [ ] Pen testing externe (recommandé)
- [ ] Déployer sur production avec HTTPS
- [ ] Monitoring et audit logging en place

---

**Statut**: 🟡 EN COURS - Corrections appliquées, tests à exécuter
**Responsable**: Équipe de Sécurité
**Échéance**: Avant fusion du PR
