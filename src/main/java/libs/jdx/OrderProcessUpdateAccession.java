package libs.jdx;

import com.junodx.api.connectors.messaging.payloads.EventType;
import com.junodx.api.models.commerce.Order;
import com.junodx.api.util.UrlClientConnection;

import java.util.Date;

public class OrderProcessUpdateAccession implements OrderProcess {
    @Override
    public OrderProcessResponse invoke(JunoService junoApi, Order order) {
        System.out.println("About to invoke update accession");
        return null;
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
