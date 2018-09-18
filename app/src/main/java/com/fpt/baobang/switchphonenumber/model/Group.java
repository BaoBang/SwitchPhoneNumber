package com.fpt.baobang.switchphonenumber.model;

import java.util.HashMap;

public class Group {
    private int count;
    private HashMap<String, String> map = new HashMap<>();

    public Group(int count, HashMap<String, String> map) {
        this.count = count;
        this.map = map;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }
}
