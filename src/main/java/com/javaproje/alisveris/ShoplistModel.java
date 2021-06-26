package com.javaproje.alisveris;

public class ShoplistModel {
    private String alisverisadi;
    private String alisveristarihi;
    private String alisverisyeri;

    public void setAlisverisadi(String alisverisadi) {
        this.alisverisadi = alisverisadi;
    }

    public String getAlisveristarihi() {
        return alisveristarihi;
    }

    public void setAlisveristarihi(String alisveristarihi) {
        this.alisveristarihi = alisveristarihi;
    }

    public String getAlisverisyeri() {
        return alisverisyeri;
    }

    public void setAlisverisyeri(String alisverisyeri) {
        this.alisverisyeri = alisverisyeri;
    }

    public ShoplistModel(String alisverisadi, String alisveristarihi, String alisverisyeri) {
        this.alisverisadi = alisverisadi;
        this.alisveristarihi = alisveristarihi;
        this.alisverisyeri = alisverisyeri;
    }
    public ShoplistModel() {}
    public String getAlisverisadi() {
        return alisverisadi;
    }
}