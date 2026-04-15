package com.heatbreak.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import com.heatbreak.config.AppConfig;
import com.heatbreak.dao.BreakRecordDao;
import com.heatbreak.dao.WeeklyAttendanceDao;
import com.heatbreak.model.WeeklyAttendance;

/**
 * 勤怠管理サービス
 *
 * - 休憩記録を週次勤怠テーブルに反映
 * - 前日の勤怠情報を参照して翌日の残業判定に利用
 * - 週次レポート表示
 */
public class AttendanceService {

    private final AppConfig config;
    private final WeeklyAttendanceDao attendanceDao;
    private final BreakRecordDao breakRecordDao;

    public AttendanceService(AppConfig config) {
        this.config         = config;
        this.attendanceDao  = new WeeklyAttendanceDao(config);
        this.breakRecordDao = new BreakRecordDao(config);
    }

    /**
     * 勤務開始を記録する（打刻）
     */
    public void clockIn(String employeeName, LocalDate date) {
        WeeklyAttendance wa = getOrCreate(employeeName, date);
        wa.setWorkStart(LocalDateTime.of(date, config.getWorkStartTime()));
        attendanceDao.upsert(wa);
        System.out.printf("[出勤] %s : %s%n", employeeName, wa.getWorkStart());
    }

    /**
     * 勤務終了を記録し、残業分を計算してUPSERT
     */
    public void clockOut(String employeeName, LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        WeeklyAttendance wa = getOrCreate(employeeName, date);
        wa.setWorkEnd(now);

        // 実労働時間（分）
        LocalDateTime workStart = wa.getWorkStart() != null
            ? wa.getWorkStart()
            : LocalDateTime.of(date, config.getWorkStartTime());
        long totalMinutes = java.time.Duration.between(workStart, now).toMinutes();
        int breakMin = breakRecordDao.sumBreakMinutes(date, employeeName);
        int actualWorkMin = (int)(totalMinutes - breakMin);

        // 所定労働時間（分）
        int scheduledMin = config.getStandardWorkHours() * 60;
        int overtimeMin  = Math.max(0, actualWorkMin - scheduledMin);

        wa.setActualWorkMinutes(actualWorkMin);
        wa.setBreakMinutes(breakMin);
        wa.setOvertimeMinutes(overtimeMin);
        wa.setBreakCount(breakRecordDao.countBreaks(date, employeeName));

        if (overtimeMin > 0) {
            wa.setNote(String.format("残業 %d 分", overtimeMin));
        }

        attendanceDao.upsert(wa);
        System.out.printf("[退勤] %s | 実労働: %d分 | 休憩: %d分 | 残業: %d分%n",
            employeeName, actualWorkMin, breakMin, overtimeMin);
    }

    /**
     * 休憩を勤怠テーブルに反映（BreakScheduleServiceから呼ばれる）
     */
    public void reflectBreakToAttendance(LocalDate date, String employeeName, int breakMinutes) {
        WeeklyAttendance wa = getOrCreate(employeeName, date);

        // 累積休憩時間・回数を更新
        wa.setBreakMinutes(wa.getBreakMinutes() + breakMinutes);
        wa.setBreakCount(wa.getBreakCount() + 1);

        // 実労働時間を再計算
        int scheduledMin = config.getStandardWorkHours() * 60;
        int actualWorkMin = Math.max(0, wa.getActualWorkMinutes() - breakMinutes);
        wa.setActualWorkMinutes(actualWorkMin);
        wa.setOvertimeMinutes(Math.max(0, actualWorkMin - scheduledMin));

        attendanceDao.upsert(wa);
        System.out.printf("  [勤怠更新] %s | 休憩+%d分 | 累積休憩: %d分 | 休憩回数: %d回%n",
            employeeName, breakMinutes, wa.getBreakMinutes(), wa.getBreakCount());
    }

    /**
     * 前日の勤怠情報を取得する
     * （残業があったかどうかの確認などに使用）
     */
    public WeeklyAttendance getPreviousDayAttendance(String employeeName, LocalDate today) {
        LocalDate yesterday = today.minusDays(1);
        return attendanceDao.findByEmployeeAndDate(employeeName, yesterday);
    }

    /**
     * 前日に残業があったか判定
     */
    public boolean hadOvertimeYesterday(String employeeName, LocalDate today) {
        WeeklyAttendance prev = getPreviousDayAttendance(employeeName, today);
        return prev != null && prev.hadOvertime();
    }

    /**
     * 週次勤怠をコンソールに表示
     *
     * @param anyDateInWeek 該当週の任意の日付
     */
    public void printWeeklyAttendance(LocalDate anyDateInWeek) {
        LocalDate weekStart = anyDateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd   = weekStart.plusDays(6);

        List<WeeklyAttendance> records = attendanceDao.findByWeek(weekStart, weekEnd);

        System.out.println();
        System.out.println("╔" +repeat("═", 90) + "╗");
        System.out.printf("║  週次勤怠管理  %s ～ %s%s║%n",
            weekStart, weekEnd, " ".repeat(90 - 26 - weekStart.toString().length() - weekEnd.toString().length()));
        System.out.println("╠" + "═".repeat(90) + "╣");
        System.out.printf("║ %-10s %-12s %-12s %-10s %-10s %-10s %-10s %-10s ║%n",
            "日付", "従業員", "勤務開始", "勤務終了", "実労働(分)", "休憩(分)", "残業(分)", "休憩回数");
        System.out.println("╠" + "═".repeat(90) + "╣");

        if (records.isEmpty()) {
            System.out.println("║  データなし" + " ".repeat(79) + "║");
        } else {
            for (WeeklyAttendance wa : records) {
                String ws = wa.getWorkStart() != null ? wa.getWorkStart().toLocalTime().toString() : "-";
                String we = wa.getWorkEnd()   != null ? wa.getWorkEnd().toLocalTime().toString()   : "-";
                System.out.printf("║ %-10s %-12s %-12s %-10s %-10d %-10d %-10d %-10d ║%n",
                    wa.getWorkDate(),
                    wa.getEmployeeName(),
                    ws, we,
                    wa.getActualWorkMinutes(),
                    wa.getBreakMinutes(),
                    wa.getOvertimeMinutes(),
                    wa.getBreakCount());
            }
        }
        System.out.println("╚" + "═".repeat(90) + "╝");
    }

    /** 既存レコードを取得、なければ新規作成 */
    private WeeklyAttendance getOrCreate(String employeeName, LocalDate date) {
        WeeklyAttendance wa = attendanceDao.findByEmployeeAndDate(employeeName, date);
        if (wa == null) {
            wa = new WeeklyAttendance(employeeName, date, config.getStandardWorkHours());
            // 初期の実労働時間 = 所定労働時間（分）
            wa.setActualWorkMinutes(config.getStandardWorkHours() * 60);
        }
        return wa;
    }

    /**
     * 当日の出勤者チェック。
     * app.properties の全従業員のうち weekly_attendance に
     * 当日レコードがない従業員を「未登録者」として返す。
     * 戻り値が空リストなら全員登録済み（正常）。
     */
    public List<String> findUnregisteredEmployees(LocalDate date) {
        List<String> present = attendanceDao.findPresentEmployees(date, config.getEmployees());
        List<String> missing = new ArrayList<>();
        for (String name : config.getEmployees()) {
            if (!present.contains(name)) missing.add(name);
        }
        return missing;
    }

    /**
     * 当日の出勤者リストを従業員マスター順で返す（休暇者除外済み）。
     */
    public List<String> getPresentEmployees(LocalDate date) {
        return attendanceDao.findPresentEmployees(date, config.getEmployees());
    }

}