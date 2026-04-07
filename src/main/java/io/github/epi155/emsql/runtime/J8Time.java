package io.github.epi155.emsql.runtime;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.*;

public class J8Time {
    private J8Time() {}

    public static void setDate(PreparedStatement ps, int i, LocalDate it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.DATE);
        } else {
            ps.setObject(i, it);
        }
    }
    public static void setTimestamp(PreparedStatement ps, int i, LocalDateTime it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TIMESTAMP);
        } else {
            ps.setObject(i, it);
        }
    }
    public static void setTime(PreparedStatement ps, int i, LocalTime it) throws SQLException {
        if (it==null) {
            ps.setNull(i, Types.TIME);
        } else {
            ps.setObject(i, it);
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
