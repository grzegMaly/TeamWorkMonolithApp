package com.mordiniaa.backend.config;

import lombok.Getter;

import java.util.List;

@Getter
public class NotesConstants {

    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "15";
    public static final String SORT_ORDER = "asc";
    public static final List<String> ALLOWED_SORTING_KEYS = List.of("title", "_class", "createdAt", "updatedAt");
}
