package jakartamission.udbl.jakartamission.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Bean pour gérer la navigation entre les pages JSF
 */
@Named("navigationController")
@RequestScoped
public class NavigationBean {

    public String voirApropos() {
        return "apropos";
    }

    public String ajouterLieu() {
        return "lieu";
    }

    public String allerAccueil() {
        return "home";
    }

    public String allerIndex() {
        return "index";
    }
}
