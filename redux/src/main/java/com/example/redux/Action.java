package com.example.redux;

/**
 * Created by kang on 17-3-10.
 */
public class Action<T> {
    public String type;

    private T content;

    protected Action(String type, T content) {
        this.type = type;
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("type: %s ---> %s",type,content.toString());
    }
}
