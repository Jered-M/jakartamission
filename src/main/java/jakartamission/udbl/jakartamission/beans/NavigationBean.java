package jakartamission.udbl.jakartamission.beans;

import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.RequestScoped;

/**
 * Bean pour gérer la navigation entre les pages JSF
 */
@ManagedBean
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
