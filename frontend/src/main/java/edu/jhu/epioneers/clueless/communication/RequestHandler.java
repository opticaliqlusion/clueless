package edu.jhu.epioneers.clueless.communication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.jhu.epioneers.clueless.Constants;
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
        //TODO Proof of concept only for compilation purposes only, use Apache classes
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

        return null;
    }

    /**
     * Makes POST request to the server and returns the deserialized JSON response
     * @param url Url of request
     * @param data Object to serialize and POST to the server
     * @param <T> Type of data object to deserialize
     * @return Generic response type
     */
    public <T> Response<T> makePOSTRequest(String url, Object data)
    {
        //HttpClient httpclient = HttpClients.createDefault();
        //HttpPost httppost = new HttpPost("http://www.a-domain.com/foo/");

        return null;
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
