package cn.edu.zjut.common.redis;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author bert
 * @description redis工具类
 * @date 2023/1/14 13:10
 */
public class RedisUtils {

    public static <T> String[] listToStringArray(List<T> list, Function<T,String> function) {
        String[] strings = new String[list.size()];
        int index = 0;
        for (T t : list) {
            strings[index++] = function.apply(t);
        }
        return strings;
    }
}
