package com.sanghm2.xinhxinhchat.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TimeZone;
import java.util.TreeMap;

public class FormatData {
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String formatYYYYmmDD(long d) {
        long unix = d * 1000;
        Date dt = new Date(unix);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return df.format(dt);
    }

    @SuppressLint("DefaultLocale")
    public static String formatHHmm(long d) {
        long unix = d * 1000;
        Date dt = new Date(unix);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return df.format(dt);
    }

    public static String formatTime(long d) {
        long unix = d * 1000;
        Date dt = new Date(unix);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        String createdTime = df.format(dt);
        String currentTime = df.format(new Date());

//        Log.d("Hoang", "currentTime: " + currentTime + " createdTime: " + createdTime);

        Date createdTimeDate = null;
        Date currentTimeDate = null;
        try {
            createdTimeDate = df.parse(createdTime);
            currentTimeDate = df.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long createdTimeMillis = createdTimeDate.getTime();
        long currentTimeMillis = currentTimeDate.getTime();

//        Log.d("Hoang", "currentTimeMillis: " + currentTimeMillis + " createdTimeMillis: " + createdTimeMillis);
//        Log.d("Hoang", "subtractTime: " + formatDuration(currentTimeMillis - createdTimeMillis));

        long millis = currentTimeMillis - createdTimeMillis;
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = millis / (1000 * 60 * 60);

        if (hours > 48) {
            return createdTime;
        } else if (hours > 24) {
            return "Hôm qua " + formatHHmm(d);
        } else if (hours > 0) {
            return hours + " giờ trước";
        } else if (minutes > 0) {
            return minutes + " phút trước";
        } else if (seconds > 3) {
            return seconds + " giây trước";
        } else {
            return "Vừa xong";
        }
    }

    public static String formatDuration(final long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = millis / (1000 * 60 * 60);

        StringBuilder b = new StringBuilder();
        b.append(hours == 0 ? "00" : hours < 10 ? String.valueOf("0" + hours) :
                String.valueOf(hours));
        b.append(":");
        b.append(minutes == 0 ? "00" : minutes < 10 ? String.valueOf("0" + minutes) :
                String.valueOf(minutes));
        b.append(":");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) :
                String.valueOf(seconds));
        return b.toString();
    }

    public static String formatCurrency(int currency) {
        DecimalFormat currencyFormat = new DecimalFormat("###,###,###");
        return currencyFormat.format(currency) + "đ";
    }
}
