package com.hcr.org.n3r.idworker;

public interface WorkerIdStrategy {
    void initialize();

    long availableWorkerId();

    void release();
}
