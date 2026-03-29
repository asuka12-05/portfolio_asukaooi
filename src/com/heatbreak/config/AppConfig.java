package com.heatbreak.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppConfig {

    // 勤務設定
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private int standardWorkHours;

    // 食事休憩設定
    /** 食事休憩の開始時刻（固定） */
    private LocalTime lunchBreakStart;
    /** 食事休憩の終了時刻（固定） */
    private LocalTime lunchBreakEnd;

    // 従業員リスト
    private List<String> employees;

    public static AppConfig load() {
        AppConfig cfg = new AppConfig();
        Properties props = new Properties();

        try (InputStream is = AppConfig.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            System.out.println("app.properties が見つかりません。デフォルト値を使用します。");
        }

        cfg.workStartTime = LocalTime.parse(props.getProperty("work.start", "09:00"));
        cfg.workEndTime   = LocalTime.parse(props.getProperty("work.end",   "18:00"));
        cfg.standardWorkHours = Integer.parseInt(props.getProperty("work.standard_hours", "8"));

        cfg.lunchBreakStart = LocalTime.parse(props.getProperty("lunch.start", "12:00"));
        cfg.lunchBreakEnd   = LocalTime.parse(props.getProperty("lunch.end",   "12:45"));

        String empList = props.getProperty("employees", "田中,鈴木,佐藤,山田");
        cfg.employees = new ArrayList<>();
        for (String name : empList.split(",")) {
            cfg.employees.add(name.trim());
        }

        return cfg;
    }

    // --- Getters ---
    public LocalTime getWorkStartTime()           { return workStartTime; }
    public LocalTime getWorkEndTime()             { return workEndTime; }
    public int getStandardWorkHours()             { return standardWorkHours; }
    public LocalTime getLunchBreakStart()         { return lunchBreakStart; }
    public LocalTime getLunchBreakEnd()           { return lunchBreakEnd; }
    public List<String> getEmployees()            { return employees; }


}
