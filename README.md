# 📚 Jakarta Mission - Guide Complet du Projet

## 📋 Table des matières
1. [Vue d'ensemble](#-vue-densemble)
2. [Architecture du projet](#-architecture-du-projet)
3. [Étapes de développement](#-étapes-de-développement)
4. [Entité User](#-entité-user)
5. [Logique métier](#-logique-métier)
6. [Managed Bean JSF](#-managed-bean-jsf)
7. [Hachage des mots de passe](#-hachage-des-mots-de-passe)
8. [Validation et contraintes](#-validation-et-contraintes)
9. [Test - Erreur de doublon](#-test---erreur-de-doublon)
10. [Déploiement](#-déploiement)

---

## 🎯 Vue d'ensemble

**Jakarta Mission** est une application web Jakarta EE moderne permettant l'enregistrement et la gestion des utilisateurs avec :
- ✅ Architecture 3 niveaux (Présentation, Métier, Persistance)
- ✅ Sécurité avec hachage BCrypt
- ✅ Validation en plusieurs niveaux
- ✅ Interface JSF élégante avec Bootstrap
- ✅ Base de données avec contraintes UNIQUE

---

## 🏗️ Architecture du projet

```
jakartamission/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── jakartamission/udbl/jakartamission/
│   │   │       ├── entities/
│   │   │       │   └── User.java                      [1] Entité JPA
│   │   │       ├── business/
│   │   │       │   └── UtilisateurEntrepriseBean.java [2] Logique métier
│   │   │       ├── beans/
│   │   │       │   └── UtilisateurBean.java           [3] Managed Bean JSF
│   │   │       ├── converter/
│   │   │       ├── resources/
│   │   │       └── utils/
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml                    [4] Configuration JPA
│   │   └── webapp/
│   │       ├── ajouter_utilisateur.xhtml              [5] Formulaire
│   │       ├── index.xhtml
│   │       ├── index.html
│   │       └── WEB-INF/
│   │           ├── glassfish-web.xml                  [6] Config GlassFish
│   │           ├── web.xml
│   │           ├── beans.xml
│   │           └── faces-config.xml
│   └── test/
│       └── java/
│           └── UtilisateurEntrepriseBeanTest.java     [7] Tests unitaires
├── pom.xml                                            [8] Dépendances Maven
└── DOCUMENTATION_DOUBLON.md                           [9] Justification erreurs
```

---

## 📝 Étapes de développement

### **Étape 1 : Création de l'Entité User** ✅

**Fichier** : `src/main/java/jakartamission/udbl/jakartamission/entities/User.java`

```java
@Entity
@Table(name = "utilisateur")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 8)
    private String password;

    @Column(length = 500)
    @Size(max = 500)
    private String description;

    // Constructeurs, Getters, Setters
}
```

**Contraintes** :
- ✅ `@Column(unique = true)` sur username et email
- ✅ Validations Jakarta Bean Validation
- ✅ Constructeur par défaut + initialisation
- ✅ Accesseurs et mutateurs complets

---

### **Étape 2 : Logique Métier - UtilisateurEntrepriseBean** ✅

**Fichier** : `src/main/java/jakartamission/udbl/jakartamission/business/UtilisateurEntrepriseBean.java`

```java
@Stateless
@Transactional
public class UtilisateurEntrepriseBean {
    
    @PersistenceContext(unitName = "jakartamissionPU")
    private EntityManager em;

    // CREATE
    public User creerUtilisateur(String username, String email, String password, String description) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(username, email, hashedPassword, description);
        em.persist(user);
        em.flush();
        return user;
    }

    // READ
    public User obtenirUtilisateurParId(Long id) { ... }
    public User obtenirUtilisateurParUsername(String username) { ... }
    public User obtenirUtilisateurParEmail(String email) { ... }
    public List<User> obtenirTousLesUtilisateurs() { ... }

    // UPDATE
    public User modifierUtilisateur(User user) { ... }

    // DELETE
    public boolean supprimerUtilisateur(Long id) { ... }
    public boolean supprimerUtilisateurParUsername(String username) { ... }

    // UTILITY
    public boolean usernameExiste(String username) { ... }
    public boolean emailExiste(String email) { ... }
    public boolean verifierMotDePasse(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
```

**Annotations** :
- ✅ `@Stateless` : EJB stateless
- ✅ `@Transactional` : Gestion des transactions
- ✅ `@PersistenceContext` : Injection EntityManager

---

### **Étape 3 : Managed Bean JSF - UtilisateurBean** ✅

**Fichier** : `src/main/java/jakartamission/udbl/jakartamission/beans/UtilisateurBean.java`

```java
@Named("utilisateurBean")
@RequestScoped
public class UtilisateurBean implements Serializable {
    
    @Inject
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String description;

    public void ajouterUtilisateur() {
        FacesContext context = FacesContext.getCurrentInstance();

        // VALIDATION 1 : Mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Les mots de passe ne correspondent pas",
                null));
            return;
        }

        // VALIDATION 2 : Pas de doublon
        if (utilisateurEntrepriseBean.usernameExiste(username) || 
            utilisateurEntrepriseBean.emailExiste(email)) {
            context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Ce nom d'utilisateur et cette adresse existent déjà",
                null));
            return;
        }

        // INSERTION avec mot de passe haché
        utilisateurEntrepriseBean.creerUtilisateur(username, email, password, description);

        context.addMessage(null, new FacesMessage(
            FacesMessage.SEVERITY_INFO,
            "Utilisateur ajouté avec succès !",
            null));

        // Réinitialisation
        reinitialiserFormulaire();
    }

    private void reinitialiserFormulaire() {
        username = "";
        email = "";
        password = "";
        confirmPassword = "";
        description = "";
    }
}
```

**Annotations** :
- ✅ `@Named` : Accessible en JSF
- ✅ `@RequestScoped` : Portée requête HTTP
- ✅ `@Inject` : Injection du service métier

---

### **Étape 4 : Dépendances Maven** ✅

**Fichier** : `pom.xml`

```xml
<dependencies>
    <!-- Jakarta EE API -->
    <dependency>
        <groupId>jakarta.platform</groupId>
        <artifactId>jakarta.jakartaee-api</artifactId>
        <version>10.0.0</version>
        <scope>provided</scope>
    </dependency>

    <!-- BCrypt pour hachage des mots de passe -->
    <dependency>
        <groupId>org.mindrot</groupId>
        <artifactId>jbcrypt</artifactId>
        <version>0.4</version>
    </dependency>

    <!-- JUnit 5 pour les tests -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.9.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.9.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🔐 Hachage des mots de passe

### **Pourquoi hacher les mots de passe ?**

| Raison | Explication |
|--------|-------------|
| **Sécurité** | Si la BD est compromise, les vrais mots de passe restent secrets |
| **Irréversible** | Impossible de récupérer le mot de passe du hash |
| **Unicité** | 2 mots de passe identiques donnent 2 hashes différents (salt) |
| **Conformité** | RGPD, OWASP, standards de sécurité |

### **Implémentation BCrypt**

```java
// HACHAGE (lors de la création)
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
user.setPassword(hashedPassword);  // Sauvegarde le hash, pas le password

// VÉRIFICATION (lors de la connexion)
boolean isPasswordCorrect = BCrypt.checkpw(inputPassword, storedHash);
```

### **Exemple**

```
Mot de passe saisi  : "MyPassword123@"
Hash stocké en BD   : "$2a$10$wI9RGz0CxaO0cXKJZEbVS.lGTXSJVbcSL1qSQNPLRlgQX4xQKLb/m"

Vérification        : BCrypt.checkpw("MyPassword123@", hash)
Résultat           : ✓ TRUE
```

---

## ✅ Validation et contraintes

### **NIVEAU 1 : Annotations Jakarta Bean Validation**

```java
@NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
@Size(min = 3, max = 50)
@Pattern(regexp = "^[a-zA-Z0-9_]+$")
private String username;

@Email(message = "L'email doit être valide")
@NotBlank
private String email;

@Size(min = 8)
@NotBlank
private String password;
```

### **NIVEAU 2 : Contraintes JPA (@Column)**

```java
@Column(unique = true, nullable = false, length = 50)
private String username;

@Column(unique = true, nullable = false, length = 100)
private String email;
```

### **NIVEAU 3 : Contraintes Base de Données**

```sql
CREATE TABLE utilisateur (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    description VARCHAR(500)
);

CREATE UNIQUE INDEX idx_username ON utilisateur(username);
CREATE UNIQUE INDEX idx_email ON utilisateur(email);
```

---

## 🔍 Test - Erreur de doublon

### **Scénario**

**1. Premier utilisateur enregistré** ✓
```
Username : john_doe
Email    : john@mail.com
```

**2. Tentative de 2e utilisateur avec email identique** ✗
```
Username : jane_smith (DIFFERENT)
Email    : john@mail.com (IDENTIQUE)
```

### **Résultat**

```
ERREUR AFFICHÉE :
"Ce nom d'utilisateur et cette adresse existent déjà"

Pourquoi ?
1. UtilisateurBean appelle emailExiste("john@mail.com")
2. Retourne TRUE (email déjà en BD)
3. FacesMessage d'erreur affichée
4. Insertion BLOQUÉE
5. Champs du formulaire CONSERVÉS pour correction
```

### **Architecture de protection**

```
┌────────────────────────────────────┐
│ NIVEAU 1 : FRONTEND (JSF)          │
│ ├─ emailExiste("john@mail.com")    │
│ ├─ usernameExiste("jane_smith")    │
│ └─ Si doublon : Message d'erreur   │
└────────────────────────────────────┘
              ↓
MÊME SI CONTOURNÉE...
              ↓
┌────────────────────────────────────┐
│ NIVEAU 2 : MÉTIER (EJB)            │
│ ├─ Requête JPQL                    │
│ ├─ Gestion transactionnelle        │
│ └─ Hachage BCrypt                  │
└────────────────────────────────────┘
              ↓
MÊME SI CONTOURNÉE...
              ↓
┌────────────────────────────────────┐
│ NIVEAU 3 : BD (Contrainte UNIQUE)  │
│ ├─ Index UNIQUE                    │
│ ├─ Bloque la duplication           │
│ └─ SQLException                    │
└────────────────────────────────────┘
```

---

## 🌐 Interface utilisateur

### **Page d'enregistrement** : `ajouter_utilisateur.xhtml`

**Caractéristiques** :
- ✅ Bootstrap 5.3 pour le design responsive
- ✅ Dégradé violet moderne
- ✅ Boutons afficher/masquer mot de passe
- ✅ Messages de succès/erreur stylisés
- ✅ Validation côté client
- ✅ Animations fluides

**Formulaire**
```xhtml
<h:form>
    <h:inputText id="username" value="#{utilisateurBean.username}" />
    <h:inputText id="email" value="#{utilisateurBean.email}" />
    <h:inputSecret id="password" value="#{utilisateurBean.password}" />
    <h:inputSecret id="confirmPassword" value="#{utilisateurBean.confirmPassword}" />
    <h:inputTextarea id="description" value="#{utilisateurBean.description}" />
    
    <h:commandButton value="Créer mon compte" 
                     action="#{utilisateurBean.ajouterUtilisateur}" />
    
    <h:messages globalOnly="true" />
</h:form>
```

---

## 🚀 Déploiement

### **Configuration GlassFish**

**Fichier** : `src/main/webapp/WEB-INF/glassfish-web.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE glassfish-web-app PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Servlet 3.0//EN" 
    "http://glassfish.org/dtds/glassfish-web-app_3_0-1.dtd">
<glassfish-web-app error-url="">
  <context-root>/jakartamission</context-root>
  <class-loader delegate="true"/>
  <jsp-config>
    <property name="keepgenerated" value="true">
      <description>Keep a copy of the generated servlet class' java code.</description>
    </property>
  </jsp-config>
</glassfish-web-app>
```

### **Configuration Persistance**

**Fichier** : `src/main/resources/META-INF/persistence.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.0">
  <persistence-unit name="jakartamissionPU" transaction-type="JTA">
    <jta-data-source>jdbc/jakartamission</jta-data-source>
    <class>jakartamission.udbl.jakartamission.entities.User</class>
    <properties>
      <property name="eclipselink.ddl-generation" value="create-tables"/>
    </properties>
  </persistence-unit>
</persistence>
```

### **Étapes de déploiement**

1. **Build du projet** :
```bash
mvn clean package
```

2. **À partir de NetBeans** :
```
Projects > jakartamission (right-click) > Run
```

3. **Accès à l'application** :
```
http://localhost:8080/jakartamission
```

---

## 📦 Structure des classes

### **Flux de données**

```
Utilisateur remplit le formulaire
    ↓
JSF (ajouter_utilisateur.xhtml)
    ↓
UtilisateurBean.ajouterUtilisateur()
    ├─ Valide les données (mots de passe, unicité)
    └─ Appelle UtilisateurEntrepriseBean.creerUtilisateur()
        ├─ Hash du mot de passe avec BCrypt
        ├─ Crée objet User
        └─ Persiste en BD via EntityManager
            ├─ Respects des contraintes @Column
            └─ Vérification des index UNIQUE en BD
```

---

## 🔧 Technologies utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| **Jakarta EE** | 10.0.0 | Framework principal |
| **JSF (Jakarta Faces)** | - | Présentation web |
| **JPA (Jakarta Persistence)** | - | ORM |
| **EJB** | - | Logique métier |
| **BCrypt** | 0.4 | Hachage mots de passe |
| **Bootstrap** | 5.3 | Design front-end |
| **Maven** | 3.9.6 | Build & dépendances |
| **GlassFish** | 7.0.24 | Serveur d'application |
| **JUnit** | 5.9.3 | Tests unitaires |
| **JavaDB** | - | Base de données |

---

## ✨ Résumé des réalisations

✅ Entité User avec validations complètes  
✅ Service métier avec CRUD complet  
✅ Managed Bean JSF avec injection  
✅ Hachage BCrypt des mots de passe  
✅ Validation en 3 niveaux (app, métier, BD)  
✅ Interface Bootstrap élégante  
✅ Gestion des transactions  
✅ Tests unitaires JUnit 5  
✅ Configuration GlassFish  
✅ Documentation complète  

---

## 📞 Support

Pour toute question sur l'implémentation, consultez :
- `DOCUMENTATION_DOUBLON.md` : Justification des erreurs
- `src/main/java` : Code source complet
- `src/test/java` : Tests unitaires

**Projet créé le 19 décembre 2025**
