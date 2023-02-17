package libs.jdx;

import com.junodx.api.connectors.messaging.payloads.EventType;
import com.junodx.api.models.commerce.Order;
import com.junodx.api.util.UrlClientConnection;

public interface OrderProcess {
    public OrderProcessResponse invoke(JunoService junoApi, Order order);
    public OrderProcessResponse setProcessed(Order order, boolean success);
}
