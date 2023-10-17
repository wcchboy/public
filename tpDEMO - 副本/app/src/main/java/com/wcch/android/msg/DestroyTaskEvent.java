package com.wcch.android.msg;

/**
 * @author created by Lzq
 * @time：2022/5/13
 * @Des：
 */
public class DestroyTaskEvent {
    private String taskId;
    public DestroyTaskEvent() {
    }

    public DestroyTaskEvent(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
