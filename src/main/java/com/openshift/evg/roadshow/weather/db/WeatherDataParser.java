package com.openshift.evg.roadshow.weather.db;

import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import com.openshift.evg.roadshow.weather.model.DataPoint;
import com.openshift.evg.roadshow.weather.model.DataPointBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jmorales on 01/11/16.
 */
public class WeatherDataParser {

    public static DataPoint convert(List<String> columnNames, TableRow rows){
        DataPoint point = new DataPoint();
        final Map<String, Object> document = new HashMap<String, Object>();

        for (final Map.Entry<String, Object> row : rows.entrySet()) {
            int columnCounter = 0;

            for (TableCell field : (ArrayList<TableCell>) row.getValue()) {
                final String key = columnNames.get(columnCounter++);

                if (field.getV() instanceof String) {
                    final String value = (String) field.getV();
                    document.put(key, value);
                }
            }
        }
        return new DataPointBuilder().setId(document.get("station"))
                .setLatitude(document.get("lat"))
                .setLongitude(document.get("lon"))
                .setMonth(document.get("month"))
                .setMax(document.get("max"))
                .setMin(document.get("min"))
                .setTemp(document.get("temp")).build();
    }
}
