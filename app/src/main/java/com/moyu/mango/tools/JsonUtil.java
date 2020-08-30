package com.moyu.mango.tools;

import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

public class JsonUtil {
    private String url = "";

    private InputStream getString(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            //获得结果码
            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                //请求成功 获得返回的流
                InputStream is = connection.getInputStream();
                return is;
            }else {
                //请求失败
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getString() {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            //获得结果码
            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                //请求成功 获得返回的流
                InputStream is = connection.getInputStream();
                return is;
            }else {
                //请求失败
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getJson(String url) throws IOException{
        InputStream is = getString(url);
        StringBuilder sb = new StringBuilder();
        String line = null;
        BufferedReader reader=new BufferedReader(new InputStreamReader(is,"utf-8"));
        while((line=reader.readLine())!=null){
            sb.append(line);
        }
        String respText=sb.toString();
        return respText;
    }

    public String getJson() throws IOException{
        InputStream is = getString();
        StringBuilder sb = new StringBuilder();
        String line = null;
        BufferedReader reader=new BufferedReader(new InputStreamReader(is,"utf-8"));
        while((line=reader.readLine())!=null){
            sb.append(line);
        }
        String respText=sb.toString();
        return respText;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getJson2(String url) {
        try{

//            processResponse(
                    return searchRequest(url);
//            );
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public void closeStrictMode() {//不知道干嘛的，但是加上去之后就可以读取到网站的内容了
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
    }

    public String searchRequest(String searchString) throws IOException {//用于获取网站的Json数据

        String newFeed = searchString;
        StringBuilder response = new StringBuilder();

        System.out.println("gsearch url:"+newFeed);
        URL url = new URL(newFeed);
        HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        httpconn.setReadTimeout(10000);
        httpconn.setConnectTimeout(15000);
        httpconn.setRequestMethod("GET");
        httpconn.setDoInput(true);

        httpconn.connect();
        if (httpconn.getResponseCode()==200){//HttpURLConnection.HTTP_OK
            BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()),8192);
            String strLine = null;
            while ((strLine = input.readLine()) != null){
                response.append(strLine);
            }
            input.close();
        }
        return response.toString();
    }


    //--------------------------

    public static String loadJson (String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            URLConnection uc = urlObject.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"utf-8"));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
