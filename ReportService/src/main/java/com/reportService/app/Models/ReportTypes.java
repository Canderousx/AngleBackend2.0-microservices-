package com.reportService.app.Models;

public enum ReportTypes {

    VIDEO,
    COMMENT;


    public static boolean checkIfExists(String type){
        if (type == null){
            return  false;
        }
        for (ReportTypes rt : ReportTypes.values()){
            if(rt.name().equalsIgnoreCase(type)){
                return true;
            }
        }
        return false;
    }

    public static String readAll(){
        StringBuilder result = new StringBuilder();
        for(ReportTypes rt : ReportTypes.values()){
            result.append(rt.name());
            result.append(",");
        }
        return result.toString();
    }

    public static String[] toArray(){
        ReportTypes[] types = ReportTypes.values();
        String[] array = new String[types.length];
        for(int i = 0; i < types.length; i++){
            array[i] = types[i].name();
        }
        return array;
    }
}
