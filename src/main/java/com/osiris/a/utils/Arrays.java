package com.osiris.a.utils;

import java.util.ArrayList;
import java.util.List;

public class Arrays {
    public static <T> boolean contains(T[] arr, T obj) {
        for (T o : arr) {
            if (o == obj) return true;
        }
        return false;
    }

    public static <T> List<T> toList(T[] arr) {
        return toList(arr, 0, arr.length);
    }

    public static <T> List<T> toList(T[] arr, int start, int end) {
        List<T> list = new ArrayList<>(arr.length);
        for (int i = start; i < end; i++) {
            list.add(arr[i]);
        }
        return list;
    }

    public static <T> String toString(T[] arr) {
        StringBuilder s = new StringBuilder("[");
        for (T o : arr) {
            s.append("'");
            s.append(o);
            s.append("'");
        }
        s.append("]");
        return s.toString();
    }
}
