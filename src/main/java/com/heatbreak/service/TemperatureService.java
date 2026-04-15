package com.heatbreak.service;

import com.heatbreak.config.AppConfig;
//import com.heatbreak.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
public class TemperatureService {

    // シングルトンではないため、config不要のケースも対応
    private AppConfig config;

    public TemperatureService() {}

    public TemperatureService(AppConfig config) {
        this.config = config;
    }
    
    /**
     * 気温をtemperature_logテーブルに記録する
     * （DB接続なし版：コンソール出力のみ）
     */
/**    public void recordTemperature(double temp) {
        if (config != null) {
            recordToDb(temp);
        }
        System.out.printf("[気温記録] %.1f℃%n", temp);
    }
*/
/**
    private void recordToDb(double temp) {
        String sql = "INSERT INTO temperature_log (temperature, above_37) VALUES (?, ?)";
        //try (Connection conn = DatabaseManager.getInstance(config).getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, temp);
            ps.setBoolean(2, temp >= 37.0);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("気温記録DBエラー（スキップ）: " + e.getMessage());
        }
    }
}
*/