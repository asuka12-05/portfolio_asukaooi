package com.heatbreak.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WeeklyAttendance {
    private Long id;
    private String employeeName;
    private LocalDate workDate;
    private LocalDateTime workStart;
    private LocalDateTime workEnd;
    private double scheduledHours;
    private int actualWorkMinutes;
    private int breakMinutes;
    private int overtimeMinutes;
    private int breakCount;
    private String note;

    public WeeklyAttendance() {}

    public WeeklyAttendance(String employeeName, LocalDate workDate, double scheduledHours) {
        this.employeeName   = employeeName;
        this.workDate       = workDate;
        this.scheduledHours = scheduledHours;
    }

    // Getters & Setters
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getEmployeeName()              { return employeeName; }
    public void setEmployeeName(String n)        { this.employeeName = n; }

    public LocalDate getWorkDate()               { return workDate; }
    public void setWorkDate(LocalDate d)         { this.workDate = d; }

    public LocalDateTime getWorkStart()          { return workStart; }
    public void setWorkStart(LocalDateTime t)    { this.workStart = t; }

    public LocalDateTime getWorkEnd()            { return workEnd; }
    public void setWorkEnd(LocalDateTime t)      { this.workEnd = t; }

    public double getScheduledHours()            { return scheduledHours; }
    public void setScheduledHours(double h)      { this.scheduledHours = h; }

    public int getActualWorkMinutes()            { return actualWorkMinutes; }
    public void setActualWorkMinutes(int m)      { this.actualWorkMinutes = m; }

    public int getBreakMinutes()                 { return breakMinutes; }
    public void setBreakMinutes(int m)           { this.breakMinutes = m; }

    public int getOvertimeMinutes()              { return overtimeMinutes; }
    public void setOvertimeMinutes(int m)        { this.overtimeMinutes = m; }

    public int getBreakCount()                   { return breakCount; }
    public void setBreakCount(int c)             { this.breakCount = c; }

    public String getNote()                      { return note; }
    public void setNote(String note)             { this.note = note; }

    /** 残業があるか（前日の勤怠判定用） */
    public boolean hadOvertime() {
        return overtimeMinutes > 0;
    }
}
