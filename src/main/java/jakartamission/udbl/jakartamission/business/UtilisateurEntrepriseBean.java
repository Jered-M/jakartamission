package jakartamission.udbl.jakartamission.business;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakartamission.udbl.jakartamission.entities.User;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;

/**
 * Classe métier pour la gestion des utilisateurs
 * Fournit les opérations CRUD (Create, Read, Update, Delete)
 */
@Stateless
public class UtilisateurEntrepriseBean {

    /**
     * Injection du gestionnaire d'entités via @PersistenceContext
     * Permet l'accès à la base de données
     */
    @PersistenceContext(unitName = "jakartamissionPU")
    private EntityManager em;

    /**
     * CREATE : Ajouter un nouvel utilisateur avec hachage du mot de passe
     *
     * @param username    Le nom d'utilisateur
     * @param email       L'email
     * @param password    Le mot de passe (sera haché)
     * @param description La description optionnelle
     * @return L'utilisateur créé avec son ID généré
     */
    public User creerUtilisateur(String username, String email, String password, String description) {
        // Normaliser l'email en minuscules
        String emailNormalized = email.toLowerCase().trim();

        // Hacher le mot de passe avant la sauvegarde
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User(username, emailNormalized, hashedPassword, description);
        em.persist(user);
        em.flush(); // Force la génération de l'ID
        return user;
    }

    /**
     * CREATE : Ajouter un nouvel utilisateur (surcharge avec objet User)
     *
     * @param user L'utilisateur à ajouter
     * @return L'utilisateur créé avec son ID généré
     */
    public User ajouterUtilisateurEntreprise(User user) {
        // Normaliser l'email
        user.setEmail(user.getEmail().toLowerCase().trim());

        // Hacher le mot de passe si nécessaire
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        em.persist(user);
        em.flush();
        return user;
    }

    /**
     * READ : Récupérer un utilisateur par son ID
     *
     * @param id L'ID de l'utilisateur
     * @return L'utilisateur trouvé, null sinon
     */
    public User obtenirUtilisateurParId(Long id) {
        return em.find(User.class, id);
    }

    /**
     * READ : Récupérer un utilisateur par son nom d'utilisateur
     *
     * @param username Le nom d'utilisateur
     * @return L'utilisateur trouvé, null sinon
     */
    public User obtenirUtilisateurParUsername(String username) {
        try {
            return em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username",
                    User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * READ : Récupérer un utilisateur par son email
     *
     * @param email L'email de l'utilisateur
     * @return L'utilisateur trouvé, null sinon
     */
    public User obtenirUtilisateurParEmail(String email) {
        try {
            System.out.println("[BEAN] obtenirUtilisateurParEmail() - Recherche: " + email);
            User result = em.createQuery(
                    "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)",
                    User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            System.out.println("[BEAN] User trouvé: " + result.getUsername());
            return result;
        } catch (Exception e) {
            System.out.println(
                    "[BEAN] User NOT found - Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * READ : Récupérer tous les utilisateurs
     *
     * @return Liste de tous les utilisateurs
     */
    public List<User> obtenirTousLesUtilisateurs() {
        return em.createQuery(
                "SELECT u FROM User u ORDER BY u.username",
                User.class)
                .getResultList();
    }

    /**
     * UPDATE : Modifier un utilisateur existant
     *
     * @param user L'utilisateur avec les modifications
     * @return L'utilisateur modifié
     */
    public User modifierUtilisateur(User user) {
        return em.merge(user);
    }

    /**
     * DELETE : Supprimer un utilisateur par son ID
     *
     * @param id L'ID de l'utilisateur à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerUtilisateur(Long id) {
        try {
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * DELETE : Supprimer un utilisateur par son nom d'utilisateur
     *
     * @param username Le nom d'utilisateur
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerUtilisateurParUsername(String username) {
        try {
            User user = obtenirUtilisateurParUsername(username);
            if (user != null) {
                em.remove(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Vérifier si un email existe déjà
     *
     * @param email L'email à vérifier
     * @return true si l'email existe, false sinon
     */
    public boolean emailExiste(String email) {
        return obtenirUtilisateurParEmail(email) != null;
    }

    /**
     * Vérifier si un nom d'utilisateur existe déjà
     *
     * @param username Le nom d'utilisateur à vérifier
     * @return true si le nom d'utilisateur existe, false sinon
     */
    public boolean usernameExiste(String username) {
        return obtenirUtilisateurParUsername(username) != null;
    }

    /**
     * Compter le nombre total d'utilisateurs
     *
     * @return Le nombre d'utilisateurs en base de données
     */
    public long compterUtilisateurs() {
        return em.createQuery(
                "SELECT COUNT(u) FROM User u",
                Long.class)
                .getSingleResult();
    }

    /**
     * Vérifier un mot de passe contre un mot de passe haché
     *
     * @param password       Le mot de passe en clair
     * @param hashedPassword Le mot de passe haché
     * @return true si le mot de passe est correct, false sinon
     */
    public boolean verifierMotDePasse(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * Ajouter un utilisateur avec vérification d'unicité
     *
     * @param username    Le nom d'utilisateur
     * @param email       L'email
     * @param password    Le mot de passe
     * @param description La description
     * @return L'utilisateur créé, ou null si l'utilisateur existe déjà
     */
    public User ajouterUtilisateurAvecVerification(String username, String email, String password, String description) {
        // Vérifier si l'utilisateur existe déjà
        if (usernameExiste(username) || emailExiste(email)) {
            return null; // Signale que l'utilisateur existe déjà
        }

        // Créer et sauvegarder l'utilisateur
        return creerUtilisateur(username, email, password, description);
    }

    /**
     * Authentifier un utilisateur avec son email et son mot de passe
     *
     * @param email    L'email de l'utilisateur
     * @param password Le mot de passe en clair
     * @return L'utilisateur authentifié, ou null si l'authentification échoue
     */
    public User authentifier(String email, String password) {
        System.out.println("[BEAN] authentifier() - Tentative d'authentification");

        // Récupérer l'utilisateur par son email
        User user = obtenirUtilisateurParEmail(email);
        
        // Vérifier que l'utilisateur existe et que le mot de passe est correct
        if (user != null) {
            boolean passwordMatch = verifierMotDePasse(password, user.getPassword());

            if (passwordMatch) {
                System.out.println("[BEAN] Authentification réussie");
                return user;
            }
        }

        System.out.println("[BEAN] Authentification échouée - identifiants invalides");
        return null;
    }

    /**
     * UPDATE : Mettre à jour le mot de passe d'un utilisateur
     *
     * @param userId    L'ID de l'utilisateur
     * @param newPassword Le nouveau mot de passe en clair
     * @return true si la mise à jour est réussie, false sinon
     */
    public boolean changerMotDePasse(Long userId, String newPassword) {
        try {
            User user = obtenirUtilisateurParId(userId);
            if (user == null) {
                return false;
            }
            
            // Hacher le nouveau mot de passe
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPassword(hashedPassword);
            
            em.merge(user);
            em.flush();
            
            System.out.println("[BEAN] Mot de passe mis à jour avec succès pour l'utilisateur ID: " + userId);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour du mot de passe: " + e.getMessage());
            return false;
        }
    }

    /**
     * UPDATE : Mettre à jour un utilisateur existant
     *
     * @param user L'utilisateur à mettre à jour
     * @return L'utilisateur mis à jour
     */
    public User mettreAJourUtilisateur(User user) {
        return em.merge(user);
    }
}
