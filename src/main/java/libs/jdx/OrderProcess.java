package libs.jdx;

import com.junodx.api.connectors.messaging.payloads.EventType;
import libs.jdx.Order;
import com.junodx.api.util.UrlClientConnection;

public interface OrderProcess {
    public OrderProcessResponse invoke(JunoService junoApi, Order order);
    public OrderProcessResponse setProcessed(Order order, boolean success);
}
