package com.example.zhangyulong.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Content extends AppCompatActivity {
    private TextView title,newscome;
    private WebView content;
    protected static final int SUCCESS = 1;
    protected static final int ERROR = 2;
    private static final int EXCEPTION = 4;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    String text = (String) msg.obj;
                    Gson gson = new Gson();
                    News result = gson.fromJson(text, News.class);
                    title=(TextView)findViewById(R.id.title);
                    newscome=(TextView)findViewById(R.id.newscome);
                    content=(WebView)findViewById(R.id.content);
                    title.setText(result.getData().getSubject());
                    newscome.setText("来源："+result.getData().getNewscome()+"\n"+"供稿："+result.getData().getGonggao()+"  审稿："+
                    result.getData().getShengao()+"  摄影:"+result.getData().getSheying());
                    String html="<style=\"background-color:\"#EEEEEE\"></style>"+result.getData().getContent();
                    content.loadDataWithBaseURL(null,html,"text/html", "utf-8",
                            null);


                    break;
                case ERROR:
                    Toast.makeText(Content.this, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        getData();



    }

    void getData() {
        new Thread() {
            public void run() {
                try {
                    int index=MainActivity.tmpIndex;
                   URL url = new URL("http://open.twtstudio.com/api/v1/news/" +index);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");//声明请求方式 默认get
                    //conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.3.3; zh-cn; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 MicroMessenger/6.0.0.57_r870003.501 NetType/internet");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        Scanner scanner = new Scanner(is, "UTF-8");
                        String result = scanner.useDelimiter("\\A").next();

                        Message msg = Message.obtain();//减少消息创建的数量
                        msg.obj = result;
                        msg.what = SUCCESS;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = Message.obtain();//减少消息创建的数量
                    msg.what = ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }

        }.start();

    }

}
