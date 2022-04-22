package com.nhnacademy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CurrentDateTime {
    public static void main(String[] args) {

        // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
        LocalDate now = LocalDate.now();
        // 연도, 월(문자열, 숫자), 일, 일(year 기준), 요일(문자열, 숫자)
        int year = now.getYear();
        String month = now.getMonth().toString();
        int dayOfMonth = now.getDayOfMonth();
        String dayOfWeek = now.getDayOfWeek().toString();

        // 현재 시간 구하기
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.KOREA);
        // 포맷 적용하기
        String formattedNow = time.format(formatter);


        // 결과 출력

        System.out.println(dayOfWeek + ", " + dayOfMonth + " " + month + " " + year + " " + formattedNow + " KR" );
    }


}
