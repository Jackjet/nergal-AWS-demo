Content-MD5:
    BinaryUtils.toBase64(Md5Utils.computeMD5Hash(bytes))

x-amz-content-sha256:
sha256Hash = Digest.sha256Hash(data, length);

x-amz-date:
 Date date = new Date(System.currentTimeMillis());
 String result = DateUtils.format("yyyyMMdd\'T\'HHmmss\'Z\'", date);



代码实现文件(支持超大文件上传)上传：
MainActivity 106-108行 ：
AWSTransferUtility utility = AWSTransferUtility.getInstance();
//***********文件上传，先设置监听和HttpClient，后上传文件*************若需要用其他网络框架实现上传，请自行实现BaseHttpClient,然后setHttpClient
utility.setHttpClient(new HttpClient()).setUploadListener(new MAWSUploadListener()).upload(file, Constants.BUCKET_NAME, file.getName());
