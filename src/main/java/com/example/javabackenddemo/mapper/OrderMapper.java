package com.example.javabackenddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.entity.Order;
import com.example.javabackenddemo.enums.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("<script>" +
            "SELECT * FROM orders WHERE 1=1 " +
            "<if test='status != null'>AND status = #{status}</if> " +
            "<if test='orderNo != null'>AND order_no = #{orderNo}</if> " +
            "<if test='startDate != null'>AND created_at &gt;= #{startDate}</if> " +
            "<if test='endDate != null'>AND created_at &lt;= #{endDate}</if> " +
            "ORDER BY created_at DESC" +
            "</script>")
    Page<Order> findByFilters(Page<Order> page,
                              @Param("status") OrderStatus status,
                              @Param("orderNo") String orderNo,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);
}
