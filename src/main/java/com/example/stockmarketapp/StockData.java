package com.example.stockmarketapp;

import android.content.Intent;

public class StockData {
   // Date,Open,High,Low,Close,Adj Close,Volume
    private  String  date;
    private float open;
    private float high;
    private float low;
    private float close;
    private float adj_close;
    private int volume;


    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }





    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getAdj_close() {
        return adj_close;
    }

    public void setAdj_close(float adj_close) {
        this.adj_close = adj_close;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
