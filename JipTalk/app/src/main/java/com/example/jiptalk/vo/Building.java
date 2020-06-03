package com.example.jiptalk.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Building {

    String id;
    String name;
    String buildingAddress;
    int unitCnt;
    int occupiedCnt;
    int emptyCnt;
    int paidCnt;
    int unpaidCnt;
    int monthlyIncome;
    HashMap<String, Unit> unitList;

    public Building() {
    }

    public Building(String name, String buildingAddress, int unitCnt) {
        this.name = name;
        this.buildingAddress = buildingAddress;
        this.unitCnt = unitCnt;
    }

    public Building(String name, String buildingAddress, int unitCnt, int occupiedCnt, int emptyCnt, int paidCnt, int unpaidCnt, int monthlyIncome, HashMap<String, Unit> unitList) {
        this.name = name;
        this.buildingAddress = buildingAddress;
        this.unitCnt = unitCnt;
        this.occupiedCnt = occupiedCnt;
        this.emptyCnt = emptyCnt;
        this.paidCnt = paidCnt;
        this.unpaidCnt = unpaidCnt;
        this.monthlyIncome = monthlyIncome;
        this.unitList = unitList;
    }

    public Building(String id, String name, String buildingAddress, int unitCnt, int occupiedCnt, int emptyCnt, int paidCnt, int unpaidCnt, int monthlyIncome, HashMap<String, Unit> unitList) {
        this.id = id;
        this.name = name;
        this.buildingAddress = buildingAddress;
        this.unitCnt = unitCnt;
        this.occupiedCnt = occupiedCnt;
        this.emptyCnt = emptyCnt;
        this.paidCnt = paidCnt;
        this.unpaidCnt = unpaidCnt;
        this.monthlyIncome = monthlyIncome;
        this.unitList = unitList;
    }

    protected Building(Parcel in) {
        id = in.readString();
        name = in.readString();
        buildingAddress = in.readString();
        unitCnt = in.readInt();
        occupiedCnt = in.readInt();
        emptyCnt = in.readInt();
        paidCnt = in.readInt();
        unpaidCnt = in.readInt();
        monthlyIncome = in.readInt();
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

    public String getBuildingAddress() {
        return buildingAddress;
    }

    public void setBuildingAddress(String buildingAddress) {
        this.buildingAddress = buildingAddress;
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
    public int getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(int monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public HashMap<String, Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(HashMap<String, Unit> unitList) {
        this.unitList = unitList;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", buildingAddress='" + buildingAddress + '\'' +

                ", unitCnt=" + unitCnt +
                ", occupiedCnt=" + occupiedCnt +
                ", emptyCnt=" + emptyCnt +
                ", paidCnt=" + paidCnt +
                ", unpaidCnt=" + unpaidCnt +
                ", monthlyIncome=" + monthlyIncome +

                ", unitList=" + unitList +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("buildingAddress",buildingAddress);
        result.put("unitCnt", unitCnt);
        result.put("occupiedCnt", occupiedCnt);
        result.put("emptyCnt", emptyCnt);
        result.put("paidCnt", paidCnt);
        result.put("unpaidCnt", unpaidCnt);
        result.put("monthlyIncome",monthlyIncome);
        result.put("unitlist",unitList);

        return result;
    }
}
