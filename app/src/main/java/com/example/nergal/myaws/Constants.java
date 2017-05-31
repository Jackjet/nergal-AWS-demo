

package com.example.nergal.myaws;

/**
 * @author zhangchen
 *
 * @date 2017/5/23 下午5:15
 *
 * @Description aws s3 需要的一些访问文件服务器的参数
 *
 */
public class Constants {

    /*文件服务器地址*/
    public static final String ENDPOINT = "http://10.10.101.80:9000";
//    public static final String ENDPOINT = "http://fsst.anbanggroup.com/";

    /*文件服务器访问时，登陆  access key */
    public static final String ACCESSKEY = "1NA5K80UU85NMPK4BPEW";

    /*文件服务器访问时，登陆  secret key */
    public static final String SecretKey = "yIK7ewMumZ88e6KDb6nBOE4C5k9uNShlbCNWnN8X";

    /*
     * 注意：在上传之前，你必须首先创建bucket,bucket的名字如下
     */
//    public static final String BUCKET_NAME = "201705";
    public static final String BUCKET_NAME = "test";

    public static final String BUCKET_REGION = "us-east-1";//bucket 的区域，一般默认为us-east-1

}
