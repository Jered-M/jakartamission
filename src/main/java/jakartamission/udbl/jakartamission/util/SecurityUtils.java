package jakartamission.udbl.jakartamission.util;

/**
 * Utilitaires de sécurité pour l'échappement et la validation des données
 * Prévient les attaques XSS et injection JSON
 */
public class SecurityUtils {

    /**
     * Échappe les caractères spéciaux pour JSON
     * Prévient les attaques JSON injection
     *
     * @param str La chaîne à échapper
     * @return La chaîne échappée pour JSON
     */
    public static String escapeJson(String str) {
        if (str == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 32 || c >= 127) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * Échappe les caractères spéciaux pour HTML/XML
     * Prévient les attaques XSS
     *
     * @param str La chaîne à échapper
     * @return La chaîne échappée pour HTML
     */
    public static String escapeHtml(String str) {
        if (str == null) {
            return null;
        }
        
        return str
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }

    /**
     * Valide une adresse email basique
     *
     * @param email L'email à valider
     * @return true si l'email semble valide, false sinon
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Pattern simple pour validation d'email
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Valide la force du mot de passe
     *
     * @param password Le mot de passe à valider
     * @return true si le mot de passe respecte les critères, false sinon
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Vérifie la présence d'au moins une majuscule, une minuscule et un chiffre
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        return hasUpperCase && hasLowerCase && hasDigit;
    }

    /**
     * Nettoie une chaîne en supprimant les caractères dangereux
     *
     * @param str La chaîne à nettoyer
     * @return La chaîne nettoyée
     */
    public static String sanitizeInput(String str) {
        if (str == null) {
            return null;
        }
        
        // Supprime les caractères de contrôle et les null bytes
        return str
            .replaceAll("[\\x00-\\x1F\\x7F]", "")
            .trim();
    }
}
