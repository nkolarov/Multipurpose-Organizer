package com.kolarov.organizeit;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Contacts;


import com.google.gson.Gson;

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

/**
 * Created by N.Kolarov on 13-11-11.
 */
public class HttpRequester {

    private String baseUrl;
    private HttpClient client;

    public HttpRequester (Context context){
        this.baseUrl = context.getResources().getString(R.string.root_url);
        this.client = new DefaultHttpClient();
    }

    public <T> T Get( String serviceUrl,  Class<T> type){
        HttpGet httpGet = new HttpGet(this.baseUrl + serviceUrl);
        httpGet.setHeader("Content-type", "application/json");

        try {
            HttpResponse response = this.client.execute(httpGet);

            if (response != null){
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
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

    public <T> T Post( String serviceUrl,  Class<T> type, String sessionKey, Object data){
        HttpPost httpPost = new HttpPost(this.baseUrl + serviceUrl);
        httpPost.setHeader("Content-type", "application/json");

        if (sessionKey != null && sessionKey != "")
            httpPost.setHeader("X-sessionKey", sessionKey);

        try {

            if (data != null){
                Gson gson = new Gson();
                StringEntity se = new StringEntity( gson.toJson(data).toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);
            }

            HttpResponse response = this.client.execute(httpPost);
            int a = 5; // debug

            if (response != null){
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
                T result = gson.fromJson(reader, type);
                int b =3;
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
