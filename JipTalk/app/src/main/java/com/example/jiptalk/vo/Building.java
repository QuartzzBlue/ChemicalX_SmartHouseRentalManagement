package com.example.jiptalk.vo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Building {

    String id;
    String name;
    int unitCnt;
    int occupiedCnt;
    int emptyCnt;
    int paidCnt;
    int unpaidCnt;
    ArrayList<Unit> unitList;


    public Building() {
    }

    public Building(String name, int unitCnt, int occupiedCnt, int emptyCnt, int paidCnt, int unpaidCnt, ArrayList<Unit> unitList) {
        this.name = name;
        this.unitCnt = unitCnt;
        this.occupiedCnt = occupiedCnt;
        this.emptyCnt = emptyCnt;
        this.paidCnt = paidCnt;
        this.unpaidCnt = unpaidCnt;
        this.unitList = unitList;
    }

    public Building(String id, String name, int unitCnt, int occupiedCnt, int emptyCnt, int paidCnt, int unpaidCnt, ArrayList<Unit> unitList) {
        this.id = id;
        this.name = name;
        this.unitCnt = unitCnt;
        this.occupiedCnt = occupiedCnt;
        this.emptyCnt = emptyCnt;
        this.paidCnt = paidCnt;
        this.unpaidCnt = unpaidCnt;
        this.unitList = unitList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnitCnt() {
        return unitCnt;
    }

    public void setUnitCnt(int unitCnt) {
        this.unitCnt = unitCnt;
    }

    public int getOccupiedCnt() {
        return occupiedCnt;
    }

    public void setOccupiedCnt(int occupiedCnt) {
        this.occupiedCnt = occupiedCnt;
    }

    public int getEmptyCnt() {
        return emptyCnt;
    }

    public void setEmptyCnt(int emptyCnt) {
        this.emptyCnt = emptyCnt;
    }

    public int getPaidCnt() {
        return paidCnt;
    }

    public void setPaidCnt(int paidCnt) {
        this.paidCnt = paidCnt;
    }

    public int getUnpaidCnt() {
        return unpaidCnt;
    }

    public void setUnpaidCnt(int unpaidCnt) {
        this.unpaidCnt = unpaidCnt;
    }

    public ArrayList<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(ArrayList<Unit> unitList) {
        this.unitList = unitList;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", unitCnt=" + unitCnt +
                ", occupiedCnt=" + occupiedCnt +
                ", emptyCnt=" + emptyCnt +
                ", paidCnt=" + paidCnt +
                ", unpaidCnt=" + unpaidCnt +
                ", unitList=" + unitList +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("unitCnt", unitCnt);
        result.put("occupiedCnt", occupiedCnt);
        result.put("emptyCnt", emptyCnt);
        result.put("paidCnt", paidCnt);
        result.put("unpaidCnt", unpaidCnt);

        return result;
    }
}
