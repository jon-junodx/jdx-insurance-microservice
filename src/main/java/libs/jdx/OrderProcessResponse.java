package libs.jdx;

import libs.jdx.Order;

import java.util.Date;

public class OrderProcessResponse {
    private boolean processed;
    private Order order;
    private Date timetamp;

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Date getTimetamp() {
        return timetamp;
    }

    public void setTimetamp(Date timetamp) {
        this.timetamp = timetamp;
    }
}
