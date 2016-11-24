package com.example.zhangyulong.myapplication;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    public static List<Artical.Data> mDatas;
    private SwipeRefreshLayout mRefreshLayout;
    private HomeAdapter homeAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static int type = 1;
    private static int pageNum = 1;
    private static int tmpPage = 2;
    private static int tmp = 0;
    protected static final int SUCCESS = 1;
    protected static final int ERROR = 2;
    private static final int EXCEPTION = 4;
    public static int tmpIndex;
    URL url;
    private AlertDialog.Builder builder;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public MainActivity() throws MalformedURLException {
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    String text = (String) msg.obj;
                    Gson gson = new Gson();
                    Artical result = gson.fromJson(text, Artical.class);
                    if (result.getData().get(0).getIndex() != tmp && tmp != 0 && pageNum != 1)
                        mDatas.addAll(result.getData());
                    else {
                        mDatas = result.getData();
                        mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
                        mRecyclerView.setLayoutManager(mLinearLayoutManager);
                        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);



                    }
                    tmp = mDatas.get(0).getIndex();

                    mRecyclerView.addOnScrollListener(new EndLessOnScrollListener(mLinearLayoutManager) {
                        public void onLoadMore(int currentPage) {
                            pageNum = tmpPage;
                            getData();
                            tmpPage++;
                            homeAdapter.notifyDataSetChanged();
                        }
                    });

                    break;
                case ERROR:
                    Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        ;
    };

    //private HomeAdapter mAdapter;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe_refresh);
        mRecyclerView.setAdapter(homeAdapter = new HomeAdapter());
        getData();
        mRefreshLayout.setOnRefreshListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    protected void initData() {

        mDatas = new ArrayList<Artical.Data>();

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onRefresh() {
        int tmp = pageNum;
        pageNum = 1;
        getData();
        pageNum = tmp;
        //数据重新加载完成后，提示数据发生改变，并且设置现在不在刷新
        homeAdapter.notifyDataSetChanged();
        mRefreshLayout.setRefreshing(false);
    }
    public interface ItemClickListener {
        void OnItemClick(View v, int position);
    }
    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {



        ItemClickListener mItemClickListener;
        public void setOnItemClickListener(ItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.activity_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.data  = MainActivity.mDatas.get(position);
            holder.tv.setText(holder.data.getSubject());
            holder.content.setText(holder.data.getSummary());
            final Image image = new Image();
            image.setPath(holder.data.getPic());
            image.setmIvShow(holder.imageView);


            image.setMainActivity(MainActivity.this);



            new Thread() {

                public void run() {
                    image.getImage(holder.data.getPic());
                }

                ;
            }.start();
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return MainActivity.mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tv, content;
            ImageView imageView;
            Artical.Data data;
            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.id_num);
                content = (TextView) view.findViewById(R.id.content);
                imageView = (ImageView) view.findViewById(R.id.imageView2);
                itemView.setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.OnItemClick(v, (Integer) itemView.getTag());



                }
                tmpIndex=data.getIndex();
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,Content.class);
                Bundle bundle=new Bundle();
                bundle.putInt("index",data.getIndex());
                intent.putExtras(bundle);
                startActivity(intent);


            }

        }


    }
    void getData() {
        new Thread() {
            public void run() {
                try {
                    url = new URL("http://open.twtstudio.com/api/v1/news/" + type + "/page/" + pageNum);
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

            ;
        }.start();

    }
}

