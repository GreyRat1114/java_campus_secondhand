package com.example.secondhand.controller;

import com.example.secondhand.entity.CategoryStat;
import com.example.secondhand.entity.DayStat;
import com.example.secondhand.mapper.StatsMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatsController {
    private final StatsMapper statsMapper;

    public StatsController(StatsMapper statsMapper) {
        this.statsMapper = statsMapper;
    }

    @GetMapping("/api/stats/category")
    public List<CategoryStat> categoryStats() {
        return statsMapper.countProductsByCategory();
    }

    @GetMapping("/api/stats/orders7days")
    public List<DayStat> orderStats() {
        return statsMapper.countOrdersLast7Days();
    }
}
