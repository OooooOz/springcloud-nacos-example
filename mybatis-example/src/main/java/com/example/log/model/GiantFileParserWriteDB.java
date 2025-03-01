package com.example.log.model;

import cn.hutool.extra.spring.SpringUtil;
import com.example.log.model.dto.GiantFileParserWriteDBTask;
import com.example.log.model.po.KeyLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class GiantFileParserWriteDB {

    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = new ThreadPoolExecutor(
            THREADS, THREADS,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(THREADS * 2),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    private static DataSource dataSource = SpringUtil.getBean(DataSource.class);

    private GiantFileParserWriteDB() {
    }

    public static void partitionWrite(List<List<KeyLog>> partition, boolean useThread) {
        if (CollectionUtils.isEmpty(partition)) {
            return;
        }

        if (useThread) {
            CompletionService<Void> compService = new ExecutorCompletionService<>(executor);
            for (List<KeyLog> keyLogs : partition) {
                compService.submit(new GiantFileParserWriteDBTask(keyLogs, dataSource));
            }
        } else {
            singleThreadWrite(partition);
        }

    }

    private static void singleThreadWrite(List<List<KeyLog>> partition) {
        String sql = "INSERT INTO t_key_log (param) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            // 关闭session的自动提交
            conn.setAutoCommit(false);
            for (List<KeyLog> keyLogs : partition) {
                for (KeyLog data : keyLogs) {
                    ps.setString(1, data.getParam());
                    ps.addBatch();
                }

                try {
                    ps.executeBatch();
                    conn.commit();
                    ps.clearBatch();
                } catch (Exception ex) {
                    handleFailedBatch(keyLogs, ex); // 失败处理
                    conn.rollback();
                }
            }

        } catch (Exception e) {
            log.info("singleThreadWrite exception：{}", e.getMessage());
        }

    }

    public static void handleFailedBatch(List<KeyLog> batch, Exception ex) {
        // 1. 记录失败批次到文件
        // 2. 提取违反唯一约束的错误码：ex.getErrorCode() == 1062
        // 3. 拆分批次重试（指数退避策略）
        log.info("handleFailedBatch");
        if (ex instanceof SQLException) {
            SQLException sqlEx = (SQLException) ex;
            while (sqlEx != null) {
                log.error("SQL State: {}", sqlEx.getSQLState());
                log.error("Error Code: {}", sqlEx.getErrorCode());
                log.error("Message: {}", sqlEx.getMessage());
                sqlEx = sqlEx.getNextException();
            }
        }
    }
}
