package org.wjlmgqs.swp.core.wss.s;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wjlmgqs.swp.core.constant.SwpConstants;

@Slf4j
@Service
public class JobTaskServiceImpl implements JobTaskService {


    @Override
    @Async(SwpConstants.JOB_THREAD_EXECUTOR_NAME)
    public void exec(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("JobTaskService exec err -> {}" , e);
        }
    }
}
