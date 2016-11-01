package com.openshift.evg.roadshow.weather.model;

public class DataPoint {

    private Object id;
    private Object name;
    private Coordinates position;
    private Object details;


    public DataPoint() {
    }

    public DataPoint(Object id, Object name) {
        this.id = id;
        this.name = name;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "id=" + id +
                ", name=" + name +
                ", coordinates=" + position +
                ", details=" + details +
                '}';
    }
}
