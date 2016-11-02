package com.openshift.evg.roadshow.weather.model;

/**
 *
 * Created by jmorales on 01/11/16.
 */
public class DataPointBuilder {

    private Object id;

    private Object min;
    private Object max;
    private Object temp;
    private Object month;

    private Object latitude;
    private Object longitude;

    public DataPointBuilder setId(Object param){
        this.id = param;
        return this;
    }

    public DataPointBuilder setLatitude(Object param){
        this.latitude = param;
        return this;
    }

    public DataPointBuilder setLongitude(Object param){
        this.longitude = param;
        return this;
    }

    public DataPointBuilder setMin(Object param){
        this.min = param;
        return this;
    }

    public DataPointBuilder setMax(Object param){
        this.max = param;
        return this;
    }

    public DataPointBuilder setTemp(Object param){
        this.temp = param;
        return this;
    }

    public DataPointBuilder setMonth(Object param){
        this.month = param;
        return this;
    }

    public DataPoint build(){
        DataPoint point = new DataPoint(id,id);
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setPosition(new Coordinates((String)latitude,(String)longitude));
        point.setDetails("Month: "+ month + ", Min: " + min + ", Max: "+ max + ", temp:" + temp);
        return point;
    }

}
