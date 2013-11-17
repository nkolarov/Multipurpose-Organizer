package com.kolarov.organizeit;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Contacts;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kolarov.organizeit.Models.ItemModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by N.Kolarov on 13-11-11.
 */
public class HttpRequester {

    private String baseUrl;
    private HttpClient client;

    final private String SESSION_KEY_HEADER_LABEL = "X-sessionKey";
    final private String CONTENT_TYPE_JSON = "application/json";
    final private String ENCODING_UTF8 = "UTF-8";

    public HttpRequester (Context context){
        this.baseUrl = context.getResources().getString(R.string.root_url);
        this.client = new DefaultHttpClient();
    }

    public <T> T Get( String serviceUrl,  Class<T> type, String sessionKey){
        HttpGet httpGet = new HttpGet(this.baseUrl + serviceUrl);
        httpGet.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON);

        if (sessionKey != null && sessionKey != "")
            httpGet.setHeader(SESSION_KEY_HEADER_LABEL, sessionKey);

        try {
            HttpResponse response = this.client.execute(httpGet);

            if (response != null){
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(response.getEntity().getContent(), ENCODING_UTF8);
                T result = gson.fromJson(reader, type);

                return result;
            } else {
                return null;
            }
        }
        catch (Exception e){
            return null;
        }
    }

    public <T> Iterable<T> GetMany( String serviceUrl,  Class<T> type, String sessionKey){
        HttpGet httpGet = new HttpGet(this.baseUrl + serviceUrl);
        httpGet.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON);

        if (sessionKey != null && sessionKey != "")
            httpGet.setHeader(SESSION_KEY_HEADER_LABEL, sessionKey);

        try {
            HttpResponse response = this.client.execute(httpGet);

            if (response != null){
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(response.getEntity().getContent(), ENCODING_UTF8);
                JsonParser parser = new JsonParser();
                JsonArray array = parser.parse(reader).getAsJsonArray();
                ArrayList<T> result = new ArrayList<T>(array.size());
                for (int i=0; i< array.size(); i++){
                    JsonElement jsonElement = array.get(i);
                    T current = gson.fromJson(jsonElement, type);
                    result.add(current);
                }

                int a = 5; // debug;
                return result;
            } else {
                return null;
            }
        }
        catch (Exception e){
            return null;
        }
    }

    public <T> T Post( String serviceUrl,  Class<T> type, String sessionKey, Object data){
        HttpPost httpPost = new HttpPost(this.baseUrl + serviceUrl);
        httpPost.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON);

        if (sessionKey != null && sessionKey != "")
            httpPost.setHeader(SESSION_KEY_HEADER_LABEL, sessionKey);

        try {

            if (data != null){
                Gson gson = new Gson();
                StringEntity se = new StringEntity( gson.toJson(data).toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON));
                httpPost.setEntity(se);
            }

            HttpResponse response = this.client.execute(httpPost);
            int a = 5; // debug

            if (response != null){
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(response.getEntity().getContent(), ENCODING_UTF8);
                T result = gson.fromJson(reader, type);

                return result;
            } else {
                return null;
            }
        }
        catch (Exception e){
            return null;
        }
    }
}
