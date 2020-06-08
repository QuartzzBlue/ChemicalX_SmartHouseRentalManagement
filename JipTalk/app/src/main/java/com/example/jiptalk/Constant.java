package com.example.jiptalk;

import com.example.jiptalk.vo.Building;
import com.example.jiptalk.vo.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Constant {

    public static String category; // 임대인 or 임차인
    public static String userID;
    public static String userUID;
    public static String userName;
    public static String token;
    public static String newToken;
    public static String nowBuildingKey;
    public static String nowUnitKey;

    public static HashMap<String, Building> buildings = new HashMap<>();
    public static ArrayList<User> myClientList;



}
