package io.hackathon.edelivery.office;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nampnq on 05/07/2015.
 */
public class Order {
    private String id;
    private LatLng latLng;
    private int status;
    private String phone;

    public Order(LatLng latLng, String id, String phone) {
        this.latLng = latLng;
        this.id = id;
        this.phone = phone;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject orderObject = new JSONObject();
//        orderObject.put("status", getStatus());
        orderObject.put("deliver_phone_number", getPhone());
        LatLng latLng = getLatLng();
        if (latLng != null) {
            JSONObject latLngObject = new JSONObject();
            latLngObject.put("lat", latLng.latitude);
            latLngObject.put("long", latLng.longitude);
            orderObject.put("loc", latLngObject);
        }
        Log.d("Order", orderObject.toString());
        return orderObject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
