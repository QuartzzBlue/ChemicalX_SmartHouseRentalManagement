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
    String buildingAddress;
    int unitCnt;
    int occupiedCnt;
    int emptyCnt;
    int paidCnt;
    int unpaidCnt;
    int monthlyIncome;
    ArrayList<Unit> unitList;

    public Building() {
    }

    public Building(String name, String buildingAddress, int unitCnt) {
        this.name = name;
        this.buildingAddress = buildingAddress;
        this.unitCnt = unitCnt;
    }

    public Building(String id, String name, String buildingAddress, int unitCnt, int occupiedCnt, int emptyCnt, int paidCnt, int unpaidCnt, int monthlyIncome, ArrayList<Unit> unitList) {
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
        result.put("unitCnt", unitCnt);
        result.put("occupiedCnt", occupiedCnt);
        result.put("emptyCnt", emptyCnt);
        result.put("paidCnt", paidCnt);
        result.put("unpaidCnt", unpaidCnt);
        result.put("monthlyIncome",monthlyIncome);

        return result;
    }
}
