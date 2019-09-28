package org.lemon;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTest {

    public static void main(String args[]) throws Exception {
        String url = null;
        if (args.length == 1) {
            url = args[0];
        }
        int n = 10000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            testGet(url);
        }

        double cost = (System.currentTimeMillis() - start) / 1000.0;
        double tps = n / cost;
        System.out.println("tps = " + tps + "  cost :" + cost);
    }

    public static void testGet(String url) throws Exception {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.connect();
        InputStream inputStream = httpURLConnection.getInputStream();
        byte buf[] = new byte[1024];
        int n = inputStream.read(buf);
        inputStream.close();
        if (httpURLConnection.getResponseCode() == 200 && n > 0) {
            return;
        }
    }
}
