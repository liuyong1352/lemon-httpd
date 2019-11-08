package org.lemon.http.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpTest {

    private static AtomicInteger counter = new AtomicInteger();
    private static boolean one = true;

    public static void main(String args[]) throws Exception {
        String url = "http://127.0.0.1:8080";
        if (args.length == 1) {
            url = args[0];
        }
        int nThread = 32;
        if (args.length == 2) {
            nThread = Integer.valueOf(args[1]);
        }

        boolean testGet = false;
        nThread = one ? 1 : nThread;
        Worker workers[] = new Worker[nThread];
        CountDownLatch latch = new CountDownLatch(nThread);

        for (int i = 0; i < nThread; i++) {
            TestCase testCase;
            if (testGet) {
                testCase = new GetTest(url);
            } else {
                testCase = new PostTest(url);
            }
            workers[i] = new Worker(testCase, latch);

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
        TestCase testCase;
        CountDownLatch countDownLatch;

        public Worker(TestCase testCase, CountDownLatch countDownLatch) {
            this.testCase = testCase;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {

            try {
                countDownLatch.await();
                while (true) {
                    counter.incrementAndGet();
                    testCase.test();
                    if (one) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    static abstract class TestCase {
        String url;
        byte buf[] = new byte[1024];

        public TestCase(String url) {
            this.url = url;
        }

        abstract void test() throws Exception;
    }

    static class GetTest extends TestCase {
        public GetTest(String url) {
            super(url);
        }

        @Override
        void test() throws Exception {
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

    static class PostTest extends TestCase {

        public PostTest(String url) {
            super(url);
        }

        @Override
        void test() throws Exception{
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // 设置请求方式
            OutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write("hello post !".getBytes());
                outputStream.flush();
                inputStream = httpURLConnection.getInputStream();
                int n = inputStream.read(buf);
                System.out.println(new String(buf,0,n));
                if (httpURLConnection.getResponseCode() == 200 && n > 0) {
                    return;
                } else {
                    throw new RuntimeException("get error");
                }
            } catch (IOException e) {
                throw e;
            } finally {
                outputStream.close();
                inputStream.close();
            }
        }
    }
}
