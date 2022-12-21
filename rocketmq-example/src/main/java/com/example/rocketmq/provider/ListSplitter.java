package com.example.rocketmq.provider;

import java.util.Iterator;
import java.util.List;

public class ListSplitter<T> implements Iterator<List<T>> {

    /**
     * 分割数据大小
     */
    private int sizeLimit;

    /**
     * 分割数据列表
     */
    private final List<T> messages;

    /**
     * 分割索引
     */
    private int currIndex;

    public ListSplitter(int sizeLimit, List<T> messages) {
        this.sizeLimit = sizeLimit;
        this.messages = messages;
    }


    @Override
    public boolean hasNext() {
        return currIndex < messages.size();
    }

    @Override
    public List<T> next() {
        int nextIndex = currIndex;
        int totalSize = 0;
        for (; nextIndex < messages.size(); nextIndex++) {
            T t = messages.get(nextIndex);
            totalSize = totalSize + t.toString().length();
            if (totalSize > sizeLimit) {
                break;
            }
        }
        List<T> subList = messages.subList(currIndex, nextIndex);
        currIndex = nextIndex;
        return subList;
    }
}
