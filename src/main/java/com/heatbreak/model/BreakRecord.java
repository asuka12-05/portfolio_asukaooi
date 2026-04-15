package com.heatbreak.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BreakRecord {
    private Long id;
    private LocalDate workDate;
    private String employeeName;
    private LocalDateTime breakStart;
    private LocalDateTime breakEnd;
    private double temperature;
    private boolean isOvertime;
    /** セット番号（食事休憩で区切られた前半=1, 後半=2） */
    private int setNumber;
    /** セット内の交代回数（1回目, 2回目…） */
    private int rotationInSet;
    /** 休憩中に行ったリリーフ作業の内容（1日の終わりにまとめて入力） */
    private String reliefWorkNote;

    public BreakRecord() {}

    public BreakRecord(LocalDate workDate, String employeeName,
                       LocalDateTime breakStart, double temperature,
                       boolean isOvertime, int setNumber, int rotationInSet) {
        this.workDate      = workDate;
        this.employeeName  = employeeName;
        this.breakStart    = breakStart;
        this.temperature   = temperature;
        this.isOvertime    = isOvertime;
        this.setNumber     = setNumber;
        this.rotationInSet = rotationInSet;
    }

    // Getters & Setters
    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public LocalDate getWorkDate()                   { return workDate; }
    public void setWorkDate(LocalDate workDate)      { this.workDate = workDate; }

    public String getEmployeeName()                  { return employeeName; }
    public void setEmployeeName(String n)            { this.employeeName = n; }

    public LocalDateTime getBreakStart()             { return breakStart; }
    public void setBreakStart(LocalDateTime t)       { this.breakStart = t; }

    public LocalDateTime getBreakEnd()               { return breakEnd; }
    public void setBreakEnd(LocalDateTime t)         { this.breakEnd = t; }

    public double getTemperature()                   { return temperature; }
    public void setTemperature(double temperature)   { this.temperature = temperature; }

    public boolean isOvertime()                      { return isOvertime; }
    public void setOvertime(boolean overtime)        { this.isOvertime = overtime; }

    public int getSetNumber()                        { return setNumber; }
    public void setSetNumber(int setNumber)          { this.setNumber = setNumber; }

    public int getRotationInSet()                    { return rotationInSet; }
    public void setRotationInSet(int r)              { this.rotationInSet = r; }

    public String getReliefWorkNote()                { return reliefWorkNote; }
    public void setReliefWorkNote(String note)       { this.reliefWorkNote = note; }

    /** 表示用ラベル（例: "セット1-2回目"） */
    public String getSetLabel() {
        return String.format("セット%d-%d回目", setNumber, rotationInSet);
    }

    @Override
    public String toString() {
        return String.format("[休憩] %s | %s | %s | 気温:%.1f℃ | 作業内容:%s",
            workDate, employeeName, getSetLabel(), temperature,
            reliefWorkNote != null ? reliefWorkNote : "未入力");
    }
}
