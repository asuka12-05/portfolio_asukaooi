package com.heatbreak.service;

import com.heatbreak.config.AppConfig;
import com.heatbreak.dao.BreakRecordDao;
import com.heatbreak.model.BreakRecord;
import com.heatbreak.service.AttendanceService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 交代休憩ロジック
 *
 * ルール:
 *  1. 気温 >= 37℃ のとき2時間ごとに1人が休憩（=1セット）
 *  2. 所定労働時間を超えた残業時間帯は交代なし
 *  3. 食事休憩時間帯（固定時刻: app.propertiesの lunch.start〜lunch.end）は交代なし
 *  4. 食事休憩でセット番号が切り替わる（前半=セット1, 後半=セット2）
 *  5. 勤怠テーブルへの反映はなし（前日データ参照のみ）
 *  6. ローテーションは従業員リスト順、セットをまたいでも継続
 */
public class BreakScheduleService {

    private final AppConfig config;
    private final BreakRecordDao breakRecordDao;

    // ローテーション：従業員インデックス（セットをまたいで継続）
    private int nextEmployeeIndex = 0;
    // 当日の出勤者リスト（休暇者除外済み、マスター順）
    private List<String> todayPresentEmployees = null;

    // 最後に交代を発動した時刻（2時間インターバル管理用）
    private LocalDateTime lastBreakTriggeredAt = null;

    public BreakScheduleService(AppConfig config) {
        this.config         = config;
        this.breakRecordDao = new BreakRecordDao(config);
    }

    /**
     * 交代休憩を発動する
     *
     * @param date        作業日
     * @param temperature 現在の気温
     */
    public void triggerBreakRotation(LocalDate date, double temperature,
                                     AttendanceService attendanceService) {
        LocalDateTime now     = LocalDateTime.now();
        LocalTime     nowTime = now.toLocalTime();

        // ① 残業時間帯チェック
        if (nowTime.isAfter(config.getWorkEndTime())) {
            System.out.println("⏰ 残業時間帯のため交代休憩はスキップされます。");
            return;
        }

        // ② 食事休憩時間帯チェック
        LocalTime lunchStart = config.getLunchBreakStart();
        LocalTime lunchEnd   = config.getLunchBreakEnd();
        if (!nowTime.isBefore(lunchStart) && nowTime.isBefore(lunchEnd)) {
            System.out.printf("🍱 食事休憩中（%s〜%s）のため交代はスキップされます。%n",
                lunchStart, lunchEnd);
            return;
        }

        // ③ 2時間インターバルチェック
        //    食事休憩をまたいだ場合は食事休憩終了時刻を基準に再カウント
        if (lastBreakTriggeredAt != null) {
            LocalDateTime effectiveBase = lastBreakTriggeredAt;
            // 前回発動が食事休憩より前で、現在が食事休憩後なら食事休憩終了を基準にする
            LocalDateTime lunchEndDt = LocalDateTime.of(date, lunchEnd);
            if (lastBreakTriggeredAt.isBefore(lunchEndDt) && now.isAfter(lunchEndDt)) {
                effectiveBase = lunchEndDt;
            }
            long minutesSinceLast = java.time.Duration.between(effectiveBase, now).toMinutes();
            if (minutesSinceLast < 120) {
                long remaining = 120 - minutesSinceLast;
                System.out.printf("⏳ 前回の交代から %d 分です。次の交代まであと %d 分あります。%n",
                    minutesSinceLast, remaining);
                return;
            }
        }

        // ④ セット番号を判定（食事休憩前=1, 後=2）
        int setNumber = nowTime.isBefore(lunchStart) ? 1 : 2;

        // ⑤ セット内の交代回数（何回目か）
        int rotationInSet = breakRecordDao.countRotationsInSet(date, setNumber) + 1;

        // ⑥ 対象従業員を選択（当日出勤者リストからローテーション）
        if (todayPresentEmployees == null) {
            todayPresentEmployees = attendanceService.getPresentEmployees(date);
        }
        if (todayPresentEmployees.isEmpty()) {
            System.out.println("⚠️ 出勤者リストが取得できません。先に出勤登録を行ってください。");
            return;
        }
        String targetEmployee = todayPresentEmployees.get(nextEmployeeIndex % todayPresentEmployees.size());
        nextEmployeeIndex++;

        // ⑦ break_record に記録（時刻はセット＋回次のみ、時刻は補助情報として保持）
        BreakRecord record = new BreakRecord(
            date, targetEmployee, now, temperature, false, setNumber, rotationInSet);
        long recordId = breakRecordDao.insert(record);
        breakRecordDao.updateBreakEnd(recordId, Timestamp.valueOf(now.plusMinutes(120)));

        lastBreakTriggeredAt = now;

        System.out.println("─".repeat(55));
        System.out.printf("🔄 交代休憩 発動%n");
        System.out.printf("   従業員    : %s%n", targetEmployee);
        System.out.printf("   セット    : セット%d - %d回目%n", setNumber, rotationInSet);
        System.out.printf("   気温      : %.1f℃%n", temperature);
        System.out.println("─".repeat(55));
    }

    /**
     * 指定日の全休憩スケジュールをコンソールに表示
     * セットごとにグループ化して見やすく出力する
     */
    public void printTodaySchedule(LocalDate date) {
        List<BreakRecord> records = breakRecordDao.findByDate(date);
        if (records.isEmpty()) {
            System.out.printf("%s の休憩記録はまだありません。%n", date);
            return;
        }

        LocalTime lunchStart = config.getLunchBreakStart();
        LocalTime lunchEnd   = config.getLunchBreakEnd();

        System.out.println();
        System.out.println("=== 本日の休憩記録 (" + date + ") ===");
        System.out.printf("  食事休憩: %s〜%s%n%n", lunchStart, lunchEnd);

        int currentSet = -1;
        for (BreakRecord r : records) {
            // セット切り替え時にヘッダー表示
            if (r.getSetNumber() != currentSet) {
                currentSet = r.getSetNumber();
                System.out.printf("┌─ セット%d（%s）%n",
                    currentSet,
                    currentSet == 1
                        ? "勤務開始〜食事休憩前"
                        : "食事休憩後〜勤務終了");
                System.out.printf("│  %-6s %-10s %-8s %-25s%n",
                    "回次", "従業員", "気温", "作業内容");
                System.out.println("│  " + "─".repeat(52));
            }

            String note = r.getReliefWorkNote() != null ? r.getReliefWorkNote() : "（未入力）";
            System.out.printf("│  %d回目   %-10s %.1f℃   %s%n",
                r.getRotationInSet(),
                r.getEmployeeName(),
                r.getTemperature(),
                note);
        }
        System.out.println("└" + "─".repeat(55));
    }

    // ローテーションインデックスをリセット（日付変わり時などに呼ぶ）
    public void resetRotation() {
        nextEmployeeIndex       = 0;
        lastBreakTriggeredAt    = null;
        todayPresentEmployees   = null;
    }
}
