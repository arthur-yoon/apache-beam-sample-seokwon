package com.seokwon.sample.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.TreeSet;

@Getter
@Setter
@ToString
public class PersonBox implements Serializable {
    //box
    BigDecimal q1;      // 중앙값 아래애서의 중앙값
    BigDecimal q2;      // 중앙값
    BigDecimal q3;      // 중앙값 위에서의 중앙값
    BigDecimal iqr;     // Q3 - Q1, 클수록 분포가 높음
    BigDecimal min;     // Q1 - (1.5 x IQR), 아웃라이어 제외한 최소값
    BigDecimal max;     // Q3 + (1.5 x IQR), 아웃라이어 제외한 최대값
    BigDecimal mean;    // 평균
    BigDecimal sd;      // 표준편차
    int totalCount;     // 표본량
    private String key;             // from + to + userId
    private String from;
    private String to;
    private int userId;
    private TreeSet<String> amountSet = new TreeSet<>();
    private BigDecimal amountTotal;
    private Timestamp lastCompleted;         // 마지막 완료시긴
    private TreeSet<String> overMaxTransactionIdSet = new TreeSet<>();
}
