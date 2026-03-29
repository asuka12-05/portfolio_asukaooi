package com.heatbreak;


import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.heatbreak.config.AppConfig;
import com.heatbreak.dao.BreakRecordDao;
import com.heatbreak.model.BreakRecord;
import com.heatbreak.service.AttendanceService;
import com.heatbreak.service.BreakScheduleService;
import com.heatbreak.service.TemperatureService;

public class Main {
    public static void main(String[] args) {
        AppConfig config = AppConfig.load();
        TemperatureService   tempService       = new TemperatureService(config);
        BreakScheduleService breakService      = new BreakScheduleService(config);
        AttendanceService    attendanceService = new AttendanceService(config);
        BreakRecordDao       breakRecordDao    = new BreakRecordDao(config);

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 室内温度管理・勤怠システム ===");
        System.out.printf("  勤務時間: %s〜%s  |  食事休憩: %s〜%s%n",
            config.getWorkStartTime(), config.getWorkEndTime(),
            config.getLunchBreakStart(), config.getLunchBreakEnd());
        System.out.println("コマンド: [t] 気温入力  [s] 本日スケジュール  [n] 作業内容入力  [w] 週次勤怠  [q] 終了");

        while (true) {
            System.out.print("\nコマンド> ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            switch (cmd) {
                case "t":
                    if (!checkAttendanceReady(attendanceService, LocalDate.now())) break;
                    handleTemperatureInput(scanner, tempService, breakService, attendanceService);
                case "s": breakService.printTodaySchedule(LocalDate.now());
                case "n": handleReliefNoteInput(scanner, breakRecordDao, LocalDate.now());
                case "w": attendanceService.printWeeklyAttendance(LocalDate.now());
                case "q": { System.out.println("終了します。"); return; }
                default : System.out.println("不明なコマンドです。");
            }
        }
    }

    // ── 気温入力 ─────────────────────────────────────────
    private static void handleTemperatureInput(Scanner scanner,
                                                TemperatureService tempService,
                                                BreakScheduleService breakService,
                                                AttendanceService attendanceService) {
        System.out.print("現在の室内気温を入力してください (℃): ");
        try {
            double temp = Double.parseDouble(scanner.nextLine().trim());
            tempService.recordTemperature(temp);

            if (temp >= 37.0) {
                System.out.printf("⚠️  気温 %.1f℃ - 交代休憩を発動します%n", temp);
                // 勤怠サービスへの反映は不要のため引数から除去
                breakService.triggerBreakRotation(LocalDate.now(), temp, attendanceService);
            } else {
                System.out.printf("✅  気温 %.1f℃ - 通常勤務（交代休憩なし）%n", temp);
            }
        } catch (NumberFormatException e) {
            System.out.println("無効な数値です。");
        }
    }


    // ── 出勤者チェック ───────────────────────────────────
    /**
     * 全従業員の当日勤怠登録を確認する。
     * 未登録者がいる場合は警告を表示して false を返す。
     */
    private static boolean checkAttendanceReady(AttendanceService attendanceService,
                                                 LocalDate date) {
        List<String> missing = attendanceService.findUnregisteredEmployees(date);
        if (missing.isEmpty()) return true;

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  ⚠️  出勤登録が未完了の従業員がいます             ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        for (String name : missing) {
            System.out.printf("║  ✗ %-46s║%n", name + "（未登録）");
        }
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  weekly_attendance に当日レコードを登録してから  ║");
        System.out.println("║  気温入力を行ってください。                       ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        return false;
    }

    // ── 作業内容まとめ入力（1日の終わりに使用）──────────
    private static void handleReliefNoteInput(Scanner scanner,
                                               BreakRecordDao breakRecordDao,
                                               LocalDate date) {
        List<BreakRecord> records = breakRecordDao.findByDate(date);

        if (records.isEmpty()) {
            System.out.printf("%s の休憩記録はまだありません。%n", date);
            return;
        }

        System.out.println();
        System.out.println("=== 本日の作業内容入力 (" + date + ") ===");
        System.out.println("（Enterのみでスキップ、既入力がある場合は上書き）");
        System.out.println();

        int currentSet = -1;
        int updated = 0;

        for (BreakRecord r : records) {
            if (r.getSetNumber() != currentSet) {
                currentSet = r.getSetNumber();
                System.out.printf("── セット%d ─────────────────%n", currentSet);
            }

            String existing = r.getReliefWorkNote();
            if (existing != null && !existing.trim().isEmpty()) {
                System.out.printf("  %d回目 %s（現在: 「%s」）%n",
                    r.getRotationInSet(), r.getEmployeeName(), existing);
            } else {
                System.out.printf("  %d回目 %s%n",
                    r.getRotationInSet(), r.getEmployeeName());
            }

            System.out.print("  作業内容 > ");
            String note = scanner.nextLine().trim();

            if (!note.isEmpty()) {
                breakRecordDao.updateReliefWorkNote(r.getId(), note);
                System.out.println("  ✅ 保存");
                updated++;
            } else {
                System.out.println("  （スキップ）");
            }
        }

        System.out.printf("%n─── %d 件を更新しました。%n", updated);
    }
}
