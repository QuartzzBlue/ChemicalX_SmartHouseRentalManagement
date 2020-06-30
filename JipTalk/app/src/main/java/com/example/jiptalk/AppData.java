package com.example.jiptalk;

import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.Unit;
import com.example.jiptalk.vo.User;

import java.util.ArrayList;
import java.util.HashMap;

public class AppData {

    public static String category; // 임대인 or 임차인
    public static String userID;
    public static String userUID;
    public static String userName;
    public static String token;
    public static String newToken;
    public static String nowBuildingKey;
    public static String nowUnitKey;
    public static int totalUnitCnt;
    public static int totalPaidCnt;
    public static int totalMonthlyIncome;

    public static HashMap<String, Building> buildings = new HashMap<>();
    public static HashMap<String,HashMap<String,Unit>> unitsInBuildings = new HashMap<>();
    public static HashMap<String,HashMap<String,Unit>> paidUnitsInBuildings = new HashMap<>();
    public static HashMap<String,HashMap<String,Unit>> unpaidUnitsInBuildings = new HashMap<>();
    public static ArrayList<User> myClientList;



}
