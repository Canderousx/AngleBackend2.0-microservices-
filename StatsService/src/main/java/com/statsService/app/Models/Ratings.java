package com.statsService.app.Models;

public enum Ratings {

    LIKE,
    DISLIKE,
    NONE;

    public static boolean doesRatingExist(String rating){
        for (Ratings value : Ratings.values()) {
            if (value.name().equalsIgnoreCase(rating)) {
                return true;
            }
        }
        return false;
    }

}
