package jakartamission.udbl.jakartamission.beans;

import jakarta.enterprise.context.SessionScoped;
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
@SessionScoped
public class LieuBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nom;
    private String description;
    private double longitude;
    private double latitude;
    private int idLieuSelectionnee;
    private String message;
    private boolean showMessage;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public List<Lieu> getLieux() {
        return lieuEntrepriseBean.listerTousLesLieux();
    }

    public void ajouterLieu() {
        if (nom != null && !nom.isEmpty()) {
            try {
                lieuEntrepriseBean.ajouterLieuEntreprise(nom, description, latitude, longitude);
                System.out.println("✅ Lieu ajouté: " + nom + " (Lat: " + latitude + ", Long: " + longitude + ")");
                // Réinitialiser complètement les champs
                this.nom = null;
                this.description = null;
                this.latitude = 0.0;
                this.longitude = 0.0;
            } catch (Exception e) {
                message = "❌ Erreur lors de l'ajout: " + e.getMessage();
                showMessage = true;
                System.out.println("❌ Erreur: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public String modifierLieu() {
        if (nom != null && !nom.isEmpty() && idLieuSelectionnee > 0) {
            try {
                String nomLieu = this.nom;
                lieuEntrepriseBean.modifierLieu(idLieuSelectionnee, nom, description, latitude, longitude);
                System.out.println("✏️ Lieu modifié: " + nomLieu);
                message = "✅ Le lieu '" + nomLieu + "' a été modifié avec succès!";
                showMessage = true;
                // Réinitialiser après affichage du message
                this.nom = null;
                this.description = null;
                this.latitude = 0.0;
                this.longitude = 0.0;
                this.idLieuSelectionnee = 0;
                return "lieuSuccess"; // Redirection vers lieu.xhtml avec message
            } catch (Exception e) {
                message = "❌ Erreur lors de la modification: " + e.getMessage();
                showMessage = true;
                System.out.println("❌ Erreur: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public String chargerLieuPourSuppression(int id) {
        Lieu lieu = lieuEntrepriseBean.trouverLieuParId(id);
        if (lieu != null) {
            this.idLieuSelectionnee = lieu.getId();
            this.nom = lieu.getNom();
            this.description = lieu.getDescription();
            this.latitude = lieu.getLatitude();
            this.longitude = lieu.getLongitude();
            System.out.println("🗑️ Lieu chargé pour suppression: " + this.nom);
            return "supprimer"; // Redirection vers supprimer.xhtml
        }
        return null;
    }

    public String confirmerSuppression() {
        if (idLieuSelectionnee > 0) {
            try {
                String nomLieu = this.nom;
                lieuEntrepriseBean.supprimerLieu(idLieuSelectionnee);
                System.out.println("✓ Lieu supprimé: " + nomLieu);
                message = "✅ Le lieu '" + nomLieu + "' a été supprimé avec succès!";
                showMessage = true;
                // Réinitialiser après affichage du message
                this.nom = null;
                this.description = null;
                this.latitude = 0.0;
                this.longitude = 0.0;
                this.idLieuSelectionnee = 0;
                return "lieuSuccess"; // Redirection vers lieu.xhtml avec message
            } catch (Exception e) {
                message = "❌ Erreur lors de la suppression: " + e.getMessage();
                showMessage = true;
                System.out.println("❌ Erreur lors de la suppression: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public String supprimerLieu(int id) {
        return chargerLieuPourSuppression(id);
    }

    public String chargerLieuPourEdition(int id) {
        Lieu lieu = lieuEntrepriseBean.trouverLieuParId(id);
        if (lieu != null) {
            this.idLieuSelectionnee = lieu.getId();
            this.nom = lieu.getNom();
            this.description = lieu.getDescription();
            this.latitude = lieu.getLatitude();
            this.longitude = lieu.getLongitude();
            showMessage = false;
            System.out.println("📝 Lieu chargé pour édition: " + this.nom);
            return "modifier"; // Redirection vers modifier.xhtml
        }
        return null;
    }
}
