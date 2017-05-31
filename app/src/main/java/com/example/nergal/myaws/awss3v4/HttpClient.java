package com.example.nergal.myaws.awss3v4;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nergal on 2017/5/23.
 */

public class HttpClient implements BaseHttpClient{
    private String tag = "HttpClient";
    private AWSUploadListener uploadListener;

    public void setUploadListener(AWSUploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }



    public void uploadFile(AWSRequest request, File file)  {
        FileInputStream fileInputStream = null;
        int fileLength = 0;
        try {
            fileInputStream = new FileInputStream(file);
            fileLength = fileInputStream.available();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            uploadFailed(file, e);
        } catch (IOException e) {
            e.printStackTrace();
            uploadFailed(file, e);
        }

        Log.e(tag,"---------fileLength------" + fileLength);
        if (fileLength == 0){
            uploadFailed(file, new Exception("file is 0 bytes"));
            return;
        }
        try {
            URL url = new URL(request.getUrl());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod(request.getMethod().toUpperCase());
            con.setFixedLengthStreamingMode(fileLength);
            //添加签名请求头
            if (!request.getHeaders().isEmpty()) {
                for (String headerName : request.getHeaders().keySet()) {
                    con.setRequestProperty(headerName, request.getHeaders().get(headerName));
                }
            }

            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            int bytesCurrent = 0;
            // 从文件读取数据到缓冲区
            while ((length = fileInputStream.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
                ds.flush();
                bytesCurrent += length;
                if (uploadListener!=null){
                    uploadListener.onProgressChanged(file,bytesCurrent,fileLength);
                }
            }
            fileInputStream.close();

            ds.flush();
            // 取得Response内容
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            ds.close();
            is.close();
            con.disconnect();
            System.gc();
            if (uploadListener!=null){
                uploadListener.onComplite(file);
            }
        } catch (Exception e) {
            uploadFailed(file, e);
        }
    }

    private void uploadFailed(File file, Exception e) {
        if (uploadListener!=null){
            uploadListener.onError(file,e);
        }
    }

}
