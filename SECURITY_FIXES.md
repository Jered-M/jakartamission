# Guide de Sécurité - Corrections des Vulnérabilités

## 🔐 Résumé des Corrections Appliquées

Ce document détaille les corrections de sécurité appliquées au pull request d'authentification.

---

## 1. Secret en Dur - Mot de Passe de Base de Données

### ❌ Problème Identifié
- **Fichier**: `src/main/webapp/WEB-INF/glassfish-resources.xml`
- **Ligne**: 7-8
- **Severité**: 🔴 **CRITIQUE**

```xml
<!-- AVANT (DANGEREUX) -->
<property name="user" value="APP"/>
<property name="password" value="APP"/>
```

### ✅ Solution Implémentée
Le mot de passe est maintenant externalisé via des variables d'environnement:

```xml
<!-- APRÈS (SÉCURISÉ) -->
<property name="user" value="${db.user}"/>
<property name="password" value="${db.password}"/>
```

**Configuration requise dans l'environnement Glassfish:**
```bash
# Dans domain.xml ou en variables système
db.user=APP
db.password=VOTRE_PASSWORD_SECURISE
glassfish.database.path=/chemin/vers/database
```

**Ou définir en variables d'environnement:**
```bash
export db.user=APP
export db.password=PASSWORD_COMPLEXE_MIN_12_CHARS
export glassfish.database.path=/path/to/db
```

---

## 2. Logs Sensibles - Exposition de Données d'Utilisateur

### ❌ Problème Identifié
- **Fichier**: `src/main/java/jakartamission/udbl/jakartamission/business/UtilisateurEntrepriseBean.java`
- **Lignes**: 245-256
- **Severité**: 🟠 **MAJEURE**

```java
// AVANT (DANGEREUX)
System.out.println("[BEAN] authentifier() - Recherche utilisateur avec email: " + email);
System.out.println("[BEAN] Utilisateur trouvé: " + (user != null ? user.getUsername() : "null"));
```

### ✅ Solution Implémentée
Suppression des logs exposant les données sensibles:

```java
// APRÈS (SÉCURISÉ)
System.out.println("[BEAN] authentifier() - Tentative d'authentification");
// Messages génériques sans exposition de données
```

**Même correction appliquée à:**
- `src/main/java/jakartamission/udbl/jakartamission/beans/WelcomeBean.java` (lignes 76-156)

**Bonnes pratiques implémentées:**
- ✅ Pas d'exposition d'emails ou usernames en logs
- ✅ Pas d'exposition de détails d'exceptions au client
- ✅ Messages d'erreur génériques pour l'authentification
- ✅ Logs serveur minimalisés en production

---

## 3. Gestion d'Erreurs - Exposition d'Exceptions

### ❌ Problème Identifié
- **Fichier**: `WelcomeBean.java`
- **Ligne**: 145-150
- **Severité**: 🟠 **MAJEURE**

```java
// AVANT (DANGEREUX)
catch (Exception e) {
    System.out.println("[ERROR] Exception: " + e.getMessage());
    FacesContext.getCurrentInstance().addMessage(null,
        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
            "Une erreur est survenue: " + e.getMessage())); // Expose exception!
}
```

### ✅ Solution Implémentée

```java
// APRÈS (SÉCURISÉ)
catch (Exception e) {
    System.err.println("[ERROR] Erreur lors de l'authentification");
    FacesContext.getCurrentInstance().addMessage(null,
        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
            "Une erreur est survenue lors de l'authentification. Veuillez réessayer."));
}
```

---

## 4. Validation des Données et Protection XSS

### ✅ Nouvelle Classe Utilitaire de Sécurité

Créée: `src/main/java/jakartamission/udbl/jakartamission/util/SecurityUtils.java`

**Fonctionnalités:**
```java
// Échappe les caractères spéciaux pour JSON (prévient JSON injection)
SecurityUtils.escapeJson(userInput);

// Échappe HTML/XML (prévient XSS)
SecurityUtils.escapeHtml(userInput);

// Valide les emails
SecurityUtils.isValidEmail(email);

// Valide la force du mot de passe (8+ chars, majuscules, minuscules, chiffres)
SecurityUtils.isValidPassword(password);

// Nettoie les entrées (supprime caractères de contrôle)
SecurityUtils.sanitizeInput(userInput);
```

**Utilisation recommandée:**
```java
// Dans les servlets
String description = SecurityUtils.sanitizeInput(input);
String escapedJson = SecurityUtils.escapeJson(data);

// Dans les JSF pages (via bean)
String safeOutput = SecurityUtils.escapeHtml(userData);
```

---

## 5. Endpoint API Sécurisé pour Mises à Jour de Profil

### ✅ Nouvelle Servlet de Mise à Jour

Créée: `src/main/java/jakartamission/udbl/jakartamission/servlet/ProfileUpdateServlet.java`

**URL**: `/api/profile/update` (POST)

**En-têtes de sécurité appliqués:**
```java
response.setHeader("X-Content-Type-Options", "nosniff");
response.setHeader("X-Frame-Options", "DENY");
response.setHeader("X-XSS-Protection", "1; mode=block");
```

**Validations implémentées:**

1. **Authentification**: Vérifie que l'utilisateur a une session active
2. **Autorisation**: Limite les mises à jour au profil de l'utilisateur authentifié
3. **Validation de description**:
   - Non vide
   - ≤ 500 caractères
   - Nettoyée des caractères de contrôle
4. **Validation de mot de passe**:
   - Tous les champs requis
   - Les nouveaux mots de passe correspondent
   - Force minimale: 8 caractères, majuscules, minuscules, chiffres
   - Différent du mot de passe actuel
   - Vérification du mot de passe actuel avant changement

**Exemple de requête:**
```javascript
// Mise à jour de description
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        type: 'description',
        description: 'Ma nouvelle description'
    })
});

// Changement de mot de passe
fetch('/api/profile/update', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
        type: 'password',
        currentPassword: 'currentPass123',
        newPassword: 'NewPassword123',
        confirmPassword: 'NewPassword123'
    })
});
```

---

## 6. Nouvelles Méthodes Bean pour Sécurité

### ✅ Ajout à UtilisateurEntrepriseBean

```java
/**
 * Mettre à jour le mot de passe (avec hachage sécurisé)
 */
public boolean changerMotDePasse(Long userId, String newPassword)

/**
 * Mettre à jour un utilisateur
 */
public User mettreAJourUtilisateur(User user)
```

---

## 7. Configuration d'Environnement

### ✅ Nouveau Fichier

Créé: `application.properties`

**À configurer selon l'environnement:**
```properties
# Production
db.user=app_user
db.password=PASSWORD_COMPLEXE_MINIMUM_16_CARACTERES
debug.enabled=false
logging.level=WARN

# Développement
debug.enabled=true
logging.level=DEBUG
```

---

## ⚠️ Recommandations Supplémentaires

### 1. Protection CSRF
```xml
<!-- Dans faces-config.xml -->
<context-param>
    <param-name>javax.faces.ENABLE_WEBSOCKET_ENDPOINT</param-name>
    <param-value>false</param-value>
</context-param>
```

### 2. Authentification Multi-Facteur
Ajouter une 2FA ou TOTP pour renforcer la sécurité

### 3. Rate Limiting
Implémenter un rate limiting sur `/api/profile/update` et les endpoints d'authentification

### 4. Audit Logging
Enregistrer les modifications de profil/mot de passe:
```java
System.out.println("[AUDIT] User password changed - Username: " + username + 
                   " - Timestamp: " + new Date());
```

### 5. HTTPS Obligatoire
```xml
<!-- web.xml -->
<security-constraint>
    <web-resource-collection>
        <url-pattern>/*</url-pattern>
    </web-resource-collection>
    <user-data-constraint>
        <transport-guarantee>CONFIDENTIAL</transport-guarantee>
    </user-data-constraint>
</security-constraint>
```

---

## 📋 Checklist de Vérification

- ✅ Secrets externalisés en variables d'environnement
- ✅ Logs sensibles supprimés
- ✅ Gestion d'erreurs sécurisée
- ✅ Classe utilitaire SecurityUtils implémentée
- ✅ Servlet de mise à jour profile sécurisée
- ✅ En-têtes de sécurité HTTP ajoutés
- ✅ Validation des entrées et escapage XSS
- ✅ Authentification vérifiée sur tous les endpoints
- ⏳ À faire: Configuration des variables d'environnement Glassfish
- ⏳ À faire: Tests de sécurité en environnement de staging
- ⏳ À faire: Mise en place du HTTPS en production

---

## 🔒 Impact sur la Sécurité

| Vulnérabilité | Avant | Après | Impact |
|---|---|---|---|
| Secret en dur | 🔴 CRITIQUE | ✅ RÉSOLU | Les credentials sont sécurisées |
| Information Disclosure | 🟠 MAJEURE | ✅ RÉSOLU | Pas d'exposition en logs |
| Exception Disclosure | 🟠 MAJEURE | ✅ RÉSOLU | Messages génériques au client |
| XSS Vulnerability | 🟡 MINEURE | ✅ MITIGÉE | Utilitaires disponibles pour escapage |
| Weak Password | 🟡 MINEURE | ✅ RÉSOLU | Validation minimale 8+ chars |

---

## 📞 Contact & Support

Pour des questions de sécurité:
- Créer une issue avec le label `security`
- Respecter la politique de disclosure responsable
- Ne pas publier les vulnérabilités en public avant correction

**Dernière mise à jour:** 16 janvier 2026
**Version du document:** 1.0
