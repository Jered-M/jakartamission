package jakartamission.udbl.jakartamission.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 * Bean pour gérer la navigation entre les pages JSF
 */
@Named("navigationController")
@RequestScoped
public class NavigationBean {

    public String voirApropos() {
        return "redirect:/pages/a_propos.xhtml";
    }

    public String ajouterLieu() {
        return "redirect:/pages/lieu.xhtml";
    }

    public String allerAccueil() {
        return "home";
    }

    public String allerIndex() {
        return "index";
    }

    public String allerLieux() {
        return "redirect:/pages/lieu.xhtml";
    }

    public String allerVisiter() {
        return "redirect:/pages/visiter.xhtml";
    }

    public String allerModifier() {
        return "redirect:/pages/modifier.xhtml";
    }

    public String allerSupprimer() {
        return "redirect:/pages/supprimer.xhtml";
    }

    public String visiter() {
        return "redirect:/pages/visiter.xhtml";
    }

    /**
     * Vérifie si l'utilisateur est authentifié
     * 
     * @return true si authentifié
     */
    public boolean isAuthenticated() {
        Object email = FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("email");
        return email != null;
    }
}
