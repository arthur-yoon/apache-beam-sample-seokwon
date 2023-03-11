package com.seokwon.common.util;

import java.math.BigDecimal;

public class CommonUtils {
    /**
     * Utility classes, which are collections of static members, are not meant to be instantiated.
     * Even abstract utility classes, which can be extended, should not have public constructors.
     * Java adds an implicit public constructor to every class which does not define at least one explicitly.
     * Hence, at least one non-public constructor should be defined.
     */
    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean hasNull(BigDecimal bd1, BigDecimal bd2) {
        return bd1 == null || bd2 == null;
    }

    public static BigDecimal nullToZero(BigDecimal bigDecimal) {
        return bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
    }

}
