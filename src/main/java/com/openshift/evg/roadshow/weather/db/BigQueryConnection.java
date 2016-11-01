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
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by jmorales on 01/11/16.
 */
@Component
public class BigQueryConnection {

    public static final String GCP_CREDENTIALS_FILE = "/tmp/google-creds.json";

    private static final String PROJECT_ID = "top-amplifier-139909";

    private List<String> columnNames;

    private String projectId = PROJECT_ID;
    private String credentialsFile = GCP_CREDENTIALS_FILE;

    public BigQueryConnection() {
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCredentialsFile() {
        return credentialsFile;
    }

    public void setCredentialsFile(String credentialsFile) {
        this.credentialsFile = credentialsFile;
    }


    private Bigquery bigquery = null;

    /**
     * @return
     * @throws IOException
     */
    public void createAuthorizedClient() throws IOException {
        // Create the credential
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(credentialsFile))
                .createScoped(Collections.singleton(BigqueryScopes.CLOUD_PLATFORM_READ_ONLY));

        bigquery = new Bigquery.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName("Google-OpenShift RoadShow BigQuery WeatherData")
                .build();
    }


    /**
     * @param querySql
     * @return
     * @throws IOException
     */
    private List<TableRow> executeQuery(String querySql)
            throws IOException {
        if (bigquery == null)
            throw new RuntimeException("Connection/Authentication with BigQuery has not been established");

        QueryResponse query =
                bigquery.jobs().query(projectId, new QueryRequest().setQuery(querySql)).execute();

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

    public void resultsToJson(List<TableRow> rows){
        for (TableRow row : rows) {
            try {
                System.out.println(getJson(row));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<DataPoint> resultsToDataPoints(List<TableRow> rows){
        List<DataPoint> points = new ArrayList<DataPoint>();
        for (TableRow row : rows) {
            DataPoint point = WeatherDataParser.convert(columnNames, row);
            System.out.println(point);
            points.add(point);
        }
        return points;
    }


    private String uniqueIdField = null;

    /**
     * Get the json document for this row.
     *
     * @param rows
     * @return A string array with the first value holding the json document and the second element holding the unique id
     *         (if configured)
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private String getJson(@Nonnull final TableRow rows) throws IOException {
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
        return rows.getFactory().toString(document);
    }


    public List<DataPoint> getAll(){
        List<TableRow> rows = null;
        try {
            rows =  executeQuery(
                    "SELECT avg_max_c AS max, avg_min_c AS min, avg_temp_c AS temp, mo AS month, stn AS station, lat, lon "
                            + "FROM [top-amplifier-139909:gsod.temp_2016]"
                            + "WHERE stn='644590'");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (rows!=null)
            return resultsToDataPoints(rows);
        return new ArrayList<DataPoint>();
    }

    public static void main(String[] args) throws IOException {

        // Create a new Bigquery client authorized via Application Default Credentials.
        BigQueryConnection con = new BigQueryConnection();
        con.createAuthorizedClient();

        List<TableRow> rows =
                con.executeQuery(
                        "SELECT avg_max_c AS max, avg_min_c AS min, avg_temp_c AS temp, mo AS month, stn AS station, lat, lon "
                                + "FROM [top-amplifier-139909:gsod.temp_2016]"
                                + "WHERE stn='644590'");

        con.resultsToDataPoints(rows);
    }
}
