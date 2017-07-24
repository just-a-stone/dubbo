package com.alibaba.dubbo.rpc.filter.trace.dapper;

import java.io.Serializable;

/**
 * Created by shield on 2017/7/24.
 */
public class Annotation implements Serializable {
    private static final long serialVersionUID = -1652527982406101722L;

    // 时间戳
    private Long timestamp;
    private String value;
    // endpoint
    private EndPoint endPoint;

    public Annotation() {
    }

    public Annotation(Long timestamp, String value, EndPoint endPoint) {
        this.timestamp = timestamp;
        this.value = value;
        this.endPoint = endPoint;
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "timestamp=" + timestamp +
                ", value='" + value + '\'' +
                ", endPoint=" + endPoint +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Annotation that = (Annotation) o;

        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return endPoint != null ? endPoint.equals(that.endPoint) : that.endPoint == null;
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (endPoint != null ? endPoint.hashCode() : 0);
        return result;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EndPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(EndPoint endPoint) {
        this.endPoint = endPoint;
    }

}
