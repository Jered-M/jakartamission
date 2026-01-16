package jakartamission.udbl.jakartamission.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import java.io.Serializable;

@Named("utilisateurBean")
@RequestScoped
public class UtilisateurBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String description;

    // Constructeurs
    public UtilisateurBean() {
        System.out.println("[UtilisateurBean] Constructeur appelé");
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
    public String ajouterUtilisateur() {
        System.out.println("\n========== CRÉATION COMPTE ==========");
        System.out.println("[DEBUG] ajouterUtilisateur() appelée");
        System.out.println("[DEBUG] Username: '" + username + "'");
        System.out.println("[DEBUG] Email: '" + email + "'");

        FacesContext context = FacesContext.getCurrentInstance();

        // Normaliser l'email pour la vérification
        String emailNormalized = email != null ? email.trim().toLowerCase() : "";

        // Vérifier si les mots de passe correspondent
        if (password == null || !password.equals(confirmPassword)) {
            System.out.println("[ERROR] Les mots de passe ne correspondent pas");
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Les mots de passe ne correspondent pas",
                    null));
            System.out.println("========== FIN CRÉATION COMPTE ==========\n");
            return null;
        }

        // Vérifier si l'utilisateur existe déjà
        if (utilisateurEntrepriseBean != null && utilisateurEntrepriseBean.usernameExiste(username)) {
            System.out.println("[ERROR] Nom d'utilisateur déjà existant");
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Ce nom d'utilisateur existe déjà. Veuillez en choisir un autre.",
                    null));
            System.out.println("========== FIN CRÉATION COMPTE ==========\n");
            return null;
        }

        // Vérifier si l'email existe déjà (utiliser l'email normalisé)
        if (utilisateurEntrepriseBean != null && utilisateurEntrepriseBean.emailExiste(emailNormalized)) {
            System.out.println("[ERROR] Email déjà existant");
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Cette adresse email existe déjà. Veuillez en utiliser une autre.",
                    null));
            System.out.println("========== FIN CRÉATION COMPTE ==========\n");
            return null;
        }

        try {
            // Ajouter l'utilisateur avec mot de passe haché
            if (utilisateurEntrepriseBean != null) {
                utilisateurEntrepriseBean.creerUtilisateur(username, emailNormalized, password, description);
                System.out.println("[SUCCESS] Utilisateur créé avec succès");

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

                System.out.println("[DEBUG] Redirection vers home.xhtml");
                System.out.println("========== FIN CRÉATION COMPTE ==========\n");
                return "home";
            } else {
                System.out.println("[ERROR] utilisateurEntrepriseBean est NULL");
                context.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Erreur: Bean métier non disponible",
                        null));
                return null;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Exception: " + e.getMessage());
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Erreur: " + e.getMessage(),
                    null));
            System.out.println("========== FIN CRÉATION COMPTE ==========\n");
            return null;
        }
    }
}
