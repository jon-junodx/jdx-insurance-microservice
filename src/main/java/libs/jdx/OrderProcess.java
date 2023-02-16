package libs.jdx;

import com.junodx.api.connectors.messaging.payloads.EventType;
import com.junodx.api.models.commerce.Order;

public interface OrderProcess {
    public OrderProcessResponse invoke(Order order);
}
