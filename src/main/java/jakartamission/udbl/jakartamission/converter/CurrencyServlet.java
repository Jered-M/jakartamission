package jakartamission.udbl.jakartamission.converter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet pour la conversion USD → IDR
 */
@WebServlet("/convert")
public class CurrencyServlet extends HttpServlet {

    private static final double TAUX_USD_IDR = 15800.0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            double usd = Double.parseDouble(request.getParameter("usd"));
            double idr = usd * TAUX_USD_IDR;

            request.setAttribute("usd", String.format("%.2f", usd));
            request.setAttribute("idr", String.format("%.0f", idr));
            request.setAttribute("success", true);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Veuillez entrer un montant valide");
        }

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
