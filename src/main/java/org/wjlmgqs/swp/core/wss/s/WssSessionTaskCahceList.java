package org.wjlmgqs.swp.core.wss.s;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点间缓存的任务队列对象
 */
public class WssSessionTaskCahceList {

    private String clientId;

    /**
     * 任务列表
     */
   private List<WssSessionTaskCahce> tasks = new ArrayList<>();

    public List<WssSessionTaskCahce> getTasks() {
        return tasks;
    }

    public WssSessionTaskCahceList setTasks(List<WssSessionTaskCahce> tasks) {
        this.tasks = tasks;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public WssSessionTaskCahceList setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }
}
