package com.seokwon.sample.enums;

import java.math.BigDecimal;

public enum BoxType {
    MONTH_1("1month", BigDecimal.valueOf(1.7D)),
    MONTH_3("3month", BigDecimal.valueOf(1.65D)),
    MONTH_6("6month", BigDecimal.valueOf(1.6D)),
    YEAR_1("1year", BigDecimal.valueOf(1.55D)),
    TOTAL("total", BigDecimal.valueOf(1.5D));

    private final String type;
    private final BigDecimal whisker;

    BoxType(String type, BigDecimal whisker) {
        this.type = type;
        this.whisker = whisker;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getWhisker() {
        return whisker;
    }
}
