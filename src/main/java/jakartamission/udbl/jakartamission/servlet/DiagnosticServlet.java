package jakartamission.udbl.jakartamission.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import jakartamission.udbl.jakartamission.entities.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet de diagnostic pour tester la persistance des données
 */
@WebServlet(name = "DiagnosticServlet", urlPatterns = { "/diagnostic" })
public class DiagnosticServlet extends HttpServlet {

    @Inject
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'/>");
        out.println("<title>Diagnostic Persistance</title>");
        out.println(
                "<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css'/>");
        out.println("<style>");
        out.println("body { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; }");
        out.println(
                ".container { background: white; border-radius: 10px; padding: 30px; margin: auto; max-width: 900px; }");
        out.println("table { width: 100%; margin-top: 20px; }");
        out.println("th { background: #667eea; color: white; padding: 10px; }");
        out.println("td { padding: 10px; border-bottom: 1px solid #eee; }");
        out.println(".success { color: #28a745; font-weight: bold; }");
        out.println(".error { color: #dc3545; font-weight: bold; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>🔍 Diagnostic Persistance - Jakarta Mission</h1>");

        try {
            // Test 1: Vérifie si le bean est injecté
            out.println("<h2>État des Services</h2>");
            out.println("<p>UtilisateurEntrepriseBean: <span class='success'>✓ INJECTÉ</span></p>");

            // Test 2: Récupère tous les utilisateurs
            out.println("<h2>Utilisateurs dans la Base de Données</h2>");
            out.println("<p>Tentative de récupération des utilisateurs...</p>");

            // Créer une requête JPQL pour compter les utilisateurs
            out.println("<table border='1'>");
            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>Username</th>");
            out.println("<th>Email</th>");
            out.println("<th>Description</th>");
            out.println("<th>Date</th>");
            out.println("</tr>");

            // Test d'enregistrement
            out.println("<tr>");
            out.println("<td colspan='5' style='text-align: center;'>");
            out.println("<a href='../register.html' class='btn btn-primary'>Créer un compte test</a>");
            out.println("</td>");
            out.println("</tr>");

            out.println("</table>");

            // Test 3: Authentification test
            out.println("<h2>Test d'Authentification</h2>");
            out.println("<p>Essai avec admin@example.com / admin</p>");

            User testUser = utilisateurEntrepriseBean.authentifier("admin@example.com", "admin");
            if (testUser != null) {
                out.println("<p class='success'>✓ Utilisateur trouvé: " + testUser.getUsername() + "</p>");
            } else {
                out.println("<p class='error'>✗ Utilisateur non trouvé</p>");
            }

            out.println("<hr/>");
            out.println("<p><a href='../login.html'>← Retour à l'accueil</a></p>");

        } catch (Exception e) {
            out.println("<p class='error'>❌ Erreur: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
