package com.heatbreak.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * sql.properties のSQL文を定数として保持するローダー。
 *
 * 使い方:
 *   PreparedStatement ps = conn.prepareStatement(SqlLoader.BREAK_INSERT);
 *
 * 定数名の命名規則:
 *   {テーブル略称}_{操作}_{対象}
 *   例: BREAK_INSERT, BREAK_SELECT_BY_DATE, ATTENDANCE_UPSERT
 */
public final class SqlLoader {

    // ─── break_record ──────────────────────────────────────
    public static final String BREAK_INSERT;
    public static final String BREAK_UPDATE_BREAK_END;
    public static final String BREAK_UPDATE_RELIEF_NOTE;
    public static final String BREAK_SELECT_BY_DATE;
    public static final String BREAK_COUNT_ROTATIONS_IN_SET;
    public static final String BREAK_SUM_BREAK_MINUTES;
    public static final String BREAK_COUNT_BY_EMPLOYEE;

    // ─── weekly_attendance ─────────────────────────────────
    public static final String ATTENDANCE_UPSERT;
    public static final String ATTENDANCE_SELECT_BY_EMPLOYEE_DATE;
    public static final String ATTENDANCE_SELECT_BY_WEEK;
    public static final String ATTENDANCE_SELECT_PRESENT_EMPLOYEES;

    // 静的初期化ブロックで一度だけ読み込む
    static {
        Properties props = new Properties();
        try (InputStream is = SqlLoader.class.getClassLoader()
                .getResourceAsStream("sql.properties")) {
            if (is == null) {
                throw new IllegalStateException("sql.properties が見つかりません。");
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("sql.properties の読み込みに失敗しました。", e);
        }

        BREAK_INSERT                        = require(props, "break.insert");
        BREAK_UPDATE_BREAK_END              = require(props, "break.update.break_end");
        BREAK_UPDATE_RELIEF_NOTE            = require(props, "break.update.relief_note");
        BREAK_SELECT_BY_DATE                = require(props, "break.select.by_date");
        BREAK_COUNT_ROTATIONS_IN_SET        = require(props, "break.count.rotations_in_set");
        BREAK_SUM_BREAK_MINUTES             = require(props, "break.sum.break_minutes");
        BREAK_COUNT_BY_EMPLOYEE             = require(props, "break.count.by_employee");

        ATTENDANCE_UPSERT                   = require(props, "attendance.upsert");
        ATTENDANCE_SELECT_BY_EMPLOYEE_DATE  = require(props, "attendance.select.by_employee_date");
        ATTENDANCE_SELECT_BY_WEEK           = require(props, "attendance.select.by_week");
        ATTENDANCE_SELECT_PRESENT_EMPLOYEES = require(props, "attendance.select.present_employees");
    }

    /** インスタンス化禁止 */
    private SqlLoader() {}

    /** キーが存在しない場合は起動時にエラーを出す */
    private static String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                "sql.properties にキー '" + key + "' が見つかりません。");
        }
        return value.trim();
    }
}
