package com.example.nergal.myaws.awss3v4;

import java.io.File;

/**
 * Created by nergal on 2017/5/24.
 */

public interface BaseHttpClient {
    abstract void setUploadListener(AWSUploadListener uploadListener);
    abstract void uploadFile(AWSRequest request, File file);
}
