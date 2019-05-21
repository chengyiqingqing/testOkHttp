package com.sww.testokhttp;
import android.util.Log;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;
public class testOkHttp {

//    private static final String TAG = "testOkHttp";

    /**
     * 同步；
     */
    @Test
    public void testGet(){
        //1.创建HttpClient;
        OkHttpClient client = new OkHttpClient();
        //2.创建HttpRequest;
        Request request= new Request.Builder()
                .url("http://httpbin.org/get?id=sww&sww=false")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
//            Log.e(TAG, "testGet: "+response.body().toString() );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPost(){
        //1.创建HttpClient;
        OkHttpClient client = new OkHttpClient();
        //2.创建HttpRequest;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType,"{\"sww\":\"haha\"}");
        Request request= new Request.Builder()
                .url("http://httpbin.org/post")
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
//            Log.e(TAG, "testGet: "+response.body().toString() );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAsyncPost(){
        //1.创建HttpClient;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
        //2.创建HttpRequest;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType,"{\"sww\":\"hahswwww\"}");
        Request request= new Request.Builder()
                .url("http://httpbin.org/post")
                .post(requestBody)
                .build();
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("fail");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println("chengle");
                    System.out.println(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常");
        }
    }

    /**
     *  测试拦截器
     */
    @Test
    public void testInterceptor() {
        //  定义拦截器
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                long start = System.currentTimeMillis();
                System.out.println("interceptor: 开始 = " + (start));
                Request request  = chain.request();
                Response response = chain.proceed(request);
                long end = System.currentTimeMillis();
                System.out.println("interceptor: cost time = " + (end - start));
                return response;
            }
        };
        // 创建 OkHttpClient 对象
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        // 创建 Request 对象
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        // OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  测试缓存
     */
    @Test
    public void testCache() {
        // 创建缓存对象
        Cache cache = new Cache(new File("cache.cache"), 1024 * 1024);
        // 创建 OkHttpClient 对象
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        // 创建 Request 对象
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
//                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        // OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            Response responseCache = response.cacheResponse();
            Response responseNet = response.networkResponse();
            if (responseCache != null) {
                // 从缓存响应
                System.out.println("response from cache"+response.body().string());
            }
            if (responseNet != null) {
                // 从缓存响应
                System.out.println("response from net"+response.body().string());
            }
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
