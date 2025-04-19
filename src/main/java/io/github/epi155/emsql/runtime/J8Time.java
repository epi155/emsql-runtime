package io.github.epi155.emsql.runtime;

import java.sql.*;
import java.time.*;

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
    public static void setDate(PreparedStatement ps, int i, LocalDate it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.DATE);
        } else {
            ps.setDate(i, Date.valueOf(it));
        }
    }
    public static void setTimestamp(PreparedStatement ps, int i, LocalDateTime it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(i, Timestamp.valueOf(it));
        }
    }
    public static void setTime(PreparedStatement ps, int i, LocalTime it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TIME);
        } else {
            ps.setTime(i, Time.valueOf(it));
        }
    }
    public static void setOffsetDateTime(PreparedStatement ps, int i, OffsetDateTime it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TIMESTAMP_WITH_TIMEZONE);
        } else {
            ps.setObject(i, it);
        }
    }
    public static void setOffsetTime(PreparedStatement ps, int i, OffsetTime it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TIME_WITH_TIMEZONE);
        } else {
            ps.setObject(i, it);
        }
    }
}
