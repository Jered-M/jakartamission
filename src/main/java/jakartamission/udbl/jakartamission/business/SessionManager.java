package jakartamission.udbl.jakartamission.business;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * Gestionnaire de session pour gérer les informations de l'utilisateur
 * authentifié
 * Permet de créer une session après authentification et de l'invalider lors de
 * la déconnexion
 */
@Named(value = "sessionManager")
@ApplicationScoped
public class SessionManager implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Crée une paire clé-valeur dans la session
     *
     * @param key   La clé de la session
     * @param value La valeur à stocker
     */
    public void createSession(String key, String value) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        session.setAttribute(key, value);
    }

    /**
     * Récupère une valeur de la session
     *
     * @param key La clé de la session
     * @return La valeur stockée, null si la clé n'existe pas
     */
    public String getValueFromSession(String key) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            return (String) session.getAttribute(key);
        }
        return null;
    }

    /**
     * Invalide la session et déconnecte l'utilisateur
     */
    public void invalidateSession() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
