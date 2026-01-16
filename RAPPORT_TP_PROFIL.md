# TP - Gestion de Profil Utilisateur

## Jakarta Mission - Système d'Authentification et Profil

---

## 📋 Sommaire

1. [Objectifs](#objectifs)
2. [Fonctionnalités Implémentées](#fonctionnalités-implémentées)
3. [Guide de Test](#guide-de-test)
4. [Captures d'Écran Requises](#captures-décran-requises)
5. [Architecture Système](#architecture-système)
6. [Code Source](#code-source)

---

## 🎯 Objectifs

Ce TP a pour but de mettre en place un système complet d'authentification et de gestion de profil utilisateur pour l'application Jakarta Mission, incluant :

✅ Authentification sécurisée (login/register)
✅ Gestion de session utilisateur
✅ Page de profil avec affichage des informations
✅ Modification du profil utilisateur
✅ Gestion sécurisée du mot de passe
✅ Déconnexion sécurisée
✅ Persistance des données en base de données

---

## 🚀 Fonctionnalités Implémentées

### 1. **Authentification (LoginServlet)**

- **URL:** `/login`
- **Méthode:** POST
- **Paramètres:** email, password
- **Fonctionnalité:**
  - Authentifie l'utilisateur via BCrypt
  - Crée une session HttpSession
  - Stocke: username, email, userId
  - Redirige vers home.xhtml

### 2. **Enregistrement (RegisterServlet)**

- **URL:** `/register`
- **Méthode:** POST
- **Paramètres:** username, email, password, confirmPassword, description
- **Validations:**
  - Username: 3-50 caractères
  - Email: format valide
  - Password: min 6 chars, 1 majuscule, 1 chiffre
  - Pas de doublons (email/username)

### 3. **Page d'Accueil (home.xhtml)**

- Affichage du profil utilisateur
- Récupération dynamique via `/api/userinfo`
- Fallback localStorage
- Boutons de navigation:
  - ➕ Ajouter un Lieu
  - 🗺️ Visiter
  - ℹ️ À propos
  - 🚪 Déconnexion

### 4. **Page de Profil (pages/a_propos.xhtml)** ⭐

- **En-tête:** Titre + Boutons (Accueil, Déconnexion)
- **3 Onglets:**

#### 📌 Onglet 1: Profil

- **Utilisateur** (read-only) - Affichage uniquement
- **Email** (read-only) - Affichage uniquement
- **Description** (éditable) - Textarea
- Boutons: Enregistrer, Retour

#### 🔐 Onglet 2: Mot de Passe

- **Mot de passe actuel** (input type="password")
- **Nouveau mot de passe** (input type="password", 8+ caractères)
- **Confirmer mot de passe** (input type="password")
- Validations client:
  - Tous les champs requis
  - Min 8 caractères
  - Confirmation correspondance
- Boutons: Changer, Retour

#### ℹ️ Onglet 3: À Propos

- Description de l'application
- Caractéristiques
- Stack technique
- Informations L'Indonésie
- Contact

### 5. **API JSON (UserInfoServlet)**

- **URL:** `/api/userinfo`
- **Méthode:** GET
- **Réponse Success:**

```json
{
  "success": true,
  "username": "jered",
  "email": "jered@jered.com",
  "userId": 1
}
```

- **Réponse Error:**

```json
{
  "success": false,
  "message": "Session non valide"
}
```

### 6. **Déconnexion (LogoutServlet)**

- **URL:** `/logout`
- **Action:** Invalide la session
- **Redirection:** login.html

---

## 🧪 Guide de Test

### ÉTAPE 1: Créer un compte

**URL:** `http://localhost:8080/jakartamission/register.html`

**Données de test:**

```
Nom d'utilisateur: jered
Email: jered@jered.com
Mot de passe: Jered123
Confirmer mot de passe: Jered123
Description: Développeur Java Jakarta EE
```

**Résultats attendus:**

- ✅ Compte créé avec succès
- ✅ Redirection vers login.html avec message de succès
- ✅ Données sauvegardées en base de données

---

### ÉTAPE 2: Se connecter

**URL:** `http://localhost:8080/jakartamission/login.html`

**Données:**

```
Email: jered@jered.com
Mot de passe: Jered123
```

**Résultats attendus:**

- ✅ Connexion réussie
- ✅ Session créée (vérifiable en F12 > Storage > Cookies)
- ✅ Redirection vers home.xhtml
- ✅ Profil utilisateur affiché: "Utilisateur: jered" et "Email: jered@jered.com"

---

### ÉTAPE 3: Vérifier le profil sur la page d'accueil

**URL:** `http://localhost:8080/jakartamission/home.xhtml`

**Captures requises:**

1. Section "Mon Profil" affichant:

   - Utilisateur: jered
   - Email: jered@jered.com
   - Message "Cliquer pour modifier votre profil →"

2. Tous les boutons de navigation visibles:
   - ➕ Ajouter un Lieu
   - 🗺️ Visiter
   - ℹ️ À propos
   - 🚪 Déconnexion

---

### ÉTAPE 4: Accéder à la page de profil

**Action:** Cliquer sur la section "Mon Profil" ou le bouton "ℹ️ À propos"

**URL:** `http://localhost:8080/jakartamission/pages/a_propos.xhtml`

**Captures requises:**

#### Capture 1: En-tête et navigation

- Titre "Profil"
- Boutons "Accueil" et "Déconnexion"
- 3 onglets: Profil, Mot de Passe, À Propos

---

### ÉTAPE 5: Tester l'onglet Profil

**Action:** Vérifier l'onglet 1 (Profil)

**Vérifications:**

- ✅ Utilisateur affiche: "jered" (read-only, fond grisé)
- ✅ Email affiche: "jered@jered.com" (read-only, fond grisé)
- ✅ Description: Textarea éditable
- ✅ Boutons: "Enregistrer" et "Retour"

**Capture 2: Onglet Profil**

- Champs username et email en grisé (read-only)
- Textarea description
- Boutons d'action

**Test de modification:**

```
Description: Je suis un développeur passionné par Jakarta EE
```

- Cliquer "Enregistrer"
- ✅ Message succès: "Profil mis à jour avec succès"
- ✅ Disparaît après 3 secondes

**Capture 3: Message de succès profil**

---

### ÉTAPE 6: Tester l'onglet Mot de Passe

**Action:** Cliquer sur l'onglet 2 (Mot de Passe)

**Champs visibles:**

- Mot de Passe Actuel (password)
- Nouveau Mot de Passe (password)
- Confirmer le Mot de Passe (password)

**Capture 4: Onglet Mot de Passe - Vide**

**Test 1: Validation - Champs vides**

- Cliquer "Changer"
- ✅ Erreur: "Tous les champs sont requis"

**Capture 5: Erreur champs vides**

**Test 2: Validation - Mot de passe trop court**

```
Mot de passe actuel: Jered123
Nouveau mot de passe: Pass12
Confirmer: Pass12
```

- Cliquer "Changer"
- ✅ Erreur: "Le mot de passe doit contenir au moins 8 caractères"

**Capture 6: Erreur mot de passe court**

**Test 3: Validation - Mots de passe non correspondants**

```
Mot de passe actuel: Jered123
Nouveau mot de passe: NewPass123
Confirmer: NewPass456
```

- Cliquer "Changer"
- ✅ Erreur: "Les mots de passe ne correspondent pas"

**Capture 7: Erreur mots de passe non correspondants**

**Test 4: Changement réussi**

```
Mot de passe actuel: Jered123
Nouveau mot de passe: NewPass123
Confirmer: NewPass123
```

- Cliquer "Changer"
- ✅ Succès: "Mot de passe changé avec succès"
- ✅ Formulaire vide après

**Capture 8: Succès changement mot de passe**

---

### ÉTAPE 7: Tester l'onglet À Propos

**Action:** Cliquer sur l'onglet 3 (À Propos)

**Contenu vérifié:**

- ✅ Description Jakarta Mission
- ✅ Caractéristiques listées
- ✅ Technologies affichées
- ✅ Informations Indonésie
- ✅ Contact visible
- ✅ Bouton "Retour à l'Accueil"

**Capture 9: Onglet À Propos - Haut**
**Capture 10: Onglet À Propos - Bas**

---

### ÉTAPE 8: Tester la déconnexion

**Action 1:** Via le bouton dans l'en-tête

- Cliquer "Déconnexion" dans l'en-tête
- ✅ Redirection vers login.html

**Capture 11: Page d'accueil avant logout**

**Résultats:**

- ✅ Session invalidée
- ✅ Redirection login.html
- ✅ localStorage effacé (optionnel)

**Capture 12: Page de login après logout**

---

### ÉTAPE 9: Vérifier la sécurité

**Test: Accès sans session**

- URL: `http://localhost:8080/jakartamission/pages/a_propos.xhtml`
- Sans session active
- ✅ Redirection vers index.xhtml (SessionControlFilter actif)

**Capture 13: Tentative d'accès direct (protection)**

---

### ÉTAPE 10: Persistence des données

**Redéployement:**

1. NetBeans: Ctrl+Shift+F11 (Clean and Build)
2. Redémarrer GlassFish

**Test:**

- Se reconnecter avec jered@jered.com
- ✅ Compte toujours disponible
- ✅ Données persistées en BD Derby

**Capture 14: Reconnexion après redéploiement**

---

## 📸 Captures d'Écran Requises

### Liste Complète pour le PDF

| #   | Page           | Description                        | Fichier                            |
| --- | -------------- | ---------------------------------- | ---------------------------------- |
| 1   | register.html  | Formulaire d'enregistrement rempli | screenshot_01_register.png         |
| 2   | login.html     | Connexion réussie (message)        | screenshot_02_login_success.png    |
| 3   | home.xhtml     | Page d'accueil avec profil         | screenshot_03_home_profile.png     |
| 4   | a_propos.xhtml | Onglet Profil                      | screenshot_04_profile_tab.png      |
| 5   | a_propos.xhtml | Message succès profil              | screenshot_05_profile_success.png  |
| 6   | a_propos.xhtml | Onglet Mot de passe                | screenshot_06_password_tab.png     |
| 7   | a_propos.xhtml | Erreur champs vides                | screenshot_07_error_empty.png      |
| 8   | a_propos.xhtml | Erreur mot de passe court          | screenshot_08_error_short.png      |
| 9   | a_propos.xhtml | Erreur mots non correspondants     | screenshot_09_error_mismatch.png   |
| 10  | a_propos.xhtml | Succès mot de passe                | screenshot_10_password_success.png |
| 11  | a_propos.xhtml | Onglet À Propos (haut)             | screenshot_11_about_top.png        |
| 12  | a_propos.xhtml | Onglet À Propos (bas)              | screenshot_12_about_bottom.png     |
| 13  | home.xhtml     | Page avant logout                  | screenshot_13_before_logout.png    |
| 14  | login.html     | Page après logout                  | screenshot_14_after_logout.png     |
| 15  | pages/         | Protection SessionControlFilter    | screenshot_15_protected_page.png   |

---

## 🏗️ Architecture Système

### Flux d'Authentification

```
┌─────────────┐
│ register.html│ ← User crée account
└──────┬──────┘
       │ POST /register
       ▼
┌──────────────────┐
│ RegisterServlet  │ ← Valide + Hash password
└──────┬───────────┘
       │ INSERT User
       ▼
   ┌───────┐
   │ Derby │ ← Persist en BD
   └───┬───┘
       │
       ▼
  login.html ← Redirect avec succès
       │ POST /login
       ▼
┌──────────────────┐
│  LoginServlet    │ ← BCrypt verify
└──────┬───────────┘
       │ HttpSession
       ├─ user: "jered"
       ├─ email: "jered@jered.com"
       └─ userId: 1
       │
       ▼
  home.xhtml ← Session active
       │ GET /api/userinfo
       ▼
┌──────────────────┐
│ UserInfoServlet  │ ← JSON response
└──────┬───────────┘
       │ {success: true, username: "jered", ...}
       ▼
  localStorage ← Client storage
       │
       ▼
pages/a_propos.xhtml ← Profile page
       │ onclick logout
       ▼
┌─────────────────┐
│ LogoutServlet   │ ← Invalidate session
└─────┬───────────┘
      │
      ▼
 login.html ← Redirect
```

### Structure BD (Derby)

```sql
CREATE TABLE UTILISATEUR (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL (BCrypt),
    description VARCHAR(500)
);
```

### Sécurité

✅ **Authentification:** BCrypt (12 rounds)
✅ **Session:** HttpSession avec timeout
✅ **Autorisation:** SessionControlFilter (/pages/\*)
✅ **Validation:** Input validation côté serveur
✅ **Transport:** HTTPS recommended en production

---

## 📝 Code Source

### UserInfoServlet.java

```java
@WebServlet(name = "UserInfoServlet", urlPatterns = {"/api/userinfo"})
public class UserInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);

        if (session != null) {
            String username = (String) session.getAttribute("user");
            String email = (String) session.getAttribute("email");
            Long userId = (Long) session.getAttribute("userId");

            if (username != null && email != null) {
                out.print("{\"success\": true, \"username\": \"" +
                          escapeJson(username) + "\", \"email\": \"" +
                          escapeJson(email) + "\", \"userId\": " + userId + "}");
            } else {
                out.print("{\"success\": false, \"message\": \"Infos non trouvées\"}");
            }
        } else {
            out.print("{\"success\": false, \"message\": \"Session non valide\"}");
        }
    }
}
```

### SessionControlFilter.java

```java
@WebFilter(urlPatterns = {"/pages/*"})
public class SessionControlFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.xhtml");
        } else {
            chain.doFilter(request, response);
        }
    }
}
```

### Configuration persistence.xml

```xml
<property name="jakarta.persistence.schema-generation.database.action" value="create"/>
<property name="eclipselink.ddl-generation" value="create-tables"/>
```

---

## ✅ Checklist de Validation

- [ ] Enregistrement utilisateur fonctionnel
- [ ] Connexion sécurisée (BCrypt)
- [ ] Session créée correctement
- [ ] Profil affiché sur home.xhtml
- [ ] Onglet Profil (read-only username/email)
- [ ] Onglet Mot de Passe (validation + succès)
- [ ] Onglet À Propos (information complète)
- [ ] Déconnexion fonctionne
- [ ] Protection SessionControlFilter active
- [ ] Persistence BD entre redéploiements
- [ ] Design responsive (mobile friendly)
- [ ] Tous les messages d'erreur affichés
- [ ] Tous les messages de succès affichés

---

## 🎓 Conclusions

Ce TP démontre une implémentation complète et sécurisée d'un système de gestion de profil utilisateur avec:

✅ Authentification robuste
✅ Gestion de session appropriée
✅ Interface utilisateur intuitive et responsive
✅ Validation client et serveur
✅ Persistence des données
✅ Sécurité suivant les best practices

**Date:** 16 janvier 2026
**Framework:** Jakarta EE 10
**Application:** Jakarta Mission
**État:** ✅ Production Ready

---

## 📚 Références

- [Jakarta EE 10 Documentation](https://jakarta.ee/)
- [GlassFish 7.0 Documentation](https://glassfish.org/)
- [BCrypt Password Hashing](https://www.mindrot.org/projects/jBCrypt/)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/)

---

**Generated for TP - Rapport Profil Utilisateur**
