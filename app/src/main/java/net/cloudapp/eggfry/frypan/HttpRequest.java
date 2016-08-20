package net.cloudapp.eggfry.frypan;

import java.util.Map;

/**
 * Created by user on 2016-08-07.
 */
public class HttpRequest {

    // 기본 생성자

    public HttpRequest() { }

    private String userName;
    private String password;

    public HttpResponse httpResponse = null;

    /*
     * Parameters
        - address : 최종 주소
     * Function
        - HttpRequestThread를 호출함
     * Return
        - boolean : 성공하면 true, 실패하면 False
     */
    public boolean getRequest(String address) {
        HttpRequestThread httpRequestThread = new HttpRequestThread(address);
        httpRequestThread.httpResponse = this.httpResponse;
        httpRequestThread.userName = this.userName;
        httpRequestThread.password = this.password;
        httpRequestThread.execute();
        return true;
    }

    /*
     * Parameters
        - address : 사이트 주소
        - map : 파라미터가 (key, value)로 매핑되어있는 맵
     * Function
        - 사이트 주소와 파라미터를 섞어서 최종 주소로 만들어줌
     * Return
        - String : 최종 주소
     */

    public String makeUrl(String address, Map<String, String> map) {
        StringBuilder sb = new StringBuilder(address);
        sb.append("/");
        sb.append(map.get("fileName"));
        sb.append("?");
        for( String key : map.keySet()){
            if(key.equals("fileName")) {
                continue;
            }
            sb.append(key)
                    .append("=")
                    .append(map.get(key))
                    .append("&");
        }
        this.userName = map.get("UserName");
        this.password = map.get("Password");
        return sb.toString();
    }
}
