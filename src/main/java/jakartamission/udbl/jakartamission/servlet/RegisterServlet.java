package jakartamission.udbl.jakartamission.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = { "/register" })
public class RegisterServlet extends HttpServlet {

    @Inject
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[RegisterServlet] POST request reçue");

        // Récupérer les paramètres du formulaire
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String description = request.getParameter("description");

        System.out.println("[RegisterServlet] Username: " + username);
        System.out.println("[RegisterServlet] Email: " + email);

        // Vérifier que les champs obligatoires ne sont pas vides
        if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
            System.out.println("[RegisterServlet] Champs obligatoires vides");
            response.sendRedirect("register.html?error=Veuillez remplir tous les champs obligatoires");
            return;
        }

        // Vérifier la longueur du nom d'utilisateur
        if (username.trim().length() < 3 || username.trim().length() > 50) {
            System.out.println("[RegisterServlet] Nom d'utilisateur invalide (longueur)");
            response.sendRedirect("register.html?error=Le nom d'utilisateur doit contenir entre 3 et 50 caractères");
            return;
        }

        // Vérifier le format email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            System.out.println("[RegisterServlet] Email invalide");
            response.sendRedirect("register.html?error=L'email n'est pas valide");
            return;
        }

        // Vérifier la force du mot de passe
        if (password.length() < 6) {
            System.out.println("[RegisterServlet] Mot de passe trop court");
            response.sendRedirect("register.html?error=Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            System.out.println("[RegisterServlet] Mot de passe ne respecte pas les critères");
            response.sendRedirect(
                    "register.html?error=Le mot de passe doit contenir au moins 1 majuscule et 1 chiffre");
            return;
        }

        // Vérifier que les mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            System.out.println("[RegisterServlet] Les mots de passe ne correspondent pas");
            response.sendRedirect("register.html?error=Les mots de passe ne correspondent pas");
            return;
        }

        try {
            // Vérifier si l'utilisateur existe déjà
            if (utilisateurEntrepriseBean.usernameExiste(username)) {
                System.out.println("[RegisterServlet] Nom d'utilisateur déjà existant");
                response.sendRedirect("register.html?error=Ce nom d'utilisateur existe déjà");
                return;
            }

            if (utilisateurEntrepriseBean.emailExiste(email.toLowerCase().trim())) {
                System.out.println("[RegisterServlet] Email déjà existant");
                response.sendRedirect("register.html?error=Cet email existe déjà");
                return;
            }

            // Créer l'utilisateur
            System.out.println("[RegisterServlet] Création de l'utilisateur...");
            utilisateurEntrepriseBean.creerUtilisateur(username, email.toLowerCase().trim(), password, description);
            System.out.println("[RegisterServlet] Utilisateur créé avec succès");

            // Rediriger vers la page de connexion avec message de succès
            response.sendRedirect("login.html?success=Compte créé avec succès ! Connectez-vous.");

        } catch (Exception e) {
            System.out.println("[RegisterServlet] Exception: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("register.html?error=Une erreur est survenue lors de la création du compte");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Rediriger les GET vers la page d'enregistrement
        response.sendRedirect("register.html");
    }
}
