package com.osiris.a.utils;

public class Arrays {
    public static <T> boolean contains(T[] arr, T obj){
        for (T o : arr) {
            if(o == obj) return true;
        }
        return false;
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
