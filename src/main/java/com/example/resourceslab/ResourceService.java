package com.example.resourceslab;

import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    public Throwable run(boolean bodyFails, boolean closeFails) {
        try (FailingResource resource = new FailingResource(closeFails)) {
            if (bodyFails) throw new IllegalStateException("body failure");
            return null;
        } catch (Throwable t) {
            // 最小修正: Javaが付与したsuppressed例外を保持する
            return t;
        }
    }

    static class FailingResource implements AutoCloseable {
        private final boolean fail;
        FailingResource(boolean fail) { this.fail = fail; }
        public void close() { if (fail) throw new IllegalArgumentException("close failure"); }
    }
}
