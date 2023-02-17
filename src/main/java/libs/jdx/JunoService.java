package libs.jdx;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junodx.api.controllers.lab.payloads.TestReportPdfUpdate;
import com.junodx.api.controllers.payloads.ClientCredentialsPayload;
import com.junodx.api.controllers.payloads.OAuth2TokenDTO;
import com.junodx.api.models.commerce.InsuranceQuote;
import com.junodx.api.models.commerce.Order;
import com.junodx.api.models.laboratory.TestReport;
import com.junodx.api.util.UrlClientConnection;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JunoService {
    UrlClientConnection connection;
    private OAuth2TokenDTO accessToken;

    ObjectMapper mapper;
    LambdaLogger logger;

    public JunoService(String url, String clientId, String clientSecret, LambdaLogger logger) {
        this.logger = logger;
        mapper = new ObjectMapper();

        this.connection = new UrlClientConnection();
        this.connection.setUrl(url);
        this.connection.setClientSecret(clientSecret);
        this.connection.setClientId(clientId);

        try {
            getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JunoService(UrlClientConnection connection, LambdaLogger logger) {
        this.logger = logger;
        mapper = new ObjectMapper();

        this.connection = connection;

        try {
            getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected void getAccessToken() throws IOException, URISyntaxException, InterruptedException, HttpClientAccessException {
        ClientCredentialsPayload creds = new ClientCredentialsPayload();
        creds.setClientId(this.connection.getClientId());
        creds.setClientSecret(this.connection.getClientSecret());

        String tokenPayload = httpSend(this.connection.getUrl() + "/oauth2/token", "", mapper.writeValueAsString(creds));

        OAuth2TokenDTO token = mapper.readValue(tokenPayload, OAuth2TokenDTO.class);

        if (token != null)
            this.accessToken = token;
    }

    public String addQuoteToOrder(InsuranceQuote quote) throws URISyntaxException, IOException, InterruptedException {
        try {
            String addQuotePayload = httpSend(this.connection.getUrl() + "/api/orders/insurance/quotes", accessToken.getAccessToken(), quote);
            return addQuotePayload;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Order getOrder(String orderId) throws URISyntaxException, IOException, InterruptedException {
        String queryString = orderId;
        String orderQueryPayload = httpSend(this.connection.getUrl() + "/api/orders/" + queryString, accessToken.getAccessToken(), null);
        try {
            return mapper.readValue(orderQueryPayload, Order.class);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String httpSend(String uri, String accessToken, Object body) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest;
        if (body != null) {
            String bodyString = "";
            if (body instanceof String)
                bodyString = body.toString();
            else
                bodyString = mapper.writeValueAsString(body);


            httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyString))
                    .build();
        } else {
            httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();
        }

        HttpResponse<String> response = java.net.http.HttpClient
                .newBuilder()
                .build()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200)
            return response.body();
        else
            throw new HttpClientAccessException("Error occurred calling Juno API " + response.statusCode() + " : " + response.body() + " ");
    }
}
