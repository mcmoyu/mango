package com.moyu.mango.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.moyu.mango.R;
import com.moyu.mango.adapter.MyAdapter;
import com.moyu.mango.object.Detail;
import com.moyu.mango.object.Goods;
import com.moyu.mango.tools.JsonUtil;
import com.moyu.mango.tools.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    /**
     * 备用接口：
     * https://acs.m.taobao.com/h5/mtop.taobao.detail.getdetail/6.0/?data={%22itemNumId%22%3A%22549893090068%22}
     * http://hws.m.taobao.com/cache/wdesc/5.0?id=572920146516&qq-pf-to=pcqq.group
     */

    final String API1 = "https://acs.m.taobao.com/h5/mtop.taobao.detail.getdetail/6.0/?data=%7B\"itemNumId\"%3A\"";
//    final String API1 ="https://acs.m.taobao.com/h5/mtop.taobao.detail.getdetail/6.0/?data=%257B%22itemNumId%22%253A%22";
    final String API2 = "\"%7D&qq-pf-to=pcqq.group";
//    final String API2 = "%22%257D&qq-pf-to=pcqq.group";
    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        LinearLayout top = findViewById(R.id.top);
        top.setPadding(0,StatusBarUtil.getStatusBarHeight(this),0,0);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        Goods goods = (Goods) bundle.getSerializable("goods");
        if(goods != null){
            // Toast.makeText(this, "不为空", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "主图路径：" + goods.getMainPic(), Toast.LENGTH_SHORT).show();
        }
        final String id = goods.getId();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        new Thread(){
            @Override
            public void run(){
                Message msg = Message.obtain();
                try {
//                    if(getData()) {
//                        MyAdapter myAdapter = new MyAdapter(DetailActivity.this, R.layout.layout_item, goodsList);
//                        msg.obj = myAdapter;
//                        handler.sendMessage(msg);
//                    }
                    msg.obj = getData(id);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

//        final String pic_url = bundle.getString("main_pic");
        String nowPrice = goods.getPrice();
//        Bitmap bitmap = goods.getMainPic();
        ImageView main_pic = findViewById(R.id.main_pic);
//        main_pic.setImageBitmap(bitmap);
        String volume = goods.getVolume();
        TextView tv_volume = findViewById(R.id.volume);
        tv_volume.setText("销量：" + volume + "件");
        String shopTitle = goods.getShopTitle();
        TextView tv_shopTitle = findViewById(R.id.shopTitle);
        tv_shopTitle.setText("店铺：" + shopTitle);
        String price = goods.getYuanJia();
        TextView tv_price = findViewById(R.id.price);
        tv_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_price.setText("￥" + price);
        String couponInfo = goods.getCouponInfo();
        TextView tv_couponInfo = findViewById(R.id.couponInfo);
        tv_couponInfo.setText(couponInfo);
        String title = goods.getTitle();
        TextView top_title = findViewById(R.id.top_title);
        TextView tv_title = findViewById(R.id.title);
        top_title.setText(title);
        tv_title.setText(title);
        TextView tv_nowprice = findViewById(R.id.now_price);
        tv_nowprice.setText("￥" + nowPrice);

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        main_pic.setMaxHeight(width);

        web = findViewById(R.id.web);
        WebSettings webSettings = web.getSettings();
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 设置缓存
        webSettings.setJavaScriptEnabled(true); // 设置能够解析Javascript
        webSettings.setDomStorageEnabled(true); // 设置适应Html5 重点是这个设置

        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.finish();
            }
        });

        TextView tv_open = findViewById(R.id.tv_open);
        tv_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTaobao(id);
                Toast.makeText(DetailActivity.this, "正在打开淘宝", Toast.LENGTH_SHORT).show();
            }
        });

        TextView tv_copy = findViewById(R.id.tv_copy);
        tv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTkl(id);
            }
        });
    }

    /**
     * 获取淘口令
     * @param id 商品id
     */
    private void getTkl(String id){
        final String API = "http://api.uiuk.cn/taobao/highTurnByAll.php?apkey=c90dbafa-95f2-e430-8b58-a085740c9e5c&pid=mm_129779402_46770368_109694000456&extsearch=1&tpwd=1&shorturl=1&tbname=mc陌宇&content=" + id;
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                String tkl = (String)msg.obj;
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Tkl", tkl);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(DetailActivity.this, "复制淘口令成功！", Toast.LENGTH_SHORT).show();
            }
        };
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(new JsonUtil().getJson(API));
                    Message message = Message.obtain();
                    message.obj = jsonObject.getJSONObject("data").getString("tpwd");
                    handler.sendMessage(message);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getJson(String id) throws IOException, JSONException {
//        JSONObject jsonObject = new JSONObject(new JsonUtil().getJson(API1 + id + API2));
//        JSONObject jsonData = jsonObject.getJSONObject("data");
//        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        // 创建普通字符型ClipData
//        ClipData mClipData = ClipData.newPlainText("Label", API1 + id + API2);
//        // 将ClipData内容放到系统剪贴板里。
//        cm.setPrimaryClip(mClipData);
//        Toast.makeText(this, "复制内容到剪切板成功！", Toast.LENGTH_SHORT).show();

        //Uri uri = Uri.parse(API1 + id + API2);
        //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//intent.setClassName("com.UCMobile","com.uc.browser.InnerUCMobile");//打开UC浏览器
//        intent.setClassName("com.tencent.mtt","com.tencent.mtt.MainActivity");//打开QQ浏览器
        //startActivity(intent);

        // 根据商品ID获取商品信息

    }

    /**
     * 获取商品详情
     * @param id 商品id
     * @return 商品详情url
     * @throws IOException
     * @throws JSONException
     */
    private String getData(String id) throws IOException, JSONException {
        String data = new JsonUtil().getJson(API1 + id + API2);
        JSONObject jsonObject = new JSONObject(data);
        JSONObject jsonData = jsonObject.getJSONObject("data");
        final String url = "https:" + jsonData.getJSONObject("item").getString("tmallDescUrl");
        final String url1 = "https://www.baidu.com";
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl(url);
                web.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView webView, String url){
                        super.onPageFinished(webView, url);
                        // hide(web);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
            }
        });
        return url;
    }

    private void hide(WebView web){
        String js = "javascript:function hide(){" +
                "var obj = document.getElementsByClassName('hr-box');" +
                "if(obj[0] != null){" +
                "obj[0].parentNode.removeChild(obj[0]);" +
                "}" +
                "}" +
                "hide();";
        web.loadUrl(js);
    }

    private boolean checkPackage(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    private void openTaobao(String id){
        String url = "https://h5.m.taobao.com/awp/core/detail.htm?id=" + id;
        if(checkPackage("com.taobao.taobao")){
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.taobao.taobao");
            intent.setAction("android.intent.action.VIEW");
            intent.setClassName("com.taobao.taobao", "com.taobao.tao.detail.activity.DetailActivity");
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            startActivity(intent);
        } else {
            Toast.makeText(this, "未安装淘宝APP", Toast.LENGTH_SHORT).show();
        }
    }
}
