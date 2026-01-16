package jakartamission.udbl.jakartamission.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet qui fournit les informations utilisateur actuelles en JSON
 */
@WebServlet(name = "UserInfoServlet", urlPatterns = { "/api/userinfo" })
public class UserInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);

            if (session != null) {
                String username = (String) session.getAttribute("user");
                String email = (String) session.getAttribute("email");
                Long userId = (Long) session.getAttribute("userId");

                if (username != null && email != null) {
                    // Utilisateur authentifié
                    out.print("{\"success\": true, \"username\": \"" + escapeJson(username) + "\", \"email\": \""
                            + escapeJson(email) + "\", \"userId\": " + userId + "}");
                    System.out.println("[UserInfoServlet] Infos retournées pour: " + username);
                } else {
                    // Session valide mais pas d'info utilisateur
                    out.print("{\"success\": false, \"message\": \"Informations utilisateur non trouvées\"}");
                    System.out.println("[UserInfoServlet] Session valide mais pas d'infos");
                }
            } else {
                // Pas de session
                out.print("{\"success\": false, \"message\": \"Session non valide\"}");
                System.out.println("[UserInfoServlet] Pas de session");
            }
        } catch (Exception e) {
            System.out.println("[UserInfoServlet] Exception: " + e.getMessage());
            out.print("{\"success\": false, \"message\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Échappe les caractères spéciaux pour JSON
     */
    private String escapeJson(String input) {
        if (input == null)
            return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
