//package com.yangrr.rrmianshi.enums;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.Getter;
//import org.apache.ibatis.jdbc.SQL;
//
//import javax.swing.*;
//
//
//@Getter
//@AllArgsConstructor
//public enum QuestionCatagoryEnum {
//    //    分类类型
//    //    0 - Java 基础面试题，1 - Java 集合面试题，
//    //    2 - Java 并发面试题，3 - Java 虚拟机面试题，
//    //    4 - MySQL 面试题，5 - Redis 面试题，
//    //    6 - Spring 面试题，7 - SpringBoot 面试题，
//    //    8 - SpringCloud 面试题，9 - RabbitMQ 面试题，
//    //    10 - 设计模式面试题，11 - MyBatis 面试题，
//    //    12 - ElasticSearch 面试题，13 - 伙伴匹配项目面试题，
//    //    14 - OJ 判题项目面试题，15 - 用户中心项目面试题，
//    //    16 - Docker 面试题，17 - SQL 电商场景面试题，
//    //    18 - SQL 网站场景面试题，19 - SQL 基础查询面试题，
//    //    20 - SQL 进阶查询面试题  21- "后端系统设计面试题"
//    //    22- "后端场景面试题"
//    JAVA_BASE(0, "Java 基础面试题"),
//    JAVA_COLLECTION(1, "Java 集合面试题"),
//    JAVA_CONCURRENCY(2, "Java 并发面试题"),
//    JAVA_VIRTUAL_MACHINE(3, "Java 虚拟机面试题"),
//    MYSQL(4, "MySQL 面试题"),
//    REDIS(5, "Redis 面试题"),
//    SPRING(6, "Spring 面试题"),
//    SPRING_BOOT(7, "SpringBoot 面试题"),
//    SPRING_CLOUD(8, "SpringCloud 面试题"),
//    RABBITMQ(9, "RabbitMQ 面试题"),
//    DESIGN_PATTERN(10, "设计模式面试题"),
//    MYBATIS(11, "MyBatis 面试题"),
//    ELASTIC_SEARCH(12, "ElasticSearch 面试题"),
//    PARTNER_MATCH(13, "伙伴匹配项目面试题"),
//    OJ_JUDGE(14, "OJ 判题项目面试题"),
//    USER_CENTER(15, "用户中心项目面试题"),
//    DOCKER(16, "Docker 面试题"),
//    SQL_E_COMMERCE(17, "SQL 电商场景面试题"),
//    SQL_WEBSITE(18, "SQL 网站场景面试题"),
//    SQL_BASE(19, "SQL 基础查询面试题"),
//    SQL_ADVANCED(20, "SQL 进阶查询面试题"),
//    Backend_System_Design(21, "后端系统设计面试题"),
//    Backend_Scene_Interview_Questions(22, "后端场景面试题");
//
//
//
//
//
//    private Integer code;
//    private String desc;
//
//    public static QuestionCatagoryEnum getEnumByCode(Integer code) {
//        for (QuestionCatagoryEnum questionCatagoryEnum : QuestionCatagoryEnum.values()) {
//            if (questionCatagoryEnum.getCode().equals(code)) {
//                return questionCatagoryEnum;
//            }
//        }
//        return null;
//    }
//}
