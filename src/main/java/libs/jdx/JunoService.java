package libs.jdx;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junodx.api.controllers.lab.payloads.TestReportPdfUpdate;
import com.junodx.api.controllers.payloads.ClientCredentialsPayload;
import com.junodx.api.controllers.payloads.OAuth2TokenDTO;
import com.junodx.api.models.commerce.InsuranceQuote;
import com.junodx.api.models.payment.insurance.InsurancePolicy;
import libs.jdx.Order;
import com.junodx.api.models.laboratory.TestReport;
import com.junodx.api.util.UrlClientConnection;
import libs.jdx.payloads.FetchQuotePayload;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class JunoService {
    UrlClientConnection connection;
    private OAuth2TokenDTO accessToken;

    ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    LambdaLogger logger;

    public JunoService(String url, String clientId, String clientSecret, LambdaLogger logger) {
        this.logger = logger;

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
            String addQuotePayload = httpSend(this.connection.getUrl() + "/orders/insurance/quotes", accessToken.getAccessToken(), quote);
        System.out.println("Response from posting new quote: "  +addQuotePayload);
            return addQuotePayload;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Order getOrder(String orderId) throws URISyntaxException, IOException, InterruptedException {
        String queryString = orderId;
        String orderQueryPayload = httpSend(this.connection.getUrl() + "/orders/" + queryString, accessToken.getAccessToken(), null);
        try {
            return mapper.readValue(orderQueryPayload, Order.class);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public InsuranceQuote fetchQuote(String orderId, String policyId, String patientId, String gender) throws URISyntaxException, IOException, InterruptedException {
        String queryString = "insurance/quotes/fetch";
        FetchQuotePayload payload = new FetchQuotePayload();
        payload.setOrderId(orderId);
        payload.setPolicyId(policyId);
        payload.setPatientId(patientId);
        payload.setGender(gender);

        String fetchPayloadResponse = httpSend(this.connection.getUrl() + "/orders/" + queryString, accessToken.getAccessToken(), payload);
        try {
   System.err.println("Response from fetch quote: " + mapper.writeValueAsString(fetchPayloadResponse));
            return mapper.readValue(fetchPayloadResponse, InsuranceQuote.class);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<InsurancePolicy> getPolicies(String customerId) throws URISyntaxException, IOException, InterruptedException {
        String queryString = customerId + "/insurance";
        String orderQueryPayload = httpSend(this.connection.getUrl() + "/users/" + queryString, accessToken.getAccessToken(), null);
        try {
            return mapper.readValue(orderQueryPayload, new TypeReference<List<InsurancePolicy>>() {});
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
