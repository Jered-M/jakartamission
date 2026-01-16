# Configuration des Variables d'Environnement

## Windows (PowerShell)

### Configuration Permanente

1. **Ouvrir les variables d'environnement système:**
   - `Win + X` → Système
   - Clic droit sur "Ce PC" → Propriétés
   - Clic sur "Paramètres avancés du système"
   - Onglet "Avancé" → Bouton "Variables d'environnement"

2. **Ajouter nouvelles variables:**
   - Cliquer sur "Nouvelle" sous "Variables utilisateur" ou "Variables système"
   - Ajouter:
     - `Nom de la variable`: `db.user`
     - `Valeur de la variable`: `APP`
   - Cliquer OK

3. **Ajouter le mot de passe de BD:**
   - `Nom de la variable`: `db.password`
   - `Valeur de la variable`: `APP_SECURE_PASSWORD_CHANGE_ME` (À remplacer!)

4. **Ajouter le chemin de la BD:**
   - `Nom de la variable`: `glassfish.database.path`
   - `Valeur de la variable`: `C:\glassfish-7.0.24\glassfish7\glassfish\databases\jakartamission`

5. **Redémarrer Glassfish et l'application pour activer**

### Configuration Temporaire (PowerShell)

```powershell
# Configuration pour la session courante
$env:db.user = "APP"
$env:db.password = "YOUR_SECURE_PASSWORD"
$env:glassfish.database.path = "C:\glassfish-7.0.24\glassfish7\glassfish\databases\jakartamission"

# Vérifier les variables
Get-ChildItem env:db.*
Get-ChildItem env:glassfish.*
```

### Configuration via deploy.ps1

Ajouter au script de déploiement:

```powershell
# Définir les variables d'environnement
[System.Environment]::SetEnvironmentVariable("db.user", "APP", "User")
[System.Environment]::SetEnvironmentVariable("db.password", "YOUR_SECURE_PASSWORD", "User")
[System.Environment]::SetEnvironmentVariable("glassfish.database.path", "C:\glassfish-7.0.24\glassfish7\glassfish\databases\jakartamission", "User")

# Redémarrer Glassfish
Write-Host "Redémarrage de Glassfish..."
& "C:\glassfish-7.0.24\glassfish7\bin\asadmin.bat" "restart-domain"
```

---

## Configuration via domain.xml de Glassfish

**Chemin**: `C:\glassfish-7.0.24\glassfish7\glassfish\domains\domain1\config\domain.xml`

Ajouter dans la section `<system-property>`:

```xml
<system-properties>
    <!-- Variables de sécurité pour la base de données -->
    <system-property name="db.user" value="APP"></system-property>
    <system-property name="db.password" value="YOUR_SECURE_PASSWORD"></system-property>
    <system-property name="glassfish.database.path" value="C:/glassfish-7.0.24/glassfish7/glassfish/databases/jakartamission"></system-property>
    
    <!-- Configuration debug -->
    <system-property name="debug.enabled" value="false"></system-property>
    <system-property name="logging.level" value="INFO"></system-property>
</system-properties>
```

---

## Configuration via Glassfish Admin Console

1. Ouvrir http://localhost:4848
2. Aller dans **Configurations** → **server-config** → **System Properties**
3. Ajouter les propriétés:

| Nom | Valeur |
|---|---|
| `db.user` | `APP` |
| `db.password` | `YOUR_SECURE_PASSWORD` |
| `glassfish.database.path` | `C:/glassfish-7.0.24/glassfish7/glassfish/databases/jakartamission` |

---

## Environnement de Production (Unix/Linux)

### Configuration via /etc/profile ou ~/.bashrc

```bash
# /etc/profile ou ~/.bashrc
export db.user="app_prod"
export db.password="COMPLEX_PASSWORD_MIN_16_CHARS_WITH_SPECIAL"
export glassfish.database.path="/opt/glassfish/databases/jakartamission"
export debug.enabled="false"
export logging.level="WARN"
```

### Configuration via systemd (service Glassfish)

Créer ou modifier `/etc/systemd/system/glassfish.service`:

```ini
[Unit]
Description=GlassFish Application Server
After=network.target

[Service]
Type=forking
User=glassfish
WorkingDirectory=/opt/glassfish

Environment="db.user=app_prod"
Environment="db.password=COMPLEX_PASSWORD"
Environment="glassfish.database.path=/opt/glassfish/databases/jakartamission"
Environment="debug.enabled=false"

ExecStart=/opt/glassfish/bin/asadmin start-domain
ExecStop=/opt/glassfish/bin/asadmin stop-domain
Restart=always

[Install]
WantedBy=multi-user.target
```

Recharger systemd:
```bash
sudo systemctl daemon-reload
sudo systemctl restart glassfish
```

---

## Recommandations de Sécurité

### 🔐 Mots de Passe Forts

Le `db.password` doit respecter:
- ✅ Minimum 16 caractères (recommandé)
- ✅ Majuscules et minuscules
- ✅ Chiffres
- ✅ Caractères spéciaux: `!@#$%^&*()-_=+[]{}|;:,.<>?`

**Exemple sécurisé:**
```
db.password=App#2025!Secure$Password123
```

### 🚫 À ÉVITER

```
db.password=APP              # ❌ Trop simple
db.password=password123      # ❌ Pas de majuscules
db.password=PASSWORD         # ❌ Pas de chiffres
```

### 🔒 Environnement de Production

1. **Jamais en dur** dans les fichiers de code
2. **Utiliser un gestionnaire de secrets**:
   - HashiCorp Vault
   - AWS Secrets Manager
   - Azure Key Vault
   - Kubernetes Secrets

3. **Rotation régulière** des mots de passe (tous les 90 jours)
4. **Audit logging** de tous les accès BD
5. **HTTPS obligatoire** sur tous les endpoints

---

## Vérification de la Configuration

### Tester que les variables sont bien définies

**Dans Java (TestBean ou diagnostic):**
```java
String dbUser = System.getProperty("db.user");
String dbPassword = System.getProperty("db.password");
String dbPath = System.getProperty("glassfish.database.path");

System.out.println("DB User: " + (dbUser != null ? "✓ Défini" : "✗ Non défini"));
System.out.println("DB Password: " + (dbPassword != null ? "✓ Défini" : "✗ Non défini"));
System.out.println("DB Path: " + (dbPath != null ? "✓ Défini" : "✗ Non défini"));
```

### Via glassfish CLI

```bash
cd C:\glassfish-7.0.24\glassfish7\bin

# Lister toutes les propriétés système
asadmin.bat list-system-properties

# Ajouter une propriété
asadmin.bat create-system-properties db.user=APP
asadmin.bat create-system-properties db.password=YOUR_SECURE_PASSWORD

# Vérifier
asadmin.bat get "server.system-properties.db.*"
```

---

## Troubleshooting

### Variables non trouvées au démarrage

1. Vérifier les noms exactement (case-sensitive sur Linux)
2. Redémarrer Glassfish **complètement**:
   ```bash
   asadmin.bat stop-domain
   asadmin.bat start-domain
   ```

3. Vérifier dans `domain.xml`:
   ```bash
   grep -n "db.user\|db.password" domain.xml
   ```

### Erreur de connexion BD

```
ERROR: Access denied for user 'APP'@'localhost' using password: YES
```

→ Vérifier que `db.password` correspond au password réel

### glassfish-resources.xml n'utilise pas les variables

→ Redémarrer le domaine Glassfish après modification

---

## Documentation Officielle

- [GlassFish Documentation](https://glassfish.org/)
- [System Properties in GlassFish](https://glassfish.org/docs/latest/ha-administration-guide/system-properties.html)
- [Derby Database Documentation](https://db.apache.org/derby/)

**Dernière mise à jour:** 16 janvier 2026
