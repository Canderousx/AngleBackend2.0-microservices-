package com.reportService.app.Models;

public enum ReportSolutions {
    MEDIA_BANNED,

    ACCOUNT_BANNED,

    CANCELED;



    public static boolean checkIfExist(String solution) {
        if (solution == null) {
            return false;
        }
        for (ReportSolutions rs : ReportSolutions.values()) {
            if (rs.name().equalsIgnoreCase(solution)) {
                return true;
            }
        }
        return false;
    }

    public static String readAll(){
        StringBuilder allNames = new StringBuilder();
        for (ReportSolutions rs : ReportSolutions.values()){
            allNames.append(rs.name());
            allNames.append(",");
        }
        return allNames.toString();
    }

    public static String[] toArray(){
        ReportSolutions[] solutions = ReportSolutions.values();
        String[] array = new String[solutions.length];
        for(int i = 0; i < solutions.length; i++){
            array[i] = solutions[i].name();
        }
        return array;
    }
}
