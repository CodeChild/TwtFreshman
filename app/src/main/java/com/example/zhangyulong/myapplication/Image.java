package com.example.zhangyulong.myapplication;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by zhangyulong on 16-10-15.
 */

public class Image {
    private static final int MSG_NEW_PIC = 2;
    private static final int MSG_CACHE_PIC = 1;
    private static final int ERROR = 3;
    private static final int EXCEPTION = 4;
    private ImageView mIvShow;
    private Drawable drawable;
    public Activity MainActivity;
    private String path;

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private Handler handler = new Handler() {
        //处理消息的
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CACHE_PIC:
                    //3.处理消息 运行在主线程
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if(mIvShow!=null)
                        mIvShow.setImageBitmap(bitmap);
                    System.out.println("(不用下载)缓存图片");
                    drawable=new BitmapDrawable(bitmap);
                    break;
                case MSG_NEW_PIC:
                    Bitmap bitmap2 = (Bitmap) msg.obj;
                    if(mIvShow!=null)
                        mIvShow.setImageBitmap(bitmap2);
                    System.out.println("新下载(还没有缓存)下载的图片");
                    drawable=new BitmapDrawable(bitmap2);
                    break;
                case ERROR:
                    Toast.makeText(MainActivity, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
                case EXCEPTION:
                    Toast.makeText(MainActivity, "发生异常，请求失败", Toast.LENGTH_SHORT).show();
                    break;
            }

            super.handleMessage(msg);
        }
    };
    public void excute(){

    }

    public void getImage(String path) {
        File file = new File(MainActivity.getCacheDir(), Base64.encodeToString(
                path.getBytes(), Base64.DEFAULT));
        if (file.exists() && file.length() > 0) {
            System.out.println("图片存在，拿缓存");
            Bitmap bitmap = BitmapFactory.decodeFile(file
                    .getAbsolutePath());
            //更新ui 不能写在子线程
            //iv.setImageBitmap(bitmap);
            Message msg = new Message();//声明消息
            msg.what = MSG_CACHE_PIC;
            msg.obj = bitmap;//设置数据
            handler.sendMessage(msg);//让handler帮我们发送数据
        } else {
            System.out.println("图片不存在，获取数据生成缓存");
            // 通过http请求把图片获取下来。
            try {
                // 1.声明访问的路径， url 网络资源 http ftp rtsp
                URL url = new URL(path);
                // 2.通过路径得到一个连接 http的连接
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                // 3.判断服务器给我们返回的状态信息。
                // 200 成功 302 从定向 404资源没找到 5xx 服务器内部错误
                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream is = conn.getInputStream();// png的图片
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                    Bitmap bitmap = BitmapFactory.decodeFile(file
                            .getAbsolutePath());
                    //更新ui ，不能写在子线程
                    Message msg = new Message();
                    msg.obj = bitmap;
                    msg.what = MSG_NEW_PIC;
                    handler.sendMessage(msg);
                    //iv.setImageBitmap(bitmap);
                } else {
                    // 请求失败
                    //土司更新ui，不能写在子线程
                    //Toast.makeText(this, "请求失败", 0).show();
                    Message msg = new Message();
                    msg.what = ERROR;
                    handler.sendMessage(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //土司不能写在子线程
                //Toast.makeText(this, "发生异常，请求失败", 0).show();
                Message msg = new Message();
                msg.what = EXCEPTION;
                handler.sendMessage(msg);
            }

        }

    }

    public void setMainActivity(Activity mainActivity) {
        MainActivity = mainActivity;
    }

    public void setmIvShow(ImageView mIvShow) {
        this.mIvShow = mIvShow;
    }
}