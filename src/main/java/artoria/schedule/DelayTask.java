package artoria.schedule;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DelayTask implements Task {
    private String   id;
    private String   name;
    private String   type;
    private String   description;
    private Object   data;
    private Long     delayTime;
    private TimeUnit timeUnit;
    private Date     deliveryTime;
    private String   handler;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Object getData() {

        return data;
    }

    public void setData(Object data) {

        this.data = data;
    }

    public Long getDelayTime() {

        return delayTime;
    }

    public void setDelayTime(Long delayTime) {

        this.delayTime = delayTime;
    }

    public TimeUnit getTimeUnit() {

        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {

        this.timeUnit = timeUnit;
    }

    public Date getDeliveryTime() {

        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {

        this.deliveryTime = deliveryTime;
    }

    public String getHandler() {

        return handler;
    }

    public void setHandler(String handler) {

        this.handler = handler;
    }

}
