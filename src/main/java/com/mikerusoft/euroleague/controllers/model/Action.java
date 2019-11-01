package com.mikerusoft.euroleague.controllers.model;

public enum Action {
    delete, edit, create,
    na;

    public static Action byName(String name) {
        name = name == null ? "" : name.trim();
        try {
            return Action.valueOf(name.toLowerCase());
        } catch (Exception ignore){}

        return na;
    }
}
