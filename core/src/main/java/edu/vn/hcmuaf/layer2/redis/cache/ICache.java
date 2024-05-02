package edu.vn.hcmuaf.layer2.redis.cache;

import java.util.List;
import java.util.Set;

public interface ICache<T> {
    boolean add(String key, T value);

    boolean add(T value);

    T get(String key);

    List<T> getAll();

    Set<String> getKeys();

    T remove(String key);

    boolean containsKey(String key);

    void clear();

    String getKey(T value);


}
