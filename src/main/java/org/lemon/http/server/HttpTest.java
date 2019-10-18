package org.lemon.http.server;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpTest {

    private static AtomicInteger counter = new AtomicInteger();

    public static void main(String args[]) throws Exception {

        //int c = \u005Cu001F;

        System.out.println(Character.isISOControl(0));

        String url = "http://127.0.0.1:8080";
        if (args.length == 1) {
            url = args[0];
        }
        int nThread = 16;
        if (args.length == 2) {
            nThread = Integer.valueOf(args[1]);
        }

        Worker workers[] = new Worker[nThread];
        CountDownLatch latch = new CountDownLatch(nThread);
        for (int i = 0; i < nThread; i++) {
            workers[i] = new Worker(url, latch);
            workers[i].start();
            latch.countDown();
        }


        long start = System.currentTimeMillis();
        while (true) {
            Thread.sleep(5000L);
            double cost = (System.currentTimeMillis() - start) / 1000.0;
            double tps = counter.get() / cost;
            System.out.println("tps = " + tps + "  cost :" + cost);
        }
    }

    private static class Worker extends Thread {
        String url;
        CountDownLatch countDownLatch;
        byte buf[] = new byte[1024];

        public Worker(String url, CountDownLatch countDownLatch) {
            this.url = url;
            this.countDownLatch = countDownLatch;
        }

        public void run() {

            try {
                countDownLatch.await();
                while (true) {
                    counter.incrementAndGet();
                    testGet();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void testGet() throws Exception {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            int n = inputStream.read(buf);
            inputStream.close();
            if (httpURLConnection.getResponseCode() == 200 && n > 0) {
                return;
            } else {
                throw new RuntimeException("get error");
            }
        }
    }


}
