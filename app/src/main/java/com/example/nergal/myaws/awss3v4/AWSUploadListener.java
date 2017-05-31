package com.example.nergal.myaws.awss3v4;

import java.io.File;

/**
 * Created by nergal on 2017/5/23.
 */

public interface AWSUploadListener {
    abstract void onComplite(File file);
    abstract void onError(File file,Throwable e);
    abstract void onProgressChanged(File file, long bytesCurrent, long bytesTotal);
}
