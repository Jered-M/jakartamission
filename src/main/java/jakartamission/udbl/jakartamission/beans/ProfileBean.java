package jakartamission.udbl.jakartamission.beans;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakartamission.udbl.jakartamission.business.SessionManager;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import jakartamission.udbl.jakartamission.entities.User;
import org.mindrot.jbcrypt.BCrypt;
import java.io.Serializable;

/**
 * Bean pour gérer le profil utilisateur
 */
@Named(value = "profileBean")
@ViewScoped
public class ProfileBean implements Serializable {

    private String username;
    private String email;
    private String description;

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    private boolean showMessage = false;
    private String message = "";
    private boolean passwordSuccess = false;
    private String passwordMessage = "";

    @Inject
    private SessionManager sessionManager;

    @Inject
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    /**
     * Initialise le bean en chargeant les données de l'utilisateur authentifié
     */
    public void init() {
        String sessionEmail = sessionManager.getValueFromSession("email");
        if (sessionEmail != null) {
            User user = utilisateurEntrepriseBean.obtenirUtilisateurParEmail(sessionEmail);
            if (user != null) {
                this.username = user.getUsername();
                this.email = user.getEmail();
                this.description = user.getDescription();
            }
        }
    }

    /**
     * Met à jour le profil utilisateur
     * 
     * @return la page de navigation
     */
    public String updateProfile() {
        String sessionEmail = sessionManager.getValueFromSession("email");
        if (sessionEmail == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erreur",
                            "Vous n'êtes pas connecté"));
            return "index";
        }

        try {
            User user = utilisateurEntrepriseBean.obtenirUtilisateurParEmail(sessionEmail);
            if (user != null) {
                user.setDescription(this.description);
                utilisateurEntrepriseBean.modifierUtilisateur(user);

                showMessage = true;
                message = "✓ Profil mis à jour avec succès!";

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Succès",
                                "Votre profil a été mis à jour"));
            }
        } catch (Exception e) {
            showMessage = true;
            message = "✗ Erreur lors de la mise à jour du profil";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erreur",
                            "Une erreur s'est produite: " + e.getMessage()));
        }
        return null;
    }

    /**
     * Change le mot de passe de l'utilisateur
     * 
     * @return la page de navigation
     */
    public String changePassword() {
        String sessionEmail = sessionManager.getValueFromSession("email");
        if (sessionEmail == null) {
            passwordMessage = "✗ Vous n'êtes pas connecté";
            passwordSuccess = false;
            return "index";
        }

        // Vérifier que les mots de passe ne sont pas vides
        if (currentPassword == null || currentPassword.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
            passwordMessage = "✗ Tous les champs sont requis";
            passwordSuccess = false;
            return null;
        }

        // Vérifier que les nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            passwordMessage = "✗ Les mots de passe ne correspondent pas";
            passwordSuccess = false;
            return null;
        }

        // Vérifier que le nouveau mot de passe est différent de l'ancien
        if (currentPassword.equals(newPassword)) {
            passwordMessage = "✗ Le nouveau mot de passe doit être différent du mot de passe actuel";
            passwordSuccess = false;
            return null;
        }

        // Vérifier la longueur minimale
        if (newPassword.length() < 8) {
            passwordMessage = "✗ Le mot de passe doit contenir au moins 8 caractères";
            passwordSuccess = false;
            return null;
        }

        try {
            User user = utilisateurEntrepriseBean.obtenirUtilisateurParEmail(sessionEmail);
            if (user == null) {
                passwordMessage = "✗ Utilisateur non trouvé";
                passwordSuccess = false;
                return null;
            }

            // Vérifier le mot de passe actuel
            if (!utilisateurEntrepriseBean.verifierMotDePasse(currentPassword, user.getPassword())) {
                passwordMessage = "✗ Le mot de passe actuel est incorrect";
                passwordSuccess = false;
                currentPassword = "";
                return null;
            }

            // Hacher et mettre à jour le nouveau mot de passe
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPassword(hashedPassword);
            utilisateurEntrepriseBean.modifierUtilisateur(user);

            passwordMessage = "✓ Mot de passe changé avec succès!";
            passwordSuccess = true;

            // Réinitialiser les champs
            currentPassword = "";
            newPassword = "";
            confirmPassword = "";

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Succès",
                            "Votre mot de passe a été changé avec succès"));
        } catch (Exception e) {
            passwordMessage = "✗ Erreur lors du changement de mot de passe";
            passwordSuccess = false;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Erreur",
                            "Une erreur s'est produite: " + e.getMessage()));
        }
        return null;
    }

    // Getters et Setters

    public String getUsername() {
        if (username == null) {
            init();
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        if (email == null) {
            init();
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        if (description == null) {
            init();
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPasswordSuccess() {
        return passwordSuccess;
    }

    public void setPasswordSuccess(boolean passwordSuccess) {
        this.passwordSuccess = passwordSuccess;
    }

    public String getPasswordMessage() {
        return passwordMessage;
    }

    public void setPasswordMessage(String passwordMessage) {
        this.passwordMessage = passwordMessage;
    }
}
