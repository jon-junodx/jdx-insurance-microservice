package libs.jdx;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junodx.api.connectors.http.HttpClient;
import com.junodx.api.connectors.messaging.payloads.EventType;
import com.junodx.api.dto.mappers.CommerceMapStructMapper;
import com.junodx.api.dto.mappers.CommerceMapStructMapperImpl;
import com.junodx.api.dto.models.commerce.OrderDto;
import com.junodx.api.models.commerce.InsuranceQuote;
import com.junodx.api.models.commerce.Order;
import com.junodx.api.models.payment.insurance.InsurancePolicy;
import com.junodx.api.util.UrlClientConnection;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;

public class OrderProcessGetQuote implements OrderProcess {

    final String quoteUrl = "";
    JunoService junoApi = null;
    ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public OrderProcessResponse invoke(JunoService junoApi, Order order) {
        this.junoApi = junoApi;
        OrderProcessResponse response = new OrderProcessResponse();

        System.out.println("About to invoke get quote");
        if(order == null)
            return setProcessed(order, false);

        /*
        InsuranceQuote quote = getQuote(order);
        if(quote == null)
            setProcessed(order, false);

        System.out.println("Got quote " + quote.getEstimatedPatientResponsiblePortion() + " for order " + order.getId());

        try {
            quote = addQuoteToOrder(quote);

            //retrieve the updated order
            order = junoApi.getOrder(order.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        //Change this to processed = true after integrating with xifin microservice
        return setProcessed(order, false);
    }



    public InsuranceQuote getQuote(Order order) {
        CommerceMapStructMapper mapper = new CommerceMapStructMapperImpl();
        InsuranceQuote quote = new InsuranceQuote();
        quote.setOrder(order);

        //Need to fix this when we move to separate customer v. patient model
        quote.setPatient(order.getCustomer());

        List<InsurancePolicy> policies = order.getCustomer().getInsurancePolicies();

        //Do something with insurance policies here to construct a request to the Xifin microservice

        return quote;
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
