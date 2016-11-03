package com.openshift.evg.roadshow.weather.model;

public class DataPoint {

    private Object id;
    private Object name;
    private Object latitude;
    private Object longitude;
    private Coordinates position;
    private Object info;


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

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
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

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "name=" + name +
                ", position=" + position +
                ", info=" + info +
                '}';
    }
}
