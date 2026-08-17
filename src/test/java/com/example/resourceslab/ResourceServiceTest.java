package com.example.resourceslab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ResourceServiceTest {
    @Autowired ResourceService service;

    @Test
    void body_failure_should_remain_primary_and_close_failure_should_be_suppressed() {
        Throwable t = service.run(true, true);
        assertThat(t).isInstanceOf(IllegalStateException.class);
        assertThat(t.getSuppressed()).hasSize(1);
        assertThat(t.getSuppressed()[0]).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void close_failure_should_be_primary_when_body_succeeds() {
        Throwable t = service.run(false, true);
        assertThat(t).isInstanceOf(IllegalArgumentException.class);
    }
}
