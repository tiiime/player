package com.example.redux;

/**
 * Created by kang on 17-3-10.
 */
interface IActionCreator<T> {
    Action<T> create(T content);
}
