package com.example.helloworldmvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public interface PerformanceTestService {
    String runSequentialTest(int questionCount);
    String runParallelTest(int questionCount);
    String runMockSequentialTest(int questionCount);

    String runMockParallelTest(int questionCount);

}
