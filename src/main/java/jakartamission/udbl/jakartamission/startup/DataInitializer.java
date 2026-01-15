package jakartamission.udbl.jakartamission.startup;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import jakartamission.udbl.jakartamission.entities.User;
import java.util.List;

/**
 * Bean de démarrage pour initialiser la base de données
 * Crée un utilisateur par défaut au lancement de l'application
 */
@Singleton
@Startup
public class DataInitializer {

    @PersistenceContext(unitName = "jakartamissionPU")
    private EntityManager em;

    @Inject
    private UtilisateurEntrepriseBean utilisateurBean;

    @PostConstruct
    public void init() {
        System.out.println("[DataInitializer] Vérification et initialisation de la base de données...");

        try {
            // Vérifier s'il y a au moins un utilisateur
            List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();

            if (users.isEmpty()) {
                System.out
                        .println("[DataInitializer] Aucun utilisateur trouvé. Création de l'utilisateur par défaut...");

                // Créer l'utilisateur admin
                User admin = utilisateurBean.creerUtilisateur(
                        "admin",
                        "admin@example.com",
                        "admin",
                        "Admin User");

                System.out.println("[DataInitializer] ✅ Utilisateur admin créé avec succès !");
                System.out.println("[DataInitializer] Email: admin@example.com");
                System.out.println("[DataInitializer] Mot de passe: admin");
            } else {
                System.out.println("[DataInitializer] ✅ Base de données déjà initialisée avec " + users.size()
                        + " utilisateur(s)");
                for (User u : users) {
                    System.out.println("[DataInitializer]   - " + u.getUsername() + " (" + u.getEmail() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("[DataInitializer] ❌ Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
