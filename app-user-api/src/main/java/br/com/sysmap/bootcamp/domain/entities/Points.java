package br.com.sysmap.bootcamp.domain.entities;

import java.time.DayOfWeek;

public enum Points {
    SUNDAY(25L),
    MONDAY(7L),
    TUESDAY(6L),
    WEDNESDAY(2L),
    THURSDAY(10L),
    FRIDAY(15L),
    SATURDAY(20L);

    private Long points;

    Points(Long points) {
        this.points = points;
    }

    public Long getPoints() {
        return points;
    }

    public static Long getPoints(DayOfWeek current_day) {
        switch (current_day) {
            case SUNDAY:
                return SUNDAY.getPoints();
            case MONDAY:
                return MONDAY.getPoints();
            case TUESDAY:
                return TUESDAY.getPoints();
            case WEDNESDAY:
                return WEDNESDAY.getPoints();
            case THURSDAY:
                return THURSDAY.getPoints();
            case FRIDAY:
                return FRIDAY.getPoints();
            case SATURDAY:
                return SATURDAY.getPoints();
            default:
                return 0L;
        }
    }
}