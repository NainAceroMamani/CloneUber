package com.nain.cloneuber.models;

import java.util.ArrayList;

public class FCMResponse {
    // mapeamos la respuesta cuando envaimos una notificacion
    // => https://www.site24x7.com/tools/json-to-java.html
    private float multicast_id;
    private float success;
    private float failure;
    private float canonical_ids;
    ArrayList<Object> results = new ArrayList<Object>();

    public FCMResponse(float multicast_id, float success, float failure, float canonical_ids, ArrayList<Object> results) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.results = results;
    }

    // Getter Methods

    public float getMulticast_id() {
        return multicast_id;
    }

    public float getSuccess() {
        return success;
    }

    public float getFailure() {
        return failure;
    }

    public float getCanonical_ids() {
        return canonical_ids;
    }

    // Setter Methods

    public void setMulticast_id( float multicast_id ) {
        this.multicast_id = multicast_id;
    }

    public void setSuccess( float success ) {
        this.success = success;
    }

    public void setFailure( float failure ) {
        this.failure = failure;
    }

    public void setCanonical_ids( float canonical_ids ) {
        this.canonical_ids = canonical_ids;
    }
}
