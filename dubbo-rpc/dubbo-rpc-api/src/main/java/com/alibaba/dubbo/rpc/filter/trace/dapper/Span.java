package com.alibaba.dubbo.rpc.filter.trace.dapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shield on 2017/7/24.
 */
public class Span implements Serializable {
    private static final long serialVersionUID = -4502898658911530522L;

    // 跟踪号
    private Long traceId;
    // span的id
    private Long id;
    // 该span的父id，如果为null则该span为父span
    private Long parentId;
    // span的名字
    private String name;
    // serviceId
    private String serviceId;
    // annotations
    private List<Annotation> annotations;
    // 是否采样
    private Boolean isSample;

    public Span() {
    }

    public Span(Long traceId, Long id, Long parentId, String name, String serviceId, List<Annotation> annotations, Boolean isSample) {
        this.traceId = traceId;
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.serviceId = serviceId;
        this.annotations = annotations;
        this.isSample = isSample;
    }

    @Override
    public String toString() {
        return "Span{" +
                "traceId=" + traceId +
                ", id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", annotations=" + annotations +
                ", isSample=" + isSample +
                '}';
    }

    public void addAnnotation(Annotation annotation) {
        if (this.annotations == null) {
            this.annotations = new ArrayList<Annotation>();
        }
        this.annotations.add(annotation);
    }

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public Boolean isSample() {
        return isSample;
    }

    public void setSample(Boolean sample) {
        isSample = sample;
    }
}
