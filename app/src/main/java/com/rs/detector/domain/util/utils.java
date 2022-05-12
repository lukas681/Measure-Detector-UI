package com.rs.detector.domain.util;

import com.rs.detector.domain.MeasureBox;

public class utils {

    public static Long[] convertToXYWH(MeasureBox mb) {
        return new Long[]{
            mb.getUlx(),
            mb.getUly(),
            mb.getLrx() - mb.getUlx(),
            mb.getLry() - mb.getUly()
        };
}
}
