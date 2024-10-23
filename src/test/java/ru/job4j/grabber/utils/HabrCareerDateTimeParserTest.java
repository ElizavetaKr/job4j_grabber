package ru.job4j.grabber.utils;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    void parse() {
        String date = "2024-10-16T14:38:33+03:00";
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse(date);
        LocalDateTime now = LocalDateTime.now();
        assertThat(result).isBefore(now);
    }
    }