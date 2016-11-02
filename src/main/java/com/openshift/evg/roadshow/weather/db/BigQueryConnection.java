package com.openshift.evg.roadshow.weather.db;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.*;
import com.openshift.evg.roadshow.weather.model.DataPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * Created by jmorales on 01/11/16.
 */
@Component
public class BigQueryConnection {

    private static final String APPLICATION_NAME = "Google-OpenShift RoadShow BigQuery WeatherData";
    private static final String TABLE = "top-amplifier-139909:gsod.temp_2016";

    // "/tmp/gcp/google-creds.json"
    @Value("${GCP_CREDENTIALS_DIR}")
    private String GCP_CREDENTIALS_DIR;

    @Value("${GCP_CREDENTIALS_FILENAME}")
    private String GCP_CREDENTIALS_FILENAME;

    @Value("${GCP_PROJECT_ID}")
    private String PROJECT_ID; // "top-amplifier-139909"

    private List<String> columnNames;

    /**
     * Create the credential
     *
     * @return
     * @throws IOException
     */
    private Bigquery createAuthorizedClient() throws IOException {
        if (GCP_CREDENTIALS_DIR==null)
            throw new RuntimeException("GCP_CREDENTIALS_DIR has not been established");

        if (GCP_CREDENTIALS_FILENAME==null)
            throw new RuntimeException("GCP_CREDENTIALS_FILENAME has not been established");

        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(GCP_CREDENTIALS_DIR + File.separator + GCP_CREDENTIALS_FILENAME))
                .createScoped(Collections.singleton(BigqueryScopes.CLOUD_PLATFORM_READ_ONLY));

        return new Bigquery.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    /**
     * @param querySql
     * @return
     * @throws IOException
     */
    private List<TableRow> executeQuery(String querySql)
            throws IOException {
        Bigquery bigquery = createAuthorizedClient();

        if (bigquery == null)
            throw new RuntimeException("Connection/Authentication with BigQuery has not been established");

        if (PROJECT_ID==null)
            throw new RuntimeException("PROJECT_ID has not been established");

        QueryResponse query =
                bigquery.jobs().query(PROJECT_ID, new QueryRequest().setQuery(querySql)).execute();

        // Execute it
        GetQueryResultsResponse queryResult =
                bigquery
                        .jobs()
                        .getQueryResults(
                                query.getJobReference().getProjectId(), query.getJobReference().getJobId())
                        .execute();
        if (queryResult.getTotalRows().equals(BigInteger.ZERO)) {
            System.out.println("Got 0 results from BigQuery - not gonna do anything");
            return null;
        }
        columnNames = new ArrayList<String>();
        for (TableFieldSchema fieldSchema : queryResult.getSchema().getFields()) {
            columnNames.add(fieldSchema.getName());
        }

        return queryResult.getRows();
    }


    private List<DataPoint> resultsToDataPoints(List<TableRow> rows) {
        List<DataPoint> points = new ArrayList<DataPoint>();
        for (TableRow row : rows) {
            DataPoint point = WeatherDataParser.convert(columnNames, row);
            System.out.println(point);
            points.add(point);
        }
        return points;
    }

    public List<DataPoint> getAll(String month) {
        List<TableRow> rows = null;
        try {
            rows = executeQuery(
                    "SELECT avg_max_c AS max, avg_min_c AS min, avg_temp_c AS temp, mo AS month, stn AS station, lat, lon "
                            + "FROM [" + TABLE + "]"
                            + "WHERE mo='" + month + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (rows != null)
            return resultsToDataPoints(rows);
        return new ArrayList<DataPoint>();
    }

    public List<DataPoint> getAllWithin(String month,
                                        float lat1,
                                        float lon1,
                                        float lat2,
                                        float lon2) {
        List<TableRow> rows = null;
        try {
            rows = executeQuery(
                    "SELECT avg_max_c AS max, avg_min_c AS min, avg_temp_c AS temp, mo AS month, stn AS station, lat, lon "
                            + "FROM [" + TABLE + "]"
                            + "WHERE mo='" + month + "' AND lat<=" + lat1 + " AND lat>=" + lat2 + " " +
                            " AND lon>=" + lon1 + " AND lon<=" + lon2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (rows != null)
            return resultsToDataPoints(rows);
        return new ArrayList<DataPoint>();
    }

    public static void main(String[] args) throws IOException {
        // Create a new Bigquery client authorized via Application Default Credentials.
        BigQueryConnection con = new BigQueryConnection();

//        for (DataPoint p : con.getAll("10")) {
        for (DataPoint p : con.getAllWithin("10",45,-20,35,5)) {
            System.out.println(p);
        }
    }
}
