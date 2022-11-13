package com.example.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);      // 2022-11-09T09:08:38.944
        String basicDate = localDateTime.format(DateTimeFormatter.BASIC_ISO_DATE);
        System.out.println(basicDate);          //  20221109
        String isoLocalDateTime = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.println(isoLocalDateTime);   //  2022-11-09T09:08:38.944
        String isoDate = localDateTime.format(DateTimeFormatter.ISO_DATE);
        System.out.println(isoDate);            //  2022-11-09
        String isoTime = localDateTime.format(DateTimeFormatter.ISO_TIME);
        System.out.println(isoTime);            //  09:08:38.944
        String formatTime = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println(formatTime);         //  09:08:38
        String formatTime1 = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        System.out.println(formatTime1);        //  09:08:38:944    和ISO_TIME区别在于毫秒显示是自定义的冒号
        String formatTime2 = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(formatTime2);        //  2022-11-09 09:08:38    可以去除毫秒数

        System.out.println("===================================================================");
        String dateTimeStr = "2022-11-09T09:08:38.944";
        LocalDateTime parse = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.println(parse);              //   2022-11-09T09:08:38.944    如果字符时间带T，用ISO_LOCAL_DATE_TIME
        String dateTimeStr1 = "2022-11-09 09:08:38.944";
        LocalDateTime parse1 = LocalDateTime.parse(dateTimeStr1, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        System.out.println(parse1);             //  2022-11-09T09:08:38.944 如果字符时间带T，用自定义的

        String dateTimeStr2 = "2022-11-09";
        LocalDate parse2 = LocalDate.parse(dateTimeStr2, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(parse2);             //  2022-11-09
        LocalDate parse3 = LocalDate.parse(dateTimeStr2, DateTimeFormatter.ISO_DATE);
        System.out.println(parse3);             //  2022-11-09

        System.out.println("===================================================================");
        String dateTimeStr4 = "2022-11-09T09:08:38.944";
        LocalDateTime parse4 = LocalDateTime.parse(dateTimeStr4, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        long epochSecond = parse4.toEpochSecond(ZoneOffset.ofHours(8));
        System.out.println(epochSecond);        //  1667956118 只保留到秒的时间戳

        LocalDateTime toLocalDateTime = Instant.ofEpochSecond(epochSecond).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        System.out.println(toLocalDateTime);    //  时间戳转LocalDateTime,保留到秒

        long toEpochMilli = parse4.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        System.out.println(toEpochMilli);       //  1667956118944 保留到毫秒的时间戳

        LocalDateTime toLocalDateTime1 = Instant.ofEpochMilli(toEpochMilli).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        System.out.println(toLocalDateTime1);   //  时间戳转LocalDateTime,保留到毫秒

        LocalDate localDate = Instant.ofEpochMilli(toEpochMilli).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        System.out.println(localDate);          // 时间戳只转成日期

        System.out.println("===================================================================");
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(LocalDateTime.now().format(pattern), pattern); // 去除毫秒
        // 当月第一天0时0分0秒 与 当月最后一天最后一秒
        LocalDateTime firstDayTime = dateTime.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        System.out.println(firstDayTime);       //  2022-11-01T00:00
        LocalDateTime lastDayTime = dateTime.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        System.out.println(lastDayTime);        //  2022-11-30T23:59:59

        // 当年第一天0时0分0秒 与 当年最后一天最后一秒
        LocalDateTime firstDayTimeYear = dateTime.with(TemporalAdjusters.firstDayOfYear()).withHour(0).withMinute(0).withSecond(0);
        System.out.println(firstDayTimeYear);       //  2022-01-01T00:00
        LocalDateTime lastDayTimeYear = dateTime.with(TemporalAdjusters.lastDayOfYear()).withHour(23).withMinute(59).withSecond(59);
        System.out.println(lastDayTimeYear);        //  2022-12-31T23:59:59

        // 获取当天的起始时间与结束时间
        dateTime.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime firstTimeOfDay = dateTime.withHour(0).withMinute(0).withSecond(0);
        System.out.println(firstTimeOfDay);         //  2022-11-09T00:00
        LocalDateTime lastTimeOfDay = dateTime.withHour(23).withMinute(59).withSecond(59);
//        LocalDateTime lastTimeOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);   //  2022-11-09T23:59:59.999999999
        System.out.println(lastTimeOfDay);          //  2022-11-09T23:59:59

        System.out.println("===================================================================");
        LocalDateTime now = LocalDateTime.parse(LocalDateTime.now().format(pattern), pattern); // 去除毫秒
        System.out.println(now);                //  规定时间：2022-11-09T10:24:57
        System.out.println(now.plusHours(5));   //  比规定时间晚5h    2022-11-09T15:24:57
        System.out.println(now.plusDays(5));    //  比规定时间晚5d    2022-11-14T10:24:57
        System.out.println(now.minusHours(5));  //  比规定时间提前5h   2022-11-09T05:24:57
        System.out.println(now.minusDays(5));   //  比规定时间提前5d   2022-11-04T10:24:57

        System.out.println("===================================================================");
        LocalDateTime localDateTime1 = LocalDateTime.parse(LocalDateTime.now().format(pattern), pattern); // 去除毫秒 2022-11-09T10:44:31
        LocalDateTime time = localDateTime1.atZone(ZoneId.from(ZoneOffset.ofHours(8))).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        System.out.println(time);   // 2022-11-09T02:44:31

    }
}
