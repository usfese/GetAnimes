package io.githuhb.usfese;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetter {
    public static String doGet(String httpUrl) throws IOException {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            //创建连接
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置连接超时时间
            connection.setReadTimeout(7000);
            connection.setRequestProperty("User-agent", "usfese/GetAnimes (https://github.com/usfese/GetAnimes)");
            //开始连接
            connection.connect();
            //获取响应数据
            if (connection.getResponseCode() == 200) {
                //获取返回的数据
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw e;
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw e;
                }
            }
            //关闭远程连接
            connection.disconnect();
        }
        return result.toString();
    }
}
