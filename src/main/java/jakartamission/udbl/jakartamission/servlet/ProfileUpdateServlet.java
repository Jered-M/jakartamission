package jakartamission.udbl.jakartamission.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakartamission.udbl.jakartamission.util.SecurityUtils;
import jakartamission.udbl.jakartamission.business.UtilisateurEntrepriseBean;
import jakartamission.udbl.jakartamission.entities.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.ejb.EJB;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Servlet pour la mise à jour du profil utilisateur
 * Gère les mises à jour sécurisées de description et de mot de passe
 */
@WebServlet("/api/profile/update")
public class ProfileUpdateServlet extends HttpServlet {

    @EJB
    private UtilisateurEntrepriseBean utilisateurEntrepriseBean;

    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Configuration des en-têtes de sécurité
        response.setHeader("Content-Type", "application/json");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");

        HttpSession session = request.getSession(false);
        
        // Vérifier que l'utilisateur est authentifié
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("success", false);
            errorResponse.addProperty("message", "Non authentifié");
            response.getWriter().write(errorResponse.toString());
            return;
        }

        try {
            // Lire le corps de la requête
            BufferedReader reader = request.getReader();
            JsonObject requestBody = gson.fromJson(reader, JsonObject.class);
            
            String username = (String) session.getAttribute("user");
            String updateType = requestBody.has("type") ? requestBody.get("type").getAsString() : "";
            
            JsonObject responseBody = new JsonObject();
            
            switch (updateType) {
                case "description":
                    handleDescriptionUpdate(username, requestBody, responseBody);
                    break;
                case "password":
                    handlePasswordUpdate(username, requestBody, responseBody);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseBody.addProperty("success", false);
                    responseBody.addProperty("message", "Type de mise à jour invalide");
            }
            
            response.getWriter().write(responseBody.toString());
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour du profil: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("success", false);
            errorResponse.addProperty("message", "Erreur serveur");
            response.getWriter().write(errorResponse.toString());
        }
    }

    private void handleDescriptionUpdate(String username, JsonObject request, JsonObject response) {
        String description = request.has("description") ? request.get("description").getAsString() : "";
        
        // Validation et nettoyage
        description = SecurityUtils.sanitizeInput(description);
        
        if (description == null || description.isEmpty()) {
            response.addProperty("success", false);
            response.addProperty("message", "La description ne peut pas être vide");
            return;
        }
        
        if (description.length() > 500) {
            response.addProperty("success", false);
            response.addProperty("message", "La description dépasse 500 caractères");
            return;
        }
        
        User user = utilisateurEntrepriseBean.obtenirUtilisateurParUsername(username);
        
        if (user == null) {
            response.addProperty("success", false);
            response.addProperty("message", "Utilisateur introuvable");
            return;
        }
        
        user.setDescription(description);
        utilisateurEntrepriseBean.mettreAJourUtilisateur(user);
        
        response.addProperty("success", true);
        response.addProperty("message", "Description mise à jour avec succès");
    }

    private void handlePasswordUpdate(String username, JsonObject request, JsonObject response) {
        String currentPassword = request.has("currentPassword") ? request.get("currentPassword").getAsString() : "";
        String newPassword = request.has("newPassword") ? request.get("newPassword").getAsString() : "";
        String confirmPassword = request.has("confirmPassword") ? request.get("confirmPassword").getAsString() : "";
        
        // Validation des champs
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            response.addProperty("success", false);
            response.addProperty("message", "Tous les champs sont requis");
            return;
        }
        
        // Vérifier que les nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            response.addProperty("success", false);
            response.addProperty("message", "Les nouveaux mots de passe ne correspondent pas");
            return;
        }
        
        // Vérifier la force du nouveau mot de passe
        if (!SecurityUtils.isValidPassword(newPassword)) {
            response.addProperty("success", false);
            response.addProperty("message", "Le mot de passe doit contenir au moins 8 caractères avec majuscules, minuscules et chiffres");
            return;
        }
        
        User user = utilisateurEntrepriseBean.obtenirUtilisateurParUsername(username);
        
        if (user == null) {
            response.addProperty("success", false);
            response.addProperty("message", "Utilisateur introuvable");
            return;
        }
        
        // Vérifier le mot de passe actuel
        if (!utilisateurEntrepriseBean.verifierMotDePasse(currentPassword, user.getPassword())) {
            response.addProperty("success", false);
            response.addProperty("message", "Mot de passe actuel incorrect");
            return;
        }
        
        // Vérifier que le nouveau mot de passe est différent de l'ancien
        if (currentPassword.equals(newPassword)) {
            response.addProperty("success", false);
            response.addProperty("message", "Le nouveau mot de passe doit être différent du mot de passe actuel");
            return;
        }
        
        // Mettre à jour le mot de passe
        utilisateurEntrepriseBean.changerMotDePasse(user.getId(), newPassword);
        
        response.addProperty("success", true);
        response.addProperty("message", "Mot de passe mis à jour avec succès");
    }
}
