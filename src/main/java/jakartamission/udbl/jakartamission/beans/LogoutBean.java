package jakartamission.udbl.jakartamission.beans;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakartamission.udbl.jakartamission.business.SessionManager;
import java.io.Serializable;

/**
 * Bean pour gérer la déconnexion de l'utilisateur
 */
@Named(value = "logoutBean")
@ViewScoped
public class LogoutBean implements Serializable {

    @Inject
    private SessionManager sessionManager;

    /**
     * Déconnecte l'utilisateur en invalidant sa session
     *
     * @return "index" pour rediriger vers la page de connexion
     */
    public String logout() {
        // Invalider la session
        sessionManager.invalidateSession();

        // Afficher un message de confirmation
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Déconnexion réussie",
                        "Vous avez été déconnecté avec succès"));

        // Rediriger vers la page de connexion
        return "index?faces-redirect=true";
    }

    /**
     * Récupère le nom d'utilisateur authentifié
     *
     * @return Le nom d'utilisateur, null si aucun utilisateur authentifié
     */
    public String getAuthenticatedUsername() {
        return sessionManager.getValueFromSession("authenticatedUser");
    }

    /**
     * Récupère l'ID de l'utilisateur authentifié
     *
     * @return L'ID de l'utilisateur, null si aucun utilisateur authentifié
     */
    public String getAuthenticatedUserId() {
        return sessionManager.getValueFromSession("userId");
    }

    /**
     * Vérifie si un utilisateur est authentifié
     *
     * @return true si un utilisateur est authentifié, false sinon
     */
    public boolean isUserAuthenticated() {
        return sessionManager.getValueFromSession("authenticatedUser") != null;
    }
}
