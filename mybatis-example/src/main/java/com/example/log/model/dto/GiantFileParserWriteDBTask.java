package com.example.log.model.dto;

import com.example.log.model.GiantFileParserWriteDB;
import com.example.log.model.po.KeyLog;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public class GiantFileParserWriteDBTask implements Callable<Void> {

    private List<KeyLog> keyLogs;

    private DataSource dataSource;

    public GiantFileParserWriteDBTask(List<KeyLog> keyLogs, DataSource dataSource) {
        this.keyLogs = keyLogs;
        this.dataSource = dataSource;
    }

    @Override
    public Void call() {
        String sql = "INSERT INTO t_key_log (param) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            // 关闭session的自动提交
            conn.setAutoCommit(false);
            for (KeyLog data : keyLogs) {
                ps.setString(1, data.getParam());
                ps.addBatch();
            }

            try {
                ps.executeBatch();
                conn.commit();
                ps.clearBatch();
            } catch (Exception ex) {
                GiantFileParserWriteDB.handleFailedBatch(keyLogs, ex); // 失败处理
                conn.rollback();
            }

        } catch (Exception e) {
            log.info("singleThreadWrite exception：{}", e.getMessage());
        }
        return null;
    }
}
