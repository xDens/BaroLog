package com.example.barolog.constants;

/**
 * Created by xDens on 8/20/15.
 */
public final class BasicConst {
    private BasicConst() {}

    public final class  MeasureUnits {
        public static final String MMHG = "mmHg";
        public static final String HPA = "hPa";
        public static final String ATM = "atm";

        public static final double MMHG_MULTIPLIER = 0.75006375541921;
        public static final double ATM_DIVIDER = 1013.25;

        private MeasureUnits() {}
    }
}
