package jakartamission.udbl.jakartamission.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.json.Json;
import jakarta.json.JsonObject;

/**
 * REST API pour la conversion de monnaie (Dollar USD vers Roupie Indonésienne)
 * 
 * @author Juneau
 */
@Path("currency")
public class CurrencyConverterResource {

    // Taux de change USD vers IDR (à jour au 18 décembre 2025)
    private static final double USD_TO_IDR_RATE = 16250.00;

    /**
     * Convertit un montant en dollars US vers roupies indonésiennes
     * 
     * @param usdAmount Le montant en dollars US
     * @return JSON avec le montant converti et le taux de change
     */
    @GET
    @Path("usd-to-idr")
    public Response convertUsdToIdr(@QueryParam("amount") Double usdAmount) {
        if (usdAmount == null || usdAmount < 0) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Veuillez fournir un montant valide (amount > 0)\"}")
                    .build();
        }

        double idrAmount = usdAmount * USD_TO_IDR_RATE;

        JsonObject result = Json.createObjectBuilder()
                .add("usd", Math.round(usdAmount * 100.0) / 100.0)
                .add("idr", Math.round(idrAmount * 100.0) / 100.0)
                .add("rate", USD_TO_IDR_RATE)
                .add("currency_from", "USD")
                .add("currency_to", "IDR")
                .build();

        return Response
                .ok(result)
                .build();
    }

    /**
     * Retourne le taux de change actuel
     * 
     * @return JSON avec le taux de change
     */
    @GET
    @Path("rate")
    public Response getExchangeRate() {
        JsonObject result = Json.createObjectBuilder()
                .add("rate", USD_TO_IDR_RATE)
                .add("from", "USD")
                .add("to", "IDR")
                .add("timestamp", System.currentTimeMillis())
                .build();

        return Response
                .ok(result)
                .build();
    }
}
