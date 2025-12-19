package jakartamission.udbl.jakartamission.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.*;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import java.io.Serializable;

@Named("utilisateurBean")
@RequestScoped
public class UtilisateurBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit avoir entre 3 et 50 caractères")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$", message = "Le mot de passe doit contenir au moins une majuscule, un chiffre et un caractère spécial")
    private String password;

    @NotBlank(message = "Veuillez confirmer votre mot de passe")
    private String confirmPassword;

    private String description;

    // Constructeurs
    public UtilisateurBean() {
    }

    // Getters et Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Méthode pour ajouter un utilisateur
    public void ajouterUtilisateur() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Normaliser l'email pour la vérification
        String emailNormalized = email.trim().toLowerCase();

        // Vérifier si les mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Les mots de passe ne correspondent pas",
                    null));
            return;
        }

        // Vérifier si le nom d'utilisateur existe déjà
        if (utilisateurEntrepriseBean.usernameExiste(username)) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Ce nom d'utilisateur existe déjà. Veuillez en choisir un autre.",
                    null));
            return;
        }

        // Vérifier si l'email existe déjà (utiliser l'email normalisé)
        if (utilisateurEntrepriseBean.emailExiste(emailNormalized)) {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Cette adresse email existe déjà. Veuillez en utiliser une autre.",
                    null));
            return;
        }

        // Ajouter l'utilisateur avec mot de passe haché
        utilisateurEntrepriseBean.creerUtilisateur(username, emailNormalized, password, description);

        // Afficher le message de succès
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Utilisateur ajouté avec succès !",
                null));

        System.out.println("Utilisateur ajouté : " + username + " - " + emailNormalized);

        // Réinitialisation des champs
        username = "";
        email = "";
        password = "";
        confirmPassword = "";
        description = "";
    }
}
