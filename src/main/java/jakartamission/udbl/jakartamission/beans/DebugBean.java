package jakartamission.udbl.jakartamission.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import jakartamission.udbl.jakartamission.entities.User;
import java.io.Serializable;
import java.util.List;

@Named(value = "debugBean")
@RequestScoped
public class DebugBean implements Serializable {

    @Inject
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    public DebugBean() {
        System.out.println("[DebugBean] Constructeur appelé");
    }

    public List<User> getAllUsers() {
        System.out.println("[DebugBean] getAllUsers() appelée");
        try {
            List<User> users = utilisateurEntrepriseBean.obtenirTousLesUtilisateurs();
            System.out.println("[DebugBean] Nombre d'utilisateurs: " + users.size());
            for (User u : users) {
                System.out.println("[DebugBean]   - " + u.getUsername() + " (" + u.getEmail() + ")");
            }
            return users;
        } catch (Exception e) {
            System.out.println("[DebugBean] ERREUR: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
