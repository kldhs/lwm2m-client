package com.abupdate.iot.lwm2mClient.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @program: iot-cloud-ota-coap
 * @description: Code2工具类
 * @author: miaomingwei
 * @create: 2020-09-04 12:26
 */
public class Codec2 {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * MAC算法可选以下多种算法
     * <pre>
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    public static final String KEY_MAC = "HmacMD5";

    public static String encodeURL(String data) {
        if (Strings.isNullOrEmpty(data)) {
            return data;
        }

        try {
            return URLEncoder.encode(data, DEFAULT_CHARSET.displayName());
        } catch (UnsupportedEncodingException wontHappen) {
            throw new IllegalStateException(wontHappen);
        }
    }

    public static String getMd5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成一个四位数，包括字母
     *
     * @return
     */
    public static String getRandomFourNum() {
        String[] beforeShuffle = new String[]{"0", "1", "2", "3", "4", "5", "6", "7",
                "8", "9", "a", "A", "b", "B", "c", "C", "d", "D", "e", "E", "f", "F", "g", "G", "h", "H", "i", "I", "j", "J",
                "k", "K", "l", "L", "m", "M", "n", "N", "o", "O", "p", "P", "q", "Q", "r", "R", "s", "S", "t", "T", "u", "U", "v", "V",
                "w", "W", "x", "X", "y", "Y", "z", "Z"};
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(5, 9);
        return result;
    }

    public static void main(String[] args) {
//        try {
//        	FileWriter writer = new FileWriter("E:\\mid.txt");
//        	for (int i = 0; i < 5000; i++) {
//        		Long str = System.currentTimeMillis();
//        		writer.write(getMd5(str.toString()).substring(8, 24) + "," + Long.toHexString(str) + "\n");
//        		Thread.sleep(10);
//			}
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//			e.printStackTrace();
//		}

        System.out.println(hexString("testlwm2mclient15154890871514766964", "515fe599b8784dadb57a83d0f66de456"));
    }

    public static String decodeURL(String data) {
        if (Strings.isNullOrEmpty(data)) {
            return data;
        }
        try {
            return URLDecoder.decode(data, DEFAULT_CHARSET.displayName());
        } catch (UnsupportedEncodingException wontHappen) {
            throw new IllegalStateException(wontHappen);
        }
    }

    public static String encodeBase64(String str) {
        Preconditions.checkNotNull(str, "str参数应不为空");
        return new String(Base64.encodeBase64(str.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
    }

    public static String encodeBase64(byte[] data) {
        Preconditions.checkNotNull(data, "data参数应不为空");
        return new String(Base64.encodeBase64(data), DEFAULT_CHARSET);
    }

    public static String dencodeBase64(String str) {
        Preconditions.checkNotNull(str, "str参数应不为空");
        return new String(Base64.decodeBase64(str.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
    }

    public static String base64HmacSHA1(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            mac.init(spec);
            return encodeBase64(mac.doFinal(data.getBytes()));
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String base64HmacSHA1ForMap(TreeMap<String, String> map, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            mac.init(spec);

            Collection<String> keys = map.keySet();
            Iterator<String> it = keys.iterator();
            String str = "";
            while (it.hasNext()) {
                String key = it.next();
                str += key + "=" + map.get(key) + "&";
            }
            int index = str.lastIndexOf("&");
            str = str.substring(0, index);
            return encodeBase64(mac.doFinal(str.getBytes()));
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Build an hexadecimal MD5 hash for a String
     *
     * @param value The String to hash
     * @return An hexadecimal Hash
     */
    public static String hexMD5(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(value.getBytes("utf-8"));
            byte[] digest = messageDigest.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Build an hexadecimal SHA1 hash for a String
     *
     * @param value The String to hash
     * @return An hexadecimal Hash
     */
    public static String hexSHA1(String value) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(value.getBytes("utf-8"));
            byte[] digest = md.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Write a byte array as hexadecimal String.
     */
    public static String byteToHexString(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }

    /**
     * Transform an hexadecimal String to a byte array.
     */
    public static byte[] hexStringToByte(String hexString) {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hexHMAC(String secret, String data) {
        String HMAC_SHA1_ALGORITHM = "HmacSHA256";
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            String result = new String(Codec2.byteToHexString(rawHmac));
            return result;
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * HMAC加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);
    }

    /*签名认证*/
    public static String hexString(String key, String value) {
        try {
            return byteToHexString(encryptHMAC(key.getBytes(), value)).toString();
        } catch (Exception e) {
            /*throw new RuntimeException(e);*/
            e.printStackTrace();
            return "error";
        }
    }

    /*ProductId, 时间戳（毫秒）,三位随机数（0-9999）*/
    public static String productHEX(Long productId) {
        Long timestamp = System.currentTimeMillis();/*获取时间戳*/
        Integer random = new Random().nextInt(10000);
        Long hexLong = (productId * 100000000) + (timestamp * 10000) + random;
        return Long.toHexString(hexLong) + getRandomFourNum();
    }
}