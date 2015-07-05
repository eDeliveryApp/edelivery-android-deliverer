package io.hackathon.edelivery.office;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by nampnq on 05/07/2015.
 */
public class ApiService {
    private static final String endPoint = "http://edelivery.cf/v1/orders/";

    private static void put(String endPoint, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.removeAllHeaders();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        client.put(endPoint, params, responseHandler);
    }

    public static void updateOrder(Context context, Order order, final OrderListener listener) {
        try {
            JSONObject orderObject = order.toJSONObject();
            if (orderObject == null) {
                if (listener != null) {
                    listener.onFailure("Null object");
                }
                return;
            }
            try {
                HttpEntity httpEntity = new StringEntity(orderObject.toString(), HTTP.UTF_8);
                AsyncHttpClient client = new AsyncHttpClient();
                client.put(context, endPoint + order.getId(), httpEntity, "application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(final int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("Api", response.toString());
                        if (listener != null) {
                            listener.onSuccess(response);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        if (listener != null) {
                            listener.onFailure(statusCode + " " + errorResponse.toString());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        // Handle when response can't parse to JSON
                        if (listener != null) {
                            listener.onFailure(statusCode + " " + responseString);
                        }
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onFailure("Encode unsupported!");
                }
            }
        } catch (JSONException e) {
            if (listener != null) {
                listener.onFailure("JSON can't parse");
            }
        }

    }

    public interface OrderListener {
        void onSuccess(Object object);

        void onFailure(Object object);
    }
}
