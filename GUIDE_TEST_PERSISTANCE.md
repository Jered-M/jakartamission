# 📊 Guide de Test - Persistance des Données - register.html

## 🎯 Objectif

Vérifier que les données saisies dans le formulaire d'enregistrement (`register.html`) sont correctement persistées dans la base de données.

---

## 📋 Flux de Persistance

```
1. Utilisateur remplit le formulaire register.html
   - Username: chaîne de 3-50 caractères
   - Email: format valide
   - Password: 6+ caractères, 1 majuscule, 1 chiffre
   - ConfirmPassword: doit correspondre au password
   - Description: optionnel (jusqu'à 500 caractères)

2. POST envoyé à RegisterServlet (/register)
   - Validation côté serveur
   - Vérification des doublons (username et email)
   - Hachage du mot de passe avec BCrypt
   - Création de l'objet User

3. UtilisateurEntrepriseBean.creerUtilisateur()
   - Normalise l'email (minuscules)
   - Appelle em.persist(user) ← PERSISTANCE
   - Appelle em.flush() ← FORCE L'ÉCRITURE EN BD

4. Base de données (Derby embedded)
   - Insère la ligne dans la table UTILISATEUR
   - Génère l'ID automatiquement
   - Redirection vers login.html avec success=Compte créé

5. Vérification
   - Retourner à login.html
   - Se connecter avec le nouveau compte
   - Vérifier l'accès à home.xhtml
```

---

## 🧪 Étapes de Test

### **Test 1: Enregistrement Simple**

#### 1.1 Ouvrir le formulaire

```
URL: http://desktop-516db8b:8080/jakartamission/register.html
```

#### 1.2 Remplir le formulaire

```
Nom d'utilisateur: TestUser2026
Email: testuser@example.com
Mot de passe: Test1234
Confirmer: Test1234
Biographie: Je suis un utilisateur de test
```

#### 1.3 Soumettre

- Cliquer sur **"Créer mon compte"**

#### 1.4 Résultat attendu

```
✓ Redirigé vers login.html
✓ Message: "Compte créé avec succès ! Connectez-vous."
```

---

### **Test 2: Vérifier la Persistance**

#### 2.1 Se connecter avec le nouveau compte

```
Email: testuser@example.com
Mot de passe: Test1234
URL: Cliquer sur "Retour à l'accueil" → login.html
```

#### 2.2 Résultat attendu

```
✓ Message d'erreur si non trouvé en BD
✓ Message de succès si trouvé → Redirige à home.xhtml
```

#### 2.3 Si succès

```
✓ Affiche home.xhtml
✓ Utilisateur authentifié
✓ Données persistées correctement
```

---

### **Test 3: Valeurs par Défaut Doivent Être Rejetées**

| Test                      | Valeur        | Résultat                        |
| ------------------------- | ------------- | ------------------------------- |
| Username court            | "ab"          | ❌ Erreur: 3-50 caractères      |
| Username long             | 51 caractères | ❌ Erreur: 3-50 caractères      |
| Email invalide            | "notanemail"  | ❌ Erreur: Format invalide      |
| Password court            | "Test1"       | ❌ Erreur: 6+ caractères        |
| Password sans majuscule   | "test1234"    | ❌ Erreur: 1 majuscule requis   |
| Password sans chiffre     | "TestUser"    | ❌ Erreur: 1 chiffre requis     |
| Passwords non concordants | pwd1 ≠ pwd2   | ❌ Erreur: Doivent correspondre |

---

### **Test 4: Doublons**

#### 4.1 Enregistrer un premier compte

```
Email: unique@example.com
```

#### 4.2 Essayer d'enregistrer le même email

```
Résultat: ❌ "Cet email existe déjà"
```

#### 4.3 Essayer le même username

```
Résultat: ❌ "Ce nom d'utilisateur existe déjà"
```

---

## 🔍 Vérification en Base de Données

### Option 1: Via NetBeans (Derby embedded)

```
1. Services → Databases
2. java db (Network)
3. Connexion DB définie dans GlassFish
4. Table: UTILISATEUR
5. Colonnes:
   - ID (clé primaire)
   - USERNAME (unique)
   - EMAIL (unique)
   - PASSWORD (hash BCrypt)
   - DESCRIPTION (optionnel)
```

### Option 2: Via Diagnostic Servlet

```
URL: http://desktop-516db8b:8080/jakartamission/diagnostic
- Affiche tous les utilisateurs
- Teste l'authentification admin
- Vérifie la connexion BD
```

---

## 📊 Architecture Persistance

```
┌────────────────────────────────────────────────────┐
│  register.html (Formulaire HTML)                  │
└────────────────────┬─────────────────────────────┘
                     │ POST
                     ↓
┌────────────────────────────────────────────────────┐
│  RegisterServlet (/register)                      │
│  - Valide les données                             │
│  - Vérifie les doublons                           │
│  - Appelle creerUtilisateur()                     │
└────────────────────┬─────────────────────────────┘
                     │
                     ↓
┌────────────────────────────────────────────────────┐
│  UtilisateurEntrepriseBean                        │
│  - creerUtilisateur(username, email, pwd, desc) │
│  - em.persist(user) ← JPA Entity Manager         │
│  - em.flush() ← ÉCRIT EN BD IMMÉDIATEMENT        │
└────────────────────┬─────────────────────────────┘
                     │
                     ↓
┌────────────────────────────────────────────────────┐
│  EclipseLink ORM (JPA)                           │
│  - Génère SQL INSERT                              │
│  - Exécute la transaction                         │
└────────────────────┬─────────────────────────────┘
                     │
                     ↓
┌────────────────────────────────────────────────────┐
│  Derby Embedded Database                         │
│  - Table: UTILISATEUR                            │
│  - Insère la ligne                               │
│  - Génère l'ID (IDENTITY)                        │
│  - COMMIT la transaction                         │
└────────────────────────────────────────────────────┘
                     │
                     ↓
┌────────────────────────────────────────────────────┐
│  RegisterServlet                                  │
│  - Redirection success → login.html              │
│  - ou error → register.html                      │
└────────────────────────────────────────────────────┘
```

---

## 🔐 Données Persistées

### Champs Stockés

```
user {
  id: Long (auto-généré)
  username: String (unique)
  email: String (unique, normalisé en minuscules)
  password: String (hash BCrypt, format: $2a$10$...)
  description: String (optionnel, NULL si vide)
  created_at: Timestamp (auto-généré, optionnel)
}
```

### Validation au Niveau Base

- `username`: NOT NULL, UNIQUE, 3-50 caractères
- `email`: NOT NULL, UNIQUE, 100 caractères max
- `password`: NOT NULL, ~60 caractères (BCrypt hash)
- `description`: NULL allowed, 500 caractères max

---

## ✅ Checklist de Test Complet

### Avant de Lancer les Tests

- [ ] GlassFish est lancé
- [ ] Base de données Derby est accessible
- [ ] Application déployée (Ctrl+Shift+F11 dans NetBeans)
- [ ] F6 pour lancer l'app

### Test de Persistance

- [ ] Test 1: Enregistrement simple

  - [ ] Remplir formulaire avec données valides
  - [ ] Soumettre
  - [ ] Redirection à login.html
  - [ ] Message de succès affiché

- [ ] Test 2: Vérifier persistance

  - [ ] Se connecter avec le nouveau compte
  - [ ] Accès à home.xhtml
  - [ ] Données correctes affichées

- [ ] Test 3: Validations

  - [ ] Username court → Erreur
  - [ ] Email invalide → Erreur
  - [ ] Password faible → Erreur
  - [ ] Passwords non concordants → Erreur

- [ ] Test 4: Doublons

  - [ ] Enregistrer 1er compte
  - [ ] Réenregistrer avec même email → Erreur
  - [ ] Réenregistrer avec même username → Erreur

- [ ] Test 5: Base de données
  - [ ] Vérifier table UTILISATEUR
  - [ ] Vérifier données insérées
  - [ ] Vérifier contraintes (UNIQUE, NOT NULL)

---

## 🐛 Dépannage

### Problème: "Une erreur est survenue lors de la création du compte"

**Causes possibles:**

1. GlassFish ne s'est pas lancé
2. Base de données Derby non accessible
3. Source de données `jdbc/jakartamission` non configurée
4. Exception levée dans creerUtilisateur()

**Solution:**

1. Vérifier les logs GlassFish
2. Vérifier que le DataSource est configuré dans GlassFish Admin Console
3. Utiliser DiagnosticServlet pour tester la connexion BD

### Problème: "Cet email existe déjà" (lors du test 4)

**Normal!** Cela signifie que:

- [ ] Premier enregistrement a réussi
- [ ] Données ont été persistées
- [ ] Vérification de doublon fonctionne ✓

### Problème: Impossible de se connecter après enregistrement

**Causes possibles:**

1. Utilisateur créé mais email normalisé différemment
2. Hash du mot de passe incorrect
3. Session non créée

**Solution:**

- Vérifier les logs pour erreurs
- Vérifier que email est en minuscules dans BD
- Tester avec compte admin@example.com/admin

---

## 📝 Logs Attendus

Lors d'un enregistrement réussi, vous devriez voir:

```log
[RegisterServlet] POST request reçue
[RegisterServlet] Username: TestUser2026
[RegisterServlet] Email: testuser@example.com
[RegisterServlet] Création de l'utilisateur...
[RegisterServlet] Utilisateur créé avec succès
```

Puis lors de la connexion:

```log
[LoginServlet] Recherche utilisateur avec email: testuser@example.com
[SUCCESS] Authentification réussie pour: TestUser2026
[DEBUG] Session créée avec: TestUser2026
```

---

## 🎯 Conclusion

La persistance des données fonctionne correctement si:

1. ✅ Formulaire accepte les données
2. ✅ RegisterServlet valide et traite les données
3. ✅ UtilisateurEntrepriseBean appelle em.persist() et em.flush()
4. ✅ Données présentes dans la table UTILISATEUR
5. ✅ Utilisateur peut se connecter après enregistrement
6. ✅ Doublons d'email/username sont rejetés

**État attendu: COMPLET ET FONCTIONNEL** ✓
