package com.reportService.app.Models;

import java.util.ArrayList;
import java.util.List;

public enum ReportCategories {

    GraphicContent,
    Piracy,
    Scam,
    Violence;


    public static String readAll(){
        StringBuilder allNames = new StringBuilder();
        for (ReportCategories rc : ReportCategories.values()){
            allNames.append(rc.name());
            allNames.append(",");
        }
        return allNames.toString();
    }

    public static boolean checkIfExist(String category) {
        if (category == null) {
            return false;
        }
        for (ReportCategories rc : ReportCategories.values()) {
            if (rc.name().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getAll(){
        List<String>categories = new ArrayList<>();
        for(ReportCategories cat : values()){
            categories.add(cat.name());
        }
        return categories;
    }


}
