package com.example.LeagueTest.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StatisticDto {
    private Long count;
    private Map<String, Integer> countToDates;
    private Map<String, Integer> frequency;

}
