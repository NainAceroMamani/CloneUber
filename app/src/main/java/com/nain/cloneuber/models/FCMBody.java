package com.nain.cloneuber.models;

import java.util.Map;

public class FCMBody {
    // modelo para enviar el body => ejemplo en POSTMANN
    // https://www.site24x7.com/tools/json-to-java.html
    private String to;
    private String priority;
    private String ttl; // para asegurar que la notifiucation este en tiempo real service de fireabse databse
    Map<String, String> data;

    public FCMBody(String to, String priority, String ttl, Map<String, String> data) {
        this.to = to;
        this.priority = priority;
        this.data = data;
        this.ttl = ttl;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    // Getter Methods

    public String getTo() {
        return to;
    }

    public String getPriority() {
        return priority;
    }

    // Setter Methods

    public void setTo( String to ) {
        this.to = to;
    }

    public void setPriority( String priority ) {
        this.priority = priority;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
}