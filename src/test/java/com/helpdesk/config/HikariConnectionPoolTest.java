package com.helpdesk.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HikariConnectionPoolTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("DataSource가 HikariDataSource로 주입된다")
    void dataSourceIsHikari() {
        assertThat(dataSource).isInstanceOf(HikariDataSource.class);

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        assertThat(hikariDataSource.getPoolName()).isEqualTo("HelpdeskHikariPool");
        assertThat(hikariDataSource.getMaximumPoolSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("커넥션을 정상적으로 획득하고 반납할 수 있다")
    void canGetConnection() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(2)).isTrue();
            assertThat(connection.getMetaData().getURL()).contains("helpdesk");
        }
    }

    @Test
    @DisplayName("설정한 최대 풀 사이즈(10)를 초과하면 connection-timeout 이후 예외가 발생한다")
    void poolSizeIsLimited() throws Exception {
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        int maxPoolSize = hikariDataSource.getMaximumPoolSize();

        List<Connection> connections = new ArrayList<>();
        try {
            // 1) 설정된 최대치만큼은 정상적으로 열려야 한다
            for (int i = 0; i < maxPoolSize; i++) {
                Connection connection = dataSource.getConnection();
                assertThat(connection.isValid(1)).isTrue();
                connections.add(connection);
            }
            assertThat(connections).hasSize(maxPoolSize);

            // 2) 이미 최대치를 다 쓴 상태에서 한 개 더 요청하면
            //    connection-timeout(3000ms) 후 예외가 발생해야 한다 (=풀이 실제로 제한되고 있다는 증거)
            long start = System.currentTimeMillis();
            org.junit.jupiter.api.Assertions.assertThrows(
                    java.sql.SQLException.class,
                    dataSource::getConnection
            );
            long elapsed = System.currentTimeMillis() - start;

            // connection-timeout 설정값(3000ms) 근처에서 타임아웃 났는지 확인
            assertThat(elapsed).isGreaterThanOrEqualTo(2900);
        } finally {
            // 반드시 반납 (반납해야 풀이 재사용 가능한 상태로 돌아옴)
            for (Connection connection : connections) {
                connection.close();
            }
        }
    }
}
