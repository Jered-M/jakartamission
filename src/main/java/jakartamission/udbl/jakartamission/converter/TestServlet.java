package jakartamission.udbl.jakartamission.converter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @PersistenceContext(unitName = "jakartamissionPU")
    private EntityManager em;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<h1>Diagnostic Jakarta EE</h1>");
        out.println("<p>✅ TestServlet fonctionne</p>");
        
        // Vérifier EntityManager
        try {
            if (em != null) {
                out.println("<p style='color: green;'>✅ EntityManager injecté : " + em.getClass().getName() + "</p>");
                
                // Essayer une requête simple
                try {
                    Long count = (Long) em.createQuery("SELECT COUNT(u) FROM User u").getSingleResult();
                    out.println("<p style='color: green;'>✅ Base de données accessible - Users count: " + count + "</p>");
                } catch (Exception e) {
                    out.println("<p style='color: red;'>❌ Erreur requête DB: " + e.getMessage() + "</p>");
                    e.printStackTrace(out);
                }
            } else {
                out.println("<p style='color: red;'>❌ EntityManager NOT injected (NULL)</p>");
            }
        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Exception: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }
        
        System.out.println("✅ TestServlet appelé");
    }
}
