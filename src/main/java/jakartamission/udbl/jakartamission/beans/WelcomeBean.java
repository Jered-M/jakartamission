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
        // Vérifier que les champs ne sont pas vides
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erreur",
                            "Veuillez remplir tous les champs"));
            return "index?faces-redirect=true";
        }

        try {
            // Appeler la méthode d'authentification du bean métier
            User user = utilisateurEntrepriseBean.authentifier(email, password);

            if (user != null) {
                // Authentification réussie
                // Créer une session pour l'utilisateur via le SessionManager
                sessionManager.createSession("user", user.getUsername());
                sessionManager.createSession("userId", String.valueOf(user.getId()));
                sessionManager.createSession("email", user.getEmail());

                // Réinitialiser les champs
                email = null;
                password = null;

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Succès",
                                "Connecté avec succès"));

                return "home?faces-redirect=true";
            } else {
                // Authentification échouée
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Erreur",
                                "Email ou mot de passe incorrect"));
                password = null; // Réinitialiser le mot de passe
                return "index?faces-redirect=true";
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de l'authentification");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erreur",
                            "Une erreur est survenue lors de l'authentification. Veuillez réessayer."));
            return "index?faces-redirect=true";
        }
    }
}
