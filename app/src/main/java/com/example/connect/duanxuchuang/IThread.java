package com.example.connect.duanxuchuang;

import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */

public interface IThread {
    public void insertThread(ThreadInfo info);

    public void deleteThread(String url, int threadID);

    public void updataThread(String url, int threadID, long finished);

    public List<ThreadInfo> getThreads(String url);

    public boolean isExists(String url, int threadID);
}
