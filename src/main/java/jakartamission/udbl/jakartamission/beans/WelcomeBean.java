package jakartamission.udbl.jakartamission.beans;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakartamission.udbl.jakartamission.business.SessionManager;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import jakartamission.udbl.jakartamission.entities.User;
import java.io.Serializable;

@Named(value = "welcomeBean")
@SessionScoped
public class WelcomeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private String password;

    @Inject
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    @Inject
    private SessionManager sessionManager;

    /**
     * Constructeur par défaut
     */
    public WelcomeBean() {
        System.out.println("[WelcomeBean] Constructeur appelé");
    }

    /**
     * Accesseur pour email
     * 
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Mutateur pour email
     * 
     * @param email l'email à définir
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Accesseur pour password
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Mutateur pour password
     * 
     * @param password le mot de passe à définir
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Méthode d'authentification
     * Vérifie l'email et le mot de passe de l'utilisateur
     * 
     * @return "home" si l'authentification réussit, "index" sinon
     */
    public String sAuthentifier() {
        System.out.println("\n========== DEBUG AUTHENTIFICATION ==========");
        System.out.println("[DEBUG] sAuthentifier() appelée");
        System.out.println("[DEBUG] Email reçu: '" + email + "'");
        System.out.println("[DEBUG] Password reçu: " + (password != null ? "***" : "null"));
        System.out.println(
                "[DEBUG] utilisateurEntrepriseBean: " + (utilisateurEntrepriseBean != null ? "INJECTE" : "NULL!!!"));
        System.out.println("[DEBUG] sessionManager: " + (sessionManager != null ? "INJECTE" : "NULL!!!"));

        // Vérifier que les champs ne sont pas vides
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            System.out.println("[ERROR] Champs vides !");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erreur",
                            "Veuillez remplir tous les champs"));
            System.out.println("========== FIN DEBUG ==========\n");
            return "index?faces-redirect=true";
        }

        try {
            // Appeler la méthode d'authentification du bean métier
            System.out.println("[DEBUG] Appel authentifier() avec email: " + email);
            User user = utilisateurEntrepriseBean.authentifier(email, password);
            System.out.println("[DEBUG] Résultat authentifier: "
                    + (user != null ? "SUCCES - User: " + user.getUsername() : "ECHEC - User null"));

            if (user != null) {
                // Authentification réussie
                // Créer une session pour l'utilisateur via le SessionManager
                System.out.println("[SUCCESS] Authentification réussie pour: " + user.getUsername());
                sessionManager.createSession("user", user.getUsername());
                sessionManager.createSession("userId", String.valueOf(user.getId()));
                sessionManager.createSession("email", user.getEmail());

                System.out.println("[DEBUG] Session créée avec:");
                System.out.println("  - user: " + user.getUsername());
                System.out.println("  - userId: " + user.getId());
                System.out.println("  - email: " + user.getEmail());

                // Réinitialiser les champs
                email = null;
                password = null;

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Succès",
                                "Connecté avec succès"));

                System.out.println("[DEBUG] Redirection vers home.xhtml");
                System.out.println("========== FIN DEBUG ==========\n");
                return "home?faces-redirect=true";
            } else {
                // Authentification échouée
                System.out.println("[ERROR] Authentification échouée - Email ou mot de passe incorrect");
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Erreur",
                                "Email ou mot de passe incorrect"));
                password = null; // Réinitialiser le mot de passe
                System.out.println("========== FIN DEBUG ==========\n");
                return "index?faces-redirect=true";
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Exception: " + e.getMessage());
            System.out.println("[ERROR] Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erreur",
                            "Une erreur est survenue: " + e.getMessage()));
            System.out.println("========== FIN DEBUG ==========\n");
            return "index?faces-redirect=true";
        }
    }
}
