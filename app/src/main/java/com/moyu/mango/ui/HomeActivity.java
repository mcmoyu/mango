package com.moyu.mango.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.moyu.mango.R;
import com.moyu.mango.adapter.MyAdapter;
import com.moyu.mango.object.Goods;
import com.moyu.mango.tools.JsonUtil;
import com.moyu.mango.tools.StatusBarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    static HomeActivity homeActivity;
    private List<Goods> goodsList = new ArrayList<>();
    final String API = "http://api.uiuk.cn/taobao/getItemList.php?apkey=c90dbafa-95f2-e430-8b58-a085740c9e5c&page=1&pagesize=10&itemtype=tqg";

    public static Activity getHomeActivity() {
        return homeActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LinearLayout root = findViewById(R.id.root);
        root.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        homeActivity=this;

        final ListView lv = findViewById(R.id.lv);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    lv.setAdapter((ListAdapter) msg.obj);
                }
            }
        };
        new Thread(){
            @Override
            public void run(){
                Message msg = Message.obtain();
                msg.what = 0x11;
                try {
                    if(getData()) {
                        MyAdapter myAdapter = new MyAdapter(HomeActivity.this, R.layout.layout_item, goodsList);
                        msg.obj = myAdapter;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
//                bundle.putString("id",goodsList.get(position).getId());
//                bundle.putString("title",goodsList.get(position).getTitle());
//                bundle.putByteArray("mainPic",Bitmap2Bytes(goodsList.get(position).getMainPic()));
//                bundle.putString("quanhoujia",goodsList.get(position).getPrice());
//                bundle.putString("volume",goodsList.get(position).getVolume()); // 销量
//                bundle.putString("shopTitle",goodsList.get(position).getShopTitle()); // 店铺标题
//                bundle.putString("price",goodsList.get(position).getYuanJia()); // 原价
//                bundle.putString("couponInfo",goodsList.get(position).getCouponInfo()); // 优惠券信息
                bundle.putSerializable("goods",goodsList.get(position));
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
           // key = value
    }

    private byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public boolean getData() throws IOException, JSONException {
        // 获取数据
        JSONObject js = new JSONObject(new JsonUtil().getJson(API));
        JSONArray ja = js.getJSONArray("data");
        JSONObject json;
        for(int i = 0; i < ja.length(); i++) {
            json = new JSONObject(ja.get(i) + "");
            Goods goods = new Goods();
            // 获取商品id
            goods.setId(json.getString("num_iid"));
            // 获取主图
//            goods.setMainPic(getURLimage(json.getString("pict_url")));
            String path = "/storage/emulated/0/Pictures/" + goods.getId() + ".jpg";
            addPictureToAlbum(HomeActivity.getHomeActivity(),getURLimage(json.getString("pict_url")),path);
            goods.setMainPic(path);
            // 主图链接
            goods.setPic_url(json.getString("pict_url"));
            // 获取标题
            goods.setTitle(json.getString("title"));
            // 获取优惠券
            goods.setCoupon(json.getInt("youhuiquan"));
            // 获取销量
            goods.setVolume(json.getString("volume"));
            // 获取优惠券剩余
            goods.setRemainNum(json.getInt("coupon_remain_count"));
            // 获取优惠券总数
            goods.setTotalNum(json.getInt("coupon_total_count"));
            // 获取券后价
            goods.setPrice(json.getString("quanhoujia"));
            // 获取原价
            goods.setYuanJia(json.getString("zk_final_price"));
            // 获取佣金比率
            goods.setRate(json.getInt("commission_rate"));
            // 获取店铺名称
            goods.setShopTitle(json.getString("shop_title"));
            // 获取优惠券信息
            goods.setCouponInfo(json.getString("coupon_info"));
            // 添加到List里面
            goodsList.add(goods);
        }
        return true;
    }

    public boolean addPictureToAlbum(Context context, Bitmap bitmap, String fileName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        OutputStream outputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //加载图片

    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);//读取图像数据
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
