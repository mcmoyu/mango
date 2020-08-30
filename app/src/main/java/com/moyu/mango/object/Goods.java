package com.moyu.mango.object;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Goods implements Serializable {
//    private Bitmap mainPic; // 商品主图
    private String mainPic; // 主图路径
    private String pic_url; // 商品主图链接
    private String title; // 标题
    private int coupon; // 优惠券面额
    private String volume; // 销量
    private int remainNum; // 剩余优惠券数量
    private int totalNum; // 优惠券总数量
    private String price; // 最终价格
    private String yuanJia; // 原价
    private int rate; // 佣金比率
    private String id; // 商品id，用来获取详情数据
    private String shopTitle; // 发货地
    private String couponInfo; // 优惠券信息

    // Setter

//    public void setMainPic(Bitmap mainPicId) {
//        this.mainPic = mainPicId;
//    }


    public void setMainPic(String mainPic) {
        this.mainPic = mainPic;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setRemainNum(int remainNum) {
        this.remainNum = remainNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setYuanJia(String yuanJia) {
        this.yuanJia = yuanJia;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setShopTitle(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    public void setCouponInfo(String couponInfo) {
        this.couponInfo = couponInfo;
    }

    // Getter

//    public Bitmap getMainPic() {
//        return mainPic;
//    }


    public String getMainPic() {
        return mainPic;
    }

    public String getPic_url() {
        return pic_url;
    }

    public String getTitle() {
        return title;
    }

    public int getCoupon() {
        return coupon;
    }

    public String getVolume() {
        return volume;
    }

    public int getRemainNum() {
        return remainNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public String getPrice() {
        return price;
    }

    public String getYuanJia() {
        return yuanJia;
    }

    public int getRate() {
        return rate;
    }

    public String getId() {
        return id;
    }

    public String getShopTitle() {
        return shopTitle;
    }

    public String getCouponInfo() {
        return couponInfo;
    }
}