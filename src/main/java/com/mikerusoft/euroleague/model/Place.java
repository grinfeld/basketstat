package com.mikerusoft.euroleague.model;

public enum Place {
    all, home, away;

    public static Place byName(String str) {
        try {
            return Place.valueOf(str.trim().toLowerCase());
        } catch (Exception e) {
            return all;
        }
    }
}
