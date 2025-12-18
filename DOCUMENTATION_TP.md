# TP - Gestion des Lieux avec Jakarta EE (JSF)

## 📋 Projet : Gestion des Lieux en Indonésie

### 🎯 Objectif

Créer une application de gestion de lieux avec navigation JSF, formulaires et beans.

---

## 📁 Structure du Projet

### Pages JSF créées :

#### 1. **index.xhtml** - Page d'Accueil

- URL: `http://localhost:8080/jakartamission/index.xhtml`
- Contient: Drapeau de l'Indonésie + Bouton "Découvrir"
- Navigation: Vers home.xhtml
- Style: Bootstrap 5, gradient pourpre, carte blanche centrée

#### 2. **home.xhtml** - Accueil Principal

- URL: `http://localhost:8080/jakartamission/home.xhtml`
- Contient: 3 cartes avec 4 boutons d'action
  - Bouton "Ajouter un Lieu" → vers lieu.xhtml
  - Bouton "À Propos" → vers a_propos.xhtml
  - Bouton "Convertisseur de Monnaie" → accueil
  - Bouton "Retour" → vers index.xhtml
- Style: Bootstrap 5, cartes avec bordures, dégradé de fond

#### 3. **a_propos.xhtml** - Page À Propos

- URL: `http://localhost:8080/jakartamission/a_propos.xhtml`
- Contient:
  - Titre "À Propos"
  - Description de l'application
  - Liste des services
  - Informations sur la technologie
  - Texte sur l'Indonésie
- Style: Bootstrap 5, contenu en liste groupée

#### 4. **lieu.xhtml** - Formulaire Ajouter Lieu

- URL: `http://localhost:8080/jakartamission/lieu.xhtml`
- Contient: Formulaire JSF avec champs:
  - **Nom du Lieu** (text, requis)
  - **Description** (textarea)
  - **Latitude** (number, requis)
  - **Longitude** (number, requis)
- Boutons:
  - "Enregistrer Lieu" (submit)
  - "Retour" (vers home.xhtml)
- Style: Bootstrap 5, formulaire espacé, labels en gras

---

## ☕ Classe Java

### **NavigationBean.java**

- Package: `jakartamission.udbl.jakartamission.beans`
- Méthodes:
  - `voirApropos()` → retourne "apropos"
  - `ajouterLieu()` → retourne "lieu"
  - `allerAccueil()` → retourne "home"
  - `allerIndex()` → retourne "index"

---

## 📂 Configuration

### **faces-config.xml** - WEB-INF/faces-config.xml

- Règles de navigation entre les pages
- Redirection explicite activée
- Bean managé enregistré

---

## 🎨 Style Appliqué à Toutes les Pages

```css
- Bootstrap 5.3.0 CDN
- Gradient de fond: linear-gradient(135deg, #667eea 0%, #764ba2 100%)
- Cartes blanches centrées avec ombre
- Boutons colorés (danger, info, primary, secondary, success)
- Formulaires avec espacement et labels gras
- Responsive design
```

---

## 🔄 Flux de Navigation

```
index.xhtml
    ↓ (Découvrir)
home.xhtml
    ├─ (Ajouter un Lieu) → lieu.xhtml → home.xhtml
    ├─ (À Propos) → a_propos.xhtml → home.xhtml
    └─ (Retour) → index.xhtml
```

---

## 📸 Captures d'Écran à Générer

### Page 1: home.xhtml

- 4 cartes colorées
- 4 boutons avec différentes actions
- Titre "Accueil"

### Page 2: lieu.xhtml

- Titre "Ajouter un Lieu"
- 4 champs de formulaire
- 2 boutons (Enregistrer, Retour)

### Page 3: a_propos.xhtml

- Titre "À Propos"
- Contenu informatif avec liste
- Bouton "Retour à l'Accueil"

---

## 🚀 Déploiement

1. **Build Maven**: `mvn clean install`
2. **Déployer sur GlassFish** via NetBeans
3. **Accéder**: `http://localhost:8080/jakartamission/`

---

## 📝 Technologie Utilisée

- **Jakarta EE 10**
- **JSF (JavaServer Faces)**
- **Bootstrap 5.3.0**
- **Maven**
- **GlassFish 7**

---

## 🔗 GitHub

Repository: https://github.com/Jered-M/jakartamission

---

## ✅ Fonctionnalités Implémentées

✔️ Navigation explicite (faces-config.xml)
✔️ Navigation implicite (NavigationBean)
✔️ Formulaire JSF avec validation
✔️ Responsive design
✔️ Bootstrap 5 intégré
✔️ Pages stylisées
✔️ Managed Beans

---

**Date**: 18 décembre 2025
**Auteur**: Jered-M
**Email**: 22lm204@esisalama.org
