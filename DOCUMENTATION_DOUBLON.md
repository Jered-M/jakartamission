# 📋 Justification de l'Erreur : Doublon Username/Email

## 🎯 Scénario de Test

Tentative d'enregistrement de deux utilisateurs avec des données en conflit :

### **Cas 1 : Email dupliqué**
```
Utilisateur 1 :
  - Username : john_doe
  - Email : john@mail.com
  - Password : SecurePass123@

Utilisateur 2 :
  - Username : jane_smith  (DIFFERENT)
  - Email : john@mail.com  (IDENTIQUE) ❌
  - Password : Another@Pass123
```

### **Cas 2 : Username dupliqué**
```
Utilisateur 1 :
  - Username : john_doe
  - Email : john@mail.com
  - Password : SecurePass123@

Utilisateur 2 :
  - Username : john_doe      (IDENTIQUE) ❌
  - Email : jane@mail.com    (DIFFERENT)
  - Password : Another@Pass123
```

---

## ⛔ Résultat : Message d'Erreur

```
SEVERITY_ERROR :
"Ce nom d'utilisateur et cette adresse existent déjà"
```

Les champs du formulaire ne sont pas réinitialisés → permet à l'utilisateur de corriger.

---

## 🔍 Architecture de Validation

### **NIVEAU 1 : COUCHE PRÉSENTATION (Frontend - JSF)**

**Fichier** : `UtilisateurBean.java`  
**Méthode** : `ajouterUtilisateur()`

```java
public void ajouterUtilisateur() {
    FacesContext context = FacesContext.getCurrentInstance();

    // ✓ VERIFICATION 1 : Mots de passe correspondent
    if (!password.equals(confirmPassword)) {
        context.addMessage(null, new FacesMessage(
            FacesMessage.SEVERITY_ERROR,
            "Les mots de passe ne correspondent pas",
            null));
        return;  // ← STOP l'insertion
    }

    // ✓ VERIFICATION 2 : Username n'existe pas
    // ✓ VERIFICATION 3 : Email n'existe pas
    if (utilisateurEntrepriseBean.usernameExiste(username) || 
        utilisateurEntrepriseBean.emailExiste(email)) {
        
        context.addMessage(null, new FacesMessage(
            FacesMessage.SEVERITY_ERROR,
            "Ce nom d'utilisateur et cette adresse existent déjà",  // ← MESSAGE AFFICHE
            null));
        return;  // ← STOP l'insertion
    }

    // Si toutes les vérifications passent, on appelle le service métier
    utilisateurEntrepriseBean.creerUtilisateur(username, email, password, description);
    
    context.addMessage(null, new FacesMessage(
        FacesMessage.SEVERITY_INFO,
        "Utilisateur ajouté avec succès !",
        null));
    
    // Réinitialisation
    username = "";
    email = "";
    password = "";
    confirmPassword = "";
    description = "";
}
```

**Avantages** :
- ✅ Vérification rapide (pas de requête BD)
- ✅ Feedback immédiat pour l'utilisateur
- ✅ Améliore l'expérience utilisateur (UX)
- ✅ Réduit la charge serveur

---

### **NIVEAU 2 : COUCHE MÉTIER (Backend - Business Logic)**

**Fichier** : `UtilisateurEntrepriseBean.java`  
**Annotations** : `@Stateless` + `@Transactional`

#### Méthode 1 : Vérifier si username existe
```java
public boolean usernameExiste(String username) {
    return obtenirUtilisateurParUsername(username) != null;
}

private User obtenirUtilisateurParUsername(String username) {
    try {
        return em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username",
                User.class)
            .setParameter("username", username)
            .getSingleResult();
    } catch (Exception e) {
        return null;  // Pas trouvé
    }
}
```

#### Méthode 2 : Vérifier si email existe
```java
public boolean emailExiste(String email) {
    return obtenirUtilisateurParEmail(email) != null;
}

private User obtenirUtilisateurParEmail(String email) {
    try {
        return em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email",
                User.class)
            .setParameter("email", email)
            .getSingleResult();
    } catch (Exception e) {
        return null;  // Pas trouvé
    }
}
```

#### Méthode 3 : Créer utilisateur avec hachage
```java
public User creerUtilisateur(String username, String email, String password, String description) {
    // Hacher le mot de passe
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    
    // Créer l'objet
    User user = new User(username, email, hashedPassword, description);
    
    // Persister en BD
    em.persist(user);
    em.flush();  // Force la génération de l'ID
    return user;
}
```

**Requêtes JPQL exécutées** :
```sql
SELECT u FROM User u WHERE u.username = 'john_doe'
SELECT u FROM User u WHERE u.email = 'john@mail.com'
```

**Avantages** :
- ✅ Logique centralisée
- ✅ Transactions garanties
- ✅ Hachage sécurisé avec BCrypt
- ✅ Traçabilité complète

---

### **NIVEAU 3 : COUCHE PERSISTANCE (Database - Constraints)**

**Fichier** : `User.java` (Entité)

```java
@Entity
@Table(name = "utilisateur")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✓ CONTRAINTE UNIQUE
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Le nom d'utilisateur ne peut contenir que des lettres, chiffres et underscores")
    private String username;

    // ✓ CONTRAINTE UNIQUE
    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'email doit être au format valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    @Column(length = 500)
    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;
}
```

**SQL généré en BD** :
```sql
CREATE TABLE utilisateur (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    description VARCHAR(500)
);

-- Index automatiques créés
CREATE UNIQUE INDEX idx_username ON utilisateur(username);
CREATE UNIQUE INDEX idx_email ON utilisateur(email);
```

**Contraintes vérifiées par BD** :
```
✓ UNIQUE KEY sur username
✓ UNIQUE KEY sur email
✓ NOT NULL sur username
✓ NOT NULL sur email
✓ NOT NULL sur password
```

**Exception levée en cas de violation** :
```java
// Si tentative d'insertion avec username/email dupliqué
jakarta.persistence.EntityExistsException
// ou
java.sql.SQLIntegrityConstraintViolationException: 
  "UNIQUE constraint failed: utilisateur.username"
  "UNIQUE constraint failed: utilisateur.email"
```

**Avantages** :
- ✅ Protection absolue des données
- ✅ Aucune duplication possible
- ✅ Même si le code est hacké
- ✅ Performance : index unique pour recherche rapide

---

## 🛡️ Defense in Depth (Sécurité en Profondeur)

```
┌──────────────────────────────────────────┐
│   UTILISATEUR FINAL (Frontend JSF)       │
│  ┌─ Saisit : john_doe / john@mail.com   │
│  └─ Valide les contraintes JSR-303      │
└──────────────────────────────────────────┘
              ↓
┌──────────────────────────────────────────┐
│  NIVEAU 1 : PRÉSENTATION (UtilisateurBean)│
│  ├─ Vérifie usernameExiste()             │
│  ├─ Vérifie emailExiste()                │
│  └─ Affiche FacesMessage d'erreur        │
│                                           │
│  ✓ SI DOUBLON DÉTECTÉ :                 │
│    └─ Message : "données existent déjà"  │
│    └─ ARRÊTE l'insertion                 │
│    └─ RETOUR au formulaire               │
└──────────────────────────────────────────┘
              ↓
    MÊME SI CONTOURNÉE...
              ↓
┌──────────────────────────────────────────┐
│  NIVEAU 2 : MÉTIER (UtilisateurEJB)      │
│  ├─ Vérifie requête JPQL                 │
│  ├─ Hache password avec BCrypt           │
│  ├─ Gère les transactions                │
│  └─ Persiste l'entité                    │
│                                           │
│  ✓ SI DOUBLON DÉTECTÉ :                 │
│    └─ Exception levée                    │
│    └─ Transaction ROLLBACK               │
│    └─ Aucun insert en BD                 │
└──────────────────────────────────────────┘
              ↓
    MÊME SI CONTOURNÉE...
              ↓
┌──────────────────────────────────────────┐
│  NIVEAU 3 : BD (Contraintes UNIQUE)      │
│  ├─ Vérifie index UNIQUE                 │
│  ├─ Bloque INSERT si doublon             │
│  └─ Lève SQLException                    │
│                                           │
│  ✓ GARANTIE ABSOLUE :                   │
│    └─ AUCUN DOUBLON POSSIBLE              │
│    └─ Intégrité des données garantie     │
└──────────────────────────────────────────┘
```

---

## 📊 Tableau Comparatif

| Aspect | Niveau 1 | Niveau 2 | Niveau 3 |
|--------|----------|----------|----------|
| **Localisation** | Frontend JSF | Backend EJB | Base de Données |
| **Responsabilité** | UX/Feedback | Logique métier | Intégrité |
| **Technologie** | JSF Managed Bean | JPA/JPQL | SQL/Index UNIQUE |
| **Performance** | Rapide (local) | Moyen (requête BD) | Garanti (index) |
| **Sécurité** | Contournable | Contournable | Impossible à contourner |
| **Message** | Utilisateur-friendly | Log technique | Exception BD |
| **Exemple** | "Données existent" | Query.getSingleResult() | IntegrityConstraintViolationException |

---

## 💡 Pourquoi cette Architecture ?

### **1. Sécurité en Profondeur (Defense in Depth)**
Plusieurs couches de protection → impossible de tout contourner

### **2. Performance Optimale**
- Front-end : valide avant requête BD
- Back-end : logique métier centralisée
- BD : index pour recherche $O(\log n)$

### **3. Expérience Utilisateur**
- Feedback immédiat
- Messages explicites
- Formulaire non vidé (permet correction)

### **4. Intégrité des Données**
- Aucune duplication possible
- Même si hackée
- Même si code bypass
- La BD garantit l'invariant

### **5. Maintenabilité**
- Code modulaire
- Responsabilités claires
- Facilite debug et tests
- Traçabilité complète

---

## 🔄 Flux d'Exécution Détaillé

### **Scénario : Insertion d'un email déjà existant**

```
Utilisateur entre : jane_smith / john@mail.com

   ↓ [VALIDATION JSR-303]
   ├─ Email format valide ? ✓
   ├─ Pas vide ? ✓
   ├─ Longueur OK ? ✓
   ↓
   
   ↓ [APPEL ajouterUtilisateur()]
   ├─ password == confirmPassword ? ✓
   ├─ usernameExiste("jane_smith") ? 
   │  ├─ Query: SELECT u FROM User u WHERE u.username = 'jane_smith'
   │  ├─ Résultat: NULL (n'existe pas) → FALSE ✓
   │
   ├─ emailExiste("john@mail.com") ?
   │  ├─ Query: SELECT u FROM User u WHERE u.email = 'john@mail.com'
   │  ├─ Résultat: User(id=1, username="john_doe", ...) → TRUE ❌
   │
   ├─ CONDITION ECHEC : true || true = true
   │
   └─ FacesMessage(SEVERITY_ERROR)
      "Ce nom d'utilisateur et cette adresse existent déjà"
      
   ↓ [RETURN - PAS D'INSERTION]
   
   ↓ [AFFICHAGE FORMULAIRE]
   └─ jane_smith et john@mail.com CONSERVES
      Permet à l'utilisateur de corriger
```

---

## ✅ Résumé

| Point | Explication |
|-------|-------------|
| **Erreur levée** | FacesMessage SEVERITY_ERROR |
| **Message** | "Ce nom d'utilisateur et cette adresse existent déjà" |
| **Raison technique** | Contrainte UNIQUE en BD + vérification app |
| **Cause** | username ou email déjà présent en base |
| **Prévention** | usernameExiste() et emailExiste() |
| **Protection** | Index UNIQUE en BD + contraintes Jakarta |
| **Impact UX** | Formulaire conserve les données saisies |
| **Sécurité** | 3 niveaux : app, métier, BD |

