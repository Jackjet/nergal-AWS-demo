/*
 * Minio Java SDK for Amazon S3 Compatible Cloud Storage, (C) 2015 Minio, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.nergal.myaws.awss3v4.signaturev4;

import android.util.Log;
import android.webkit.URLUtil;

import com.example.nergal.myaws.awss3v4.AWSRequest;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;




/**
 * Amazon AWS S3 signature V4 signer.
 */
public class Signer {
  //
  // Excerpts from @lsegal - https://github.com/aws/aws-sdk-js/issues/659#issuecomment-120477258
  //
  //  User-Agent:
  //
  //      This is ignored from signing because signing this causes problems with generating pre-signed URLs
  //      (that are executed by other agents) or when customers pass requests through proxies, which may
  //      modify the user-agent.
  //
  //  Content-Length:
  //
  //      This is ignored from signing because generating a pre-signed URL should not provide a content-length
  //      constraint, specifically when vending a S3 pre-signed PUT URL. The corollary to this is that when
  //      sending regular requests (non-pre-signed), the signature contains a checksum of the body, which
  //      implicitly validates the payload length (since changing the number of bytes would change the checksum)
  //      and therefore this header is not valuable in the signature.
  //
  //  Content-Type:
  //
  //      Signing this header causes quite a number of problems in browser environments, where browsers
  //      like to modify and normalize the content-type header in different ways. There is more information
  //      on this in https://github.com/aws/aws-sdk-js/issues/244. Avoiding this field simplifies logic
  //      and reduces the possibility of future bugs
  //
  //  Authorization:
  //
  //      Is skipped for obvious reasons
  //

  private static final Set<String> NEED_HEADERS = new HashSet<>();

  static {
    NEED_HEADERS.add("content-md5");
    NEED_HEADERS.add("host");
    NEED_HEADERS.add("x-amz-content-sha256");
    NEED_HEADERS.add("x-amz-date");
  }

  private AWSRequest request;
  private String contentSha256;
  private String region;
  private String accessKey;
  private String secretKey;
  private String  url;

  private String scope;
  private String canonicalHeaders = "";
  private String signedHeaders;
  private String canonicalQueryString;
  private String canonicalRequest;
  private String canonicalRequestHash;
  private String stringToSign;
  private byte[] signingKey;
  private String signature;
  private String authorization;


  /**
   * Create new Signer object for V4.
   *
   * @param request        HTTP Request object.
   * @param contentSha256  SHA-256 hash of request payload.
   * @param region         Amazon AWS region for the request.
   * @param accessKey      Access Key string.
   * @param secretKey      Secret Key string.
   *
   */
  public Signer(AWSRequest request, String contentSha256, String region, String accessKey,
                String secretKey) {
    this.request = request;
    this.contentSha256 = contentSha256;
    this.url = request.getUrl();
    this.region = region;
    this.accessKey = accessKey;
    this.secretKey = secretKey;
  }
  /**
   * Returns the entire path of this URL, encoded for use in HTTP resource resolution. The
   * returned path is always nonempty and is prefixed with {@code /}.
   */
  public String encodedPath(String url) {
    String scheme =null;
    if (URLUtil.isHttpUrl(url)){
      scheme = "Https";
    }
    int pathStart = url.indexOf('/', scheme.length() + 3); // "://".length() == 3.
    int pathEnd = url.length();
    return url.substring(pathStart, pathEnd);
  }

  /**
   * Returns the query of this URL, encoded for use in HTTP resource resolution. The returned string
   * may be null (for URLs with no query), empty (for URLs with an empty query) or non-empty (all
   * other URLs).
   */
  public String encodedQuery() {
    if (!url.contains("?")) return null; // No query.
    int queryStart = url.indexOf('?') + 1;
    int queryEnd = url.length();
    return url.substring(queryStart, queryEnd);
  }


  private void setScope() {
    this.scope = DateUtils.format("yyyyMMdd", new Date(request.getRequestTime())) + "/" + this.region + "/s3/aws4_request";
  }


  private void setCanonicalHeaders() {
    Set<String> headerNames= request.getHeaders().keySet();
    for (String headerName : headerNames){
      if (NEED_HEADERS.contains(headerName.toLowerCase())) {
        this.canonicalHeaders += headerName.toLowerCase() + ":" + request.getHeaders().get(headerName) + "\n";
      }
    }


//    this.signedHeaders = "host;x-amz-content-sha256;x-amz-date";
    this.signedHeaders = "content-md5;host;x-amz-content-sha256;x-amz-date";
  }


  private void setCanonicalQueryString() {
    String encodedQuery = encodedQuery();
    if (encodedQuery == null) {
      this.canonicalQueryString = "";
      return;
    }else {
      this.canonicalQueryString = encodedQuery;
    }
  }


  /**
   * @author zhangchen
   *
   * @date 2017/5/23 下午3:48
   *
   * @Description
   *
   * @param canonicalRequest
   *
   *样例：
   * PUT
     /test/111.jpg

     content-md5:BLbI9E/k0i1lAfJuSoQtnA==
     host:10.10.101.80:9000
     x-amz-content-sha256:32e820d03db121caf97206f8cbcc6202cf25bf59246e8d6b0e8f6e3502d68f66
     x-amz-date:20170523T071511Z

     content-md5;host;x-amz-content-sha256;x-amz-date
     32e820d03db121caf97206f8cbcc6202cf25bf59246e8d6b0e8f6e3502d68f66
   *
   * @return
   *
   */
  private void setCanonicalRequest() throws NoSuchAlgorithmException {
    setCanonicalHeaders();
    setCanonicalQueryString();

    // CanonicalRequest =
    //   HTTPRequestMethod + '\n' +
    //   CanonicalURI + '\n' +
    //   CanonicalQueryString + '\n' +
    //   CanonicalHeaders + '\n' +
    //   SignedHeaders + '\n' +
    //   HexEncode(Hash(RequestPayload))
    this.canonicalRequest = this.request.getMethod().toUpperCase() + "\n"
      + this.encodedPath(url) + "\n"
      + this.canonicalQueryString + "\n"
      + this.canonicalHeaders+"\n"
      + this.signedHeaders + "\n"
      + this.contentSha256;
    Log.d(TAG,"canonicalRequest:"+canonicalRequest);

    this.canonicalRequestHash = Digest.sha256Hash(this.canonicalRequest);
  }


  private void setStringToSign() {
    Date date = new Date(request.getRequestTime());
    String TimeStamp = DateUtils.format("yyyyMMdd\'T\'HHmmss\'Z\'", date);
    this.stringToSign = "AWS4-HMAC-SHA256" + "\n"
      + TimeStamp + "\n"
      + this.scope + "\n"
      + this.canonicalRequestHash;
  }


  private void setSigningKey() throws NoSuchAlgorithmException, InvalidKeyException {
    String aws4SecretKey = "AWS4" + this.secretKey;

    byte[] dateKey = sumHmac(aws4SecretKey.getBytes(StandardCharsets.UTF_8),
            DateUtils.format("yyyyMMdd", new Date(request.getRequestTime())) .getBytes(StandardCharsets.UTF_8));

    byte[] dateRegionKey = sumHmac(dateKey, this.region.getBytes(StandardCharsets.UTF_8));

    byte[] dateRegionServiceKey = sumHmac(dateRegionKey, "s3".getBytes(StandardCharsets.UTF_8));

    this.signingKey = sumHmac(dateRegionServiceKey, "aws4_request".getBytes(StandardCharsets.UTF_8));
  }

  private String TAG = "Signature";

  private void setSignature() throws NoSuchAlgorithmException, InvalidKeyException {
    Log.d(TAG, "signingKey: "+signingKey);
    Log.d(TAG, "stringToSign: "+stringToSign);
    byte[] digest = sumHmac(this.signingKey, this.stringToSign.getBytes(StandardCharsets.UTF_8));
    this.signature = BinaryUtils.toHex(digest);
    Log.d(TAG, "signature: "+signature);
  }


  private void setAuthorization() {
    this.authorization = "AWS4-HMAC-SHA256 Credential=" + this.accessKey + "/" + this.scope + ", SignedHeaders="
      + this.signedHeaders + ", Signature=" + this.signature;
  }


  /**
   * Returns signed request object for given request, region, access key and secret key.
   */
  public static AWSRequest signV4(AWSRequest request, String region, String accessKey, String secretKey)
    throws NoSuchAlgorithmException, InvalidKeyException {
    String contentSha256 = request.getHeader("x-amz-content-sha256");

    Signer signer = new Signer(request, contentSha256,  region, accessKey, secretKey);
    signer.setScope();
    signer.setCanonicalRequest();
    signer.setStringToSign();
    signer.setSigningKey();
    signer.setSignature();
    signer.setAuthorization();

    return request.setHeader("Authorization", signer.authorization);
  }


  /**
   * Returns HMacSHA256 digest of given key and data.
   */
  public static byte[] sumHmac(byte[] key, byte[] data)
    throws NoSuchAlgorithmException, InvalidKeyException {
    Mac mac = Mac.getInstance("HmacSHA256");

    mac.init(new SecretKeySpec(key, "HmacSHA256"));
    mac.update(data);

    return mac.doFinal();
  }
}
