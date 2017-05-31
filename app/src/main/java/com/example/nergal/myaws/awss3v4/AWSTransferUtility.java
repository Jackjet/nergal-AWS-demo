package com.example.nergal.myaws.awss3v4;

import android.util.Log;

import com.example.nergal.myaws.Constants;
import com.example.nergal.myaws.awss3v4.signaturev4.DateUtils;
import com.example.nergal.myaws.awss3v4.signaturev4.Digest;
import com.example.nergal.myaws.awss3v4.signaturev4.Signer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nergal on 2017/5/23.
 */

public class AWSTransferUtility {
    private final static String TAG = "AWSTransferUtility" ;
    private static AWSTransferUtility awsTransferUtility ;
    private ExecutorService fixedThreadPool;
    private static int MAX_THREAD_ONE_TIME = 5;//最大并发数量
    private AWSUploadListener uploadListener;
    private BaseHttpClient httpClient;

    private AWSTransferUtility(){
        fixedThreadPool = Executors.newFixedThreadPool(MAX_THREAD_ONE_TIME);
    }

    public static synchronized AWSTransferUtility getInstance(){
        if (awsTransferUtility==null){
            awsTransferUtility = new AWSTransferUtility();
        }
        return awsTransferUtility;
    }

    public AWSTransferUtility setUploadListener(AWSUploadListener uploadListener){
        this.uploadListener = uploadListener;
        return this;
    }

    public AWSTransferUtility setHttpClient(BaseHttpClient httpClient){
        this.httpClient = httpClient;
        return this;
    }

    public void upload(final File file, String bucketName , String fileName){
        if (httpClient==null){
            httpClient = new HttpClient();
        }
        if (uploadListener == null){
            Log.w(TAG,"AWSUploadListener is null.please use \"setUploadListener\" method to set AWSUploadListener,before upload file.");
        }
        String url = Constants.ENDPOINT+"/"+bucketName+"/"+fileName;
        final AWSRequest awsRequest = createAWSRequest(file,url,"PUT");

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                    Log.i("11",awsRequest.toString());

                    if (uploadListener!=null){
                        httpClient.setUploadListener(uploadListener);
                    }
                    httpClient.uploadFile(awsRequest,file);

            }
        });


    }

    private AWSRequest createAWSRequest(File file,String url,String method) {
        String[] sha256md5Hashes;
        AWSRequest mAWSRequest;
        try {
            //发起请求的时间
            long requestTimte = System.currentTimeMillis();
            AWSRequest request =new AWSRequest(url,method,requestTimte);
            long length = file.length();
            //注意：sha256md5Hashes方法会将InputStream清空
            //计算文件的sha256hash和md5hash
            sha256md5Hashes = Digest.msha256md5Hashes(file);
            request.setHeader("Host",new URL(request.getUrl()).getHost());
            request.setHeader("x-amz-content-sha256",sha256md5Hashes[0]);

            Date date = new Date(requestTimte);
            String result = DateUtils.format(DateUtils.COMPRESSED_DATE_PATTERN, date);
            request.setHeader("x-amz-date",result);

            request.setHeader("Content-Length",length+"");
            request.setHeader("Content-Type","application/octet-stream");
            request.setHeader("Content-MD5",sha256md5Hashes[1]);
            mAWSRequest = Signer.signV4(request,Constants.BUCKET_REGION,Constants.ACCESSKEY,Constants.SecretKey);
            return mAWSRequest;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
