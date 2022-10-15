package com.sanghm2.xinhxinhchat.utils;

public class CheckNumberPhone {

    private final String[] arrNumberPhone = new String[]{
            "086", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039",
            "088", "091", "094", "083", "084", "085", "081", "082",
            "089", "090", "093", "070", "079", "077", "076", "078",
            "092", "056", "058",
            "099", "059",
    };

    public boolean isValid(String phone) {
        for (String extension : arrNumberPhone) {
            if (phone.toLowerCase().startsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
