package edu.jhu.epioneers.clueless.communication;

import com.google.gson.Gson;
import edu.jhu.epioneers.clueless.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;

/**
 * Handles HTTP communication and deserializes the JSON response of generic type Response
 */
public class RequestHandler {
    /**
     * Makes GET request to the server and returns the deserialized JSON response
     * @param path Url of request
     * @param <T> Type of data object to deserialize
     * @return Generic response type
     */
    public <T> Response<T> makeGETRequest(String path, Type type)
    {
        //TODO Use Apache classes?
        try {
            URL requestUrl = new URL(Constants.SERVER_URL+path);

            BufferedReader br = new BufferedReader(new InputStreamReader(requestUrl.openStream()));
            String response = "";
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                response+=strTemp;
            }

            Gson gson = new Gson();
            return gson.fromJson(response,type);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return getFailureResponse();
    }

    /**
     * Makes POST request to the server and returns the deserialized JSON response
     * @param path Path of request
     * @param data Object to serialize and POST to the server
     * @param <T> Type of data object to deserialize
     * @return Generic response type
     */
    public <T> Response<T> makePOSTRequest(String path, Object data, Type type)
    {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(Constants.SERVER_URL+path);
            Gson gson = new Gson();

            StringEntity input = new StringEntity(gson.toJson(data));
            input.setContentType("application/json");
            httppost.setEntity(input);

            HttpResponse httpResponse = httpclient.execute(httppost);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((httpResponse.getEntity().getContent())));

            String response = "";
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                response+=strTemp;
            }

            return gson.fromJson(response, type);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return getFailureResponse();
    }

    private <T> Response<T> getFailureResponse() {
        Response<T> response = new Response<T>();
        response.setHttpStatusCode(500);
        response.setMessage("An unknown error has occurred");
        response.setAdditionalStatusCode(1);

        return response;
    }

    /**
     * Makes PUT request to the server and returns the deserialized JSON response
     * @param url Url of request
     * @param data Object to serialize and PUT to the server
     * @param <T> Type of data object to deserialize
     * @return Generic response type
     */
    public <T> Response<T> makePUTRequest(String url, Object data)
    {
        return null;
    }
}
