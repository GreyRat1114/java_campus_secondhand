package com.example.secondhand.mapper;

import com.example.secondhand.entity.CategoryStat;
import com.example.secondhand.entity.DayStat;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StatsMapper {
    List<CategoryStat> countProductsByCategory();
    List<DayStat> countOrdersLast7Days();
}
