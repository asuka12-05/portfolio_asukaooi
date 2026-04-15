package com.heatbreak.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.heatbreak.config.AppConfig;
import com.heatbreak.db.SqlLoader;
import com.heatbreak.model.BreakRecord;

import tool.MySQLConnection;

public class BreakRecordDao {

    public BreakRecordDao(AppConfig config) {
        // MySQLConnection が app.properties を直接参照するため初期化不要
    }

    /** 休憩記録を挿入し、生成されたIDを返す */
    public long insert(BreakRecord record) {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     SqlLoader.BREAK_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(record.getWorkDate()));
            ps.setString(2, record.getEmployeeName());
            ps.setTimestamp(3, Timestamp.valueOf(record.getBreakStart()));
            ps.setDouble(4, record.getTemperature());
            ps.setBoolean(5, record.isOvertime());
            ps.setInt(6, record.getSetNumber());
            ps.setInt(7, record.getRotationInSet());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("休憩記録の挿入エラー: " + e.getMessage(), e);
        }
        return -1;
    }

    /** 休憩終了時刻を更新 */
    public void updateBreakEnd(long id, Timestamp breakEnd) {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SqlLoader.BREAK_UPDATE_BREAK_END)) {
            ps.setTimestamp(1, breakEnd);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("休憩終了更新エラー: " + e.getMessage(), e);
        }
    }

    /** 作業内容を更新する（1日の終わりに一括入力） */
    public void updateReliefWorkNote(long id, String note) {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SqlLoader.BREAK_UPDATE_RELIEF_NOTE)) {
            ps.setString(1, note);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("作業内容更新エラー: " + e.getMessage(), e);
        }
    }

    /** 指定日の全休憩記録を取得 */
    public List<BreakRecord> findByDate(LocalDate date) {
        List<BreakRecord> list = new ArrayList<>();
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SqlLoader.BREAK_SELECT_BY_DATE)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BreakRecord r = new BreakRecord();
                    r.setId(rs.getLong("id"));
                    r.setWorkDate(rs.getDate("work_date").toLocalDate());
                    r.setEmployeeName(rs.getString("employee_name"));
                    r.setBreakStart(rs.getTimestamp("break_start").toLocalDateTime());
                    Timestamp end = rs.getTimestamp("break_end");
                    if (end != null) r.setBreakEnd(end.toLocalDateTime());
                    r.setTemperature(rs.getDouble("temperature"));
                    r.setOvertime(rs.getBoolean("is_overtime"));
                    r.setSetNumber(rs.getInt("set_number"));
                    r.setRotationInSet(rs.getInt("rotation_in_set"));
                    r.setReliefWorkNote(rs.getString("relief_work_note"));
                    list.add(r);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("休憩記録取得エラー: " + e.getMessage(), e);
        }
        return list;
    }

    /** 指定日の指定セット内の交代回数を取得 */
    public int countRotationsInSet(LocalDate date, int setNumber) {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SqlLoader.BREAK_COUNT_ROTATIONS_IN_SET)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, setNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("セット内カウント取得エラー: " + e.getMessage(), e);
        }
        return 0;
    }

    /** 指定日・従業員の休憩合計分数を取得 */
    public int sumBreakMinutes(LocalDate date, String employeeName) {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SqlLoader.BREAK_SUM_BREAK_MINUTES)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, employeeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("休憩合計取得エラー: " + e.getMessage(), e);
        }
        return 0;
    }

    /** 指定日・従業員の休憩回数を取得 */
    public int countBreaks(LocalDate date, String employeeName) {
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SqlLoader.BREAK_COUNT_BY_EMPLOYEE)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, employeeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("休憩回数取得エラー: " + e.getMessage(), e);
        }
        return 0;
    }
}
