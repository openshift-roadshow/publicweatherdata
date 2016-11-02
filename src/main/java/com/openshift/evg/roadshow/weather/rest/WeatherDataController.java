package com.openshift.evg.roadshow.weather.rest;

import com.google.api.services.bigquery.Bigquery;
import com.openshift.evg.roadshow.weather.db.BigQueryConnection;
import com.openshift.evg.roadshow.weather.model.DataPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmorales on 01/11/16.
 */
@RequestMapping("/ws/data")
@RestController
public class WeatherDataController {

    @Autowired
    private BigQueryConnection con;

    @Value("${MONTH}")
    private String month;

    @RequestMapping(method = RequestMethod.GET, value = "/all", produces = "application/json")
    public List<DataPoint> getAllParks() {
        System.out.println("[DEBUG] getAllParks");

        return con.getAll(month);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/within", produces = "application/json")
    public List<DataPoint> findParksWithin(
            @RequestParam("lat1") float lat1,
            @RequestParam("lon1") float lon1,
            @RequestParam("lat2") float lat2,
            @RequestParam("lon2") float lon2) {
        System.out.println("[DEBUG] findParksWithin(" + lat1 + "," + lon1 + "," + lat2 + "," + lon2 + ")");

        return con.getAllWithin(month,lat1,lon1,lat2,lon2);
    }

}
