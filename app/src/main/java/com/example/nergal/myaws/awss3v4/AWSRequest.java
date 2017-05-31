package com.example.nergal.myaws.awss3v4;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by nergal on 2017/5/23.
 */

public class AWSRequest {
    private String method;//例如："PUT" "GET" "POST"
    private String url;
    private long requestTime;//Date to be used to sign the request.    值等同x-amz-date
    /*
    以下是AWS s3 v4校验参数
      Content-MD5
      Host
      x-amz-content-sha256
      x-amz-date
      Authorization
  -------------------------
      Content-Length
      Content-Type
      */
    private Map<String,String> headers = new HashMap<>();

    public AWSRequest(String url,String method,long requestTime){
        this.url = url;
        this.method = method;
        this.requestTime = requestTime;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }


    public String getHeader(String headerName){
        if (headers.containsKey(headerName)){
            return headers.get(headerName);
        }
        return null;
    }

    public AWSRequest setHeader(String headerName,String headerInfo){
        headers.put(headerName,headerInfo);
        return this;
    }

    public String getMethod() {
        return method;
    }

    public void setHttpMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public String toString() {
        Set<String> headerNames= headers.keySet();
        String canonicalHeaders = "";
        for (String headerName : headerNames){
            canonicalHeaders += headerName+":"+headers.get(headerName)+"||";
        }
        return "AWSRequest{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", requestTime=" + requestTime +
                ", headers=" + canonicalHeaders+
                '}';
    }
}
