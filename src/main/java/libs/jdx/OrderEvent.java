package libs.jdx;

import com.junodx.api.connectors.messaging.payloads.EventType;
import libs.jdx.Order;
import com.junodx.api.models.commerce.types.OrderStatusType;

public class OrderEvent {
    private Order order;
    private OrderStatusType type;
    private String messageId;
    private boolean processed;

    public OrderStatusType getType() {
        return type;
    }

    public void setType(OrderStatusType type) {
        this.type = type;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
