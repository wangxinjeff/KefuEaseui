package com.kefueaseui.kefueaseui.ui;

import android.os.Bundle;
import android.view.View;

import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.kefueaseui.kefueaseui.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by magic on 2018/1/29.
 */

public class GetLogActivity extends BaseActivity{

    private String organme = "easemob-demo";
    private String appname = "chatdemoui";
    private String username = "walj";
    private String token = "YWMtmPpRmt5ZEeedV_961BhnFQAAAAAAAAAAAAAAAAAAAAFe2JYa1n8R45heowo6U5LUAQMAAAFgRQFecABPGgD7v50BoYYFAfDgr3XCGSgm9zdQGNyIseJjXBswqJQYSQ";
    private String host = "a1";
    private String getStatus = "http://%s.easemob.com/%s/%s/users/%s/status";
    private String sendMessage = "http://%s.easemob.com/%s/%s/messages ";
    private String checkLog = "http://%s.easemob.com/easemob/logger/devicelogs?ql=select  * where login_username='%s'";
    private String downloadLog = "http://%s.easemob.com/easemob/logger/chatfiles/%s";
    private JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlog);
        initView();
    }

    private void initView() {
        try {
            json = new JSONObject();
            json.put("target_type","users");
            json.put("target",new JSONArray().put(0,username));
            JSONObject msg = new JSONObject();
            msg.put("type","cmd");
            msg.put("action","em_upload_log");
            json.put("msg",msg);
            json.put("from","admin");
            $(R.id.request).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            request(String.format(sendMessage, host, organme, appname), "POST", json.toString());
                        }
                    }).start();

                }
            });
        }catch(Exception e){}
    }

    private String request(String url,String method,String body){
        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;   //读
        int responseCode = 0;    //远程主机响应的HTTP状态码
        String result="";
        try{
            // 创建一个URL对象
            URL mURL = new URL(url);
            // 调用URL的openConnection()方法,获取HttpURLConnection对象
            conn = (HttpURLConnection) mURL.openConnection();
            if(method.equals("POST")) {
                conn.setRequestMethod("POST");// 设置请求方法为post
                //设置post请求必要的请求头
                conn.setRequestProperty("Content-Type", "application/json"); // 请求头, 必须设置
                conn.setRequestProperty("Authorization", "Bearer "+token); // 注意是字节长度, 不是字符长度
                conn.setReadTimeout(5000);// 设置读取超时为5秒
                conn.setConnectTimeout(10000);// 设置连接网络超时为10秒
                conn.setDoOutput(true);// 设置此方法,允许向服务器输出内容
                conn.setDoInput(true);
//                conn.getOutputStream().write(body.getBytes());
                //设定参数
//                conn.connect();
                out = conn.getOutputStream();
                out.write(body.getBytes());
                out.flush(); //清空缓冲区,发送数据
                out.close();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader input = new InputStreamReader(
                            conn.getInputStream()); // 获得读取的内容
                    BufferedReader buffer = new BufferedReader(input); // 获取输入流对象
                    String inputLine = null;
                    while ((inputLine = buffer.readLine()) != null) {
                        result += inputLine + "\n";
                    }
                    in.close(); //关闭字符输入流
                }
                conn.disconnect();   //断开连接
            }


        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
