package com.heatbreak.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.heatbreak.config.AppConfig;
import com.heatbreak.db.SqlLoader;
import com.heatbreak.model.WeeklyAttendance;

import tool.MySQLConnection;

public class WeeklyAttendanceDao {

    public WeeklyAttendanceDao(AppConfig config) {
        // MySQLConnection が app.properties を直接参照するため初期化不要
    }

    /** UPSERT（存在すれば更新、なければ挿入） */
    public void upsert(WeeklyAttendance wa) {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SqlLoader.ATTENDANCE_UPSERT)) {
            ps.setString(1, wa.getEmployeeName());
            ps.setDate(2, Date.valueOf(wa.getWorkDate()));
            ps.setTimestamp(3, wa.getWorkStart() != null ? Timestamp.valueOf(wa.getWorkStart()) : null);
            ps.setTimestamp(4, wa.getWorkEnd()   != null ? Timestamp.valueOf(wa.getWorkEnd())   : null);
            ps.setDouble(5, wa.getScheduledHours());
            ps.setInt(6, wa.getActualWorkMinutes());
            ps.setInt(7, wa.getBreakMinutes());
            ps.setInt(8, wa.getOvertimeMinutes());
            ps.setInt(9, wa.getBreakCount());
            ps.setString(10, wa.getNote());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("勤怠UPSERTエラー: " + e.getMessage(), e);
        }
    }

    /** 指定日・従業員の勤怠を取得 */
    public WeeklyAttendance findByEmployeeAndDate(String employeeName, LocalDate date) {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     SqlLoader.ATTENDANCE_SELECT_BY_EMPLOYEE_DATE)) {
            ps.setString(1, employeeName);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException("勤怠取得エラー: " + e.getMessage(), e);
        }
        return null;
    }

    /** 指定週（月〜日）の全勤怠を取得 */
    public List<WeeklyAttendance> findByWeek(LocalDate weekStart, LocalDate weekEnd) {
        List<WeeklyAttendance> list = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SqlLoader.ATTENDANCE_SELECT_BY_WEEK)) {
            ps.setDate(1, Date.valueOf(weekStart));
            ps.setDate(2, Date.valueOf(weekEnd));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("週次勤怠取得エラー: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * 指定日に出勤登録されている従業員名一覧を返す。
     * weekly_attendance にその日のレコードがある = 出勤扱い。
     * app.properties の employees 順を維持して返す。
     */
    public List<String> findPresentEmployees(LocalDate date, List<String> masterOrder) {
        List<String> present = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     SqlLoader.ATTENDANCE_SELECT_PRESENT_EMPLOYEES)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) present.add(rs.getString("employee_name"));
            }
        } catch (Exception e) {
            throw new RuntimeException("出勤者取得エラー: " + e.getMessage(), e);
        }
        // masterOrder の順番を維持しつつ出勤者だけ残す
        List<String> ordered = new ArrayList<>();
        for (String name : masterOrder) {
            if (present.contains(name)) ordered.add(name);
        }
        return ordered;
    }

    private WeeklyAttendance mapRow(ResultSet rs) throws SQLException {
        WeeklyAttendance wa = new WeeklyAttendance();
        wa.setId(rs.getLong("id"));
        wa.setEmployeeName(rs.getString("employee_name"));
        wa.setWorkDate(rs.getDate("work_date").toLocalDate());
        Timestamp ws = rs.getTimestamp("work_start");
        if (ws != null) wa.setWorkStart(ws.toLocalDateTime());
        Timestamp we = rs.getTimestamp("work_end");
        if (we != null) wa.setWorkEnd(we.toLocalDateTime());
        wa.setScheduledHours(rs.getDouble("scheduled_hours"));
        wa.setActualWorkMinutes(rs.getInt("actual_work_minutes"));
        wa.setBreakMinutes(rs.getInt("break_minutes"));
        wa.setOvertimeMinutes(rs.getInt("overtime_minutes"));
        wa.setBreakCount(rs.getInt("break_count"));
        wa.setNote(rs.getString("note"));
        return wa;
    }
}
