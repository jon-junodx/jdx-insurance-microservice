package libs.jdx;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junodx.api.connectors.http.HttpClient;
import com.junodx.api.connectors.messaging.payloads.EventType;
import com.junodx.api.dto.mappers.CommerceMapStructMapper;
import com.junodx.api.dto.mappers.CommerceMapStructMapperImpl;
import com.junodx.api.dto.models.commerce.OrderDto;
import com.junodx.api.models.auth.User;
import com.junodx.api.models.commerce.InsuranceQuote;
import libs.jdx.Order;
import com.junodx.api.models.payment.insurance.InsurancePolicy;
import com.junodx.api.util.UrlClientConnection;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class OrderProcessGetQuote implements OrderProcess {

    final String quoteUrl = "";
    JunoService junoApi = null;
    ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public OrderProcessResponse invoke(JunoService junoApi, Order order) {
        try {
            this.junoApi = junoApi;
            OrderProcessResponse response = new OrderProcessResponse();

            System.out.println("About to invoke get quote");
            if (order == null)
                return setProcessed(order, true);


            InsuranceQuote quote = getQuote(order);
            if (quote == null)
                return setProcessed(order, true);

            try {
    System.err.println("Quote: " + mapper.writeValueAsString(quote));
                quote = addQuoteToOrder(quote);

                //retrieve the updated order
                order = junoApi.getOrder(order.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Got quote " + quote.getEstimatedPatientResponsiblePortion() + " for order " + order.getId());

            //Change this to processed = true after integrating with xifin microservice
            return setProcessed(order, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InsuranceQuote getQuote(Order order) throws URISyntaxException, IOException, InterruptedException {
        InsuranceQuote quote = null;
        com.junodx.api.models.commerce.Order o = new com.junodx.api.models.commerce.Order();
        o.setId(order.getId());
        o.setOrderStatusHistory(order.getOrderStatusHistory());
        o.setCustomer(order.getCustomer());
        o.setAmountDue(order.getAmountDue());

        //Need to fix this when we move to separate customer v. patient model
        User patient = order.getCustomer();

        List<InsurancePolicy> policies = junoApi.getPolicies(patient.getId());
        InsurancePolicy policy = null;

        if(policies == null)
            return null;
        else if(policies.size() == 0)
            return null;
        else if(policies.size() >= 1)
            policy = policies.get(0);

     //   if(policy != null) {
     //       quote.setInsurancePolicy(policy);
     //       quote.setInsuredUserName(policy.getPolicyHolderName());
     //   }

        /*
        quote.setPatient(order.getCustomer());
        quote.setOrder(o);
        quote.setQuotedAt(new Date());
        quote.setCurrency(Currency.getInstance("USD"));
        quote.setExternalEstimateId(generateRandomString());
        quote.setBillAmount(order.getAmountDue());
        quote.setEligible(Boolean.TRUE);
        quote.setEstimatedPatientResponsiblePortion((float) (order.getAmountDue() * .8));

         */

        //Do something with insurance policies here to construct a request to the Xifin microservice

        return junoApi.fetchQuote(order.getId(), policy.getId(), patient.getId(), "Female");
    }

    private String generateRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public InsuranceQuote addQuoteToOrder(InsuranceQuote quote) throws Exception {
        try {
            String q = junoApi.addQuoteToOrder(quote);
            if(q != null)
                return mapper.readValue(q,InsuranceQuote.class);

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public OrderProcessResponse setProcessed(Order order, boolean success) {
        OrderProcessResponse response = new OrderProcessResponse();
        response.setProcessed(success);
        response.setTimetamp(new Date());
        response.setOrder(order);

        return response;
    }
}
