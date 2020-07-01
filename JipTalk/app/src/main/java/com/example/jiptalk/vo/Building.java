package com.example.jiptalk.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Building implements Serializable {

    String id;
    String name;
    String buildingAddress;
    int unitCnt;
    int occupiedCnt;
    int emptyCnt;
    int paidCnt;
    int unpaidCnt;
    int monthlyIncome;
    int expireCnt;
    /* 임대인 계좌 정보 */
    String llBank;
    String llAccountNum;
    String llDepositor;

    Map<String, Unit> units = new HashMap<>();

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
        this.units = unitList;
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
        this.units = unitList;
    }

    public Building(String id, String name, String buildingAddress, int unitCnt, int occupiedCnt, int emptyCnt, int paidCnt, int unpaidCnt, int monthlyIncome, int expireCnt, String llBank, String llAccountNum, String llDepositor, Map<String, Unit> units) {
        this.id = id;
        this.name = name;
        this.buildingAddress = buildingAddress;
        this.unitCnt = unitCnt;
        this.occupiedCnt = occupiedCnt;
        this.emptyCnt = emptyCnt;
        this.paidCnt = paidCnt;
        this.unpaidCnt = unpaidCnt;
        this.monthlyIncome = monthlyIncome;
        this.expireCnt = expireCnt;
        this.llBank = llBank;
        this.llAccountNum = llAccountNum;
        this.llDepositor = llDepositor;
        this.units = units;
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

    public Map<String, Unit> getUnits() {
        return units;
    }

    public void setUnits(HashMap<String, Unit> units) {
        this.units = units;
    }

    public int getExpireCnt() {
        return expireCnt;
    }

    public void setExpireCnt(int expireCnt) {
        this.expireCnt = expireCnt;
    }

    public String getLlBank() {
        return llBank;
    }

    public void setLlBank(String llBank) {
        this.llBank = llBank;
    }

    public String getLlAccountNum() {
        return llAccountNum;
    }

    public void setLlAccountNum(String llAccountNum) {
        this.llAccountNum = llAccountNum;
    }

    public String getLlDepositor() {
        return llDepositor;
    }

    public void setLlDepositor(String llDepositor) {
        this.llDepositor = llDepositor;
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
                ", expireCnt=" + expireCnt +
                ", llBank='" + llBank + '\'' +
                ", llAccountNum='" + llAccountNum + '\'' +
                ", llDepositor='" + llDepositor + '\'' +
                ", units=" + units +
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
        result.put("expireCnt",expireCnt);
        result.put("llBank",llBank);
        result.put("llAccountNum",llAccountNum);
        result.put("llDepositor",llDepositor);
        //result.put("units",units);

        return result;
    }
}
