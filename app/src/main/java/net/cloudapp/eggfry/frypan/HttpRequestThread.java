package net.cloudapp.eggfry.frypan;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by user on 2016-08-07.
 */
public class HttpRequestThread extends AsyncTask<String, String, String> {

    // 기본 변수
    private String address; // 최종 주소

    public HttpResponse httpResponse = null;
    public String userName;
    public String password;

    // 기본 생성자
    public HttpRequestThread() {
        super();
    }

    // 사용자 지정 생성자
    public HttpRequestThread(String address) {
        this.address = address;
    }

    // 전처리기
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // 본 작업
    @Override
    protected String doInBackground(String... params) {
        String responseString = address+"\n"+userName+"\n"+password;
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){ // HTTP OK 응답 받았을 때
                InputStream in = new BufferedInputStream(conn.getInputStream()); // InputStream 가져오기
                responseString += "\n"+InputToString(in); // String으로 변환
            }
            else {
                responseString += "\nFail"; // Error라고 띄움
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseString += "\nFail";
        }
        return responseString;
    }

    /*
     * Parameters
        - Is : InputStream
     * Function
        - InputStream을 String으로 바꿈
     * Return
        - String : InputStream을 String으로 바꾼 값
     */
    public String InputToString(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    // 응답이 완료되었을 때
    // 응답에 따라 분기
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        httpResponse.processFinish(s);
    }
}
