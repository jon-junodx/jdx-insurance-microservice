package libs.jdx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junodx.api.models.auth.User;
import com.junodx.api.models.commerce.OrderStatus;
import com.junodx.api.models.commerce.types.OrderStatusType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Order {
    private String id;
    private List<OrderStatus> orderStatusHistory;
    private User customer;
    private Float amountDue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OrderStatus> getOrderStatusHistory() {
        return orderStatusHistory;
    }

    public void setOrderStatusHistory(List<OrderStatus> orderStatusHistory) {
        this.orderStatusHistory = orderStatusHistory;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Float getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Float amountDue) {
        this.amountDue = amountDue;
    }

    @JsonProperty("currentStatus")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public OrderStatusType getCurrentStatus(){
        if(this.orderStatusHistory != null) {
            List<OrderStatus> statusType = this.orderStatusHistory.stream().filter(x -> x.isCurrent()).collect(Collectors.toList());
            if (this.orderStatusHistory.size() == 0)
                return null;

            if (statusType != null && statusType.size() == 0) {
                setLatestStatusToCurrent();
                statusType = this.orderStatusHistory.stream().filter(x -> x.isCurrent()).collect(Collectors.toList());
            } else if (statusType != null && statusType.size() > 1)
                resetStatusAndFindCurrent();

            return statusType.get(0).getStatusType();
        }
        return null;
    }

    private void resetStatusAndFindCurrent(){
        List<OrderStatus> statusType = this.orderStatusHistory.stream().filter(x -> x.isCurrent()).collect(Collectors.toList());
        if(statusType != null && statusType.size() > 1) {
            Collections.sort(statusType);
            int index = 0;
            for(OrderStatus s : statusType){
                if(index > 0)
                    s.setCurrent(false);
            }
        }
    }

    private void setLatestStatusToCurrent(){
        Collections.sort(this.orderStatusHistory);
        if(this.orderStatusHistory.size() > 0)
            this.orderStatusHistory.get(0).setCurrent(true);
    }

    private void setAllStatusesToNotCurrent(){
        if(this.orderStatusHistory != null){
            for(OrderStatus l : this.orderStatusHistory)
                l.setCurrent(false);
        }
    }
}
