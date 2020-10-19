package com.hbouzidi.fiveprayers.ui.dashboard;

import android.content.Intent;

public class DashboardModel {
    private String head, sub;
    private int image;
    private Intent intent;

    public DashboardModel() {
    }

    public DashboardModel(String head, String sub, int image) {
        this.head = head;
        this.sub = sub;
        this.image = image;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
