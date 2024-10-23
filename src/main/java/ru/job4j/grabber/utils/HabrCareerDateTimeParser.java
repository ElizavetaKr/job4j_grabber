package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-mm-ddТhh:mm:ss");

    @Override
    public LocalDateTime parse(String parse) {
        ZonedDateTime result = ZonedDateTime.parse(parse, DateTimeFormatter.ISO_DATE_TIME);
        return result.toLocalDateTime();
    }

}