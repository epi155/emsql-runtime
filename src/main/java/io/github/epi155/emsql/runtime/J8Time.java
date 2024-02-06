package io.github.epi155.emsql.runtime;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class J8Time {
    private J8Time() {}

    public static LocalDate toLocalDate(Date d) {
        return d==null ? null : d.toLocalDate();
    }
    public static LocalDateTime toLocalDateTime(Timestamp d) {
        return d==null ? null : d.toLocalDateTime();
    }
    public static LocalTime toLocalTime(Time d) {
        return d==null ? null : d.toLocalTime();
    }
}
