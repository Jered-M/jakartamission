package jakartamission.udbl.jakartamission.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakartamission.udbl.jakartamission.business.LieuEntrepriseBean;
import jakartamission.udbl.jakartamission.entities.Lieu;
import java.io.Serializable;
import java.util.List;

/**
 * Bean managé pour gérer les lieux dans l'interface utilisateur
 */
@Named(value = "lieuBean")
@RequestScoped
public class LieuBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nom;
    private String description;
    private double longitude;
    private double latitude;
    private int idLieuSelectionnee;

    @Inject
    private LieuEntrepriseBean lieuEntrepriseBean;

    public LieuBean() {
    }

    public String getNom() { 
        return nom; 
    }

    public void setNom(String nom) { 
        this.nom = nom; 
    }

    public String getDescription() { 
        return description; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    public double getLongitude() { 
        return longitude; 
    }

    public void setLongitude(double longitude) { 
        this.longitude = longitude; 
    }

    public double getLatitude() { 
        return latitude; 
    }

    public void setLatitude(double latitude) { 
        this.latitude = latitude; 
    }

    public int getIdLieuSelectionnee() {
        return idLieuSelectionnee;
    }

    public void setIdLieuSelectionnee(int idLieuSelectionnee) {
        this.idLieuSelectionnee = idLieuSelectionnee;
    }

    public List<Lieu> getLieux() { 
        return lieuEntrepriseBean.listerTousLesLieux(); 
    }

    public String ajouterLieu() {
        if (nom != null && !nom.isEmpty()) {
            lieuEntrepriseBean.ajouterLieuEntreprise(nom, description, latitude, longitude);
            // Réinitialiser les champs
            nom = "";
            description = "";
            latitude = 0;
            longitude = 0;
        }
        return null; // Reste sur la même page
    }

    public String modifierLieu() {
        if (nom != null && !nom.isEmpty() && idLieuSelectionnee > 0) {
            lieuEntrepriseBean.modifierLieu(idLieuSelectionnee, nom, description, latitude, longitude);
            // Réinitialiser
            nom = "";
            description = "";
            latitude = 0;
            longitude = 0;
            idLieuSelectionnee = 0;
        }
        return null;
    }

    public String supprimerLieu(int id) {
        lieuEntrepriseBean.supprimerLieu(id);
        return null;
    }

    public String chargerLieuPourEdition(int id) {
        Lieu lieu = lieuEntrepriseBean.trouverLieuParId(id);
        if (lieu != null) {
            this.idLieuSelectionnee = lieu.getId();
            this.nom = lieu.getNom();
            this.description = lieu.getDescription();
            this.latitude = lieu.getLatitude();
            this.longitude = lieu.getLongitude();
        }
        return null;
    }
}
