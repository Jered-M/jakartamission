package jakartamission.udbl.jakartamission.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import jakartamission.udbl.jakartamission.entities.User;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    @Inject
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[LoginServlet] POST request reçue");

        // Récupérer les paramètres du formulaire
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println("[LoginServlet] Email: " + email);
        System.out.println("[LoginServlet] Password: " + (password != null ? "***" : "null"));

        // Vérifier que les champs ne sont pas vides
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            System.out.println("[LoginServlet] Champs vides, redirection vers login.html");
            response.sendRedirect("login.html?error=Veuillez remplir tous les champs");
            return;
        }

        try {
            // Authentifier l'utilisateur
            User user = utilisateurEntrepriseBean.authentifier(email, password);
            System.out.println("[LoginServlet] Résultat authentification: " + (user != null ? "SUCCESS" : "FAILURE"));

            if (user != null) {
                // Authentification réussie
                System.out.println("[LoginServlet] Utilisateur authentifié: " + user.getUsername());

                // Créer une session
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user.getUsername());
                session.setAttribute("userId", user.getId());
                session.setAttribute("email", user.getEmail());

                System.out.println("[LoginServlet] Session créée, redirection vers home.xhtml");

                // Rediriger vers home
                response.sendRedirect(request.getContextPath() + "/home.xhtml");
            } else {
                // Authentification échouée
                System.out.println("[LoginServlet] Email ou mot de passe incorrect");
                response.sendRedirect("login.html?error=Email ou mot de passe incorrect");
            }
        } catch (Exception e) {
            System.out.println("[LoginServlet] Exception: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("login.html?error=Une erreur est survenue");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Rediriger les GET vers la page de login
        response.sendRedirect("login.html");
    }
}
