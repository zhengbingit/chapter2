package org.smart4j.chapter2.util;

/**
 * Created by zhengbinMac on 2017/3/25.
 */

import org.apache.commons.collections.MapUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类
 */
public class CollectionUtil {
    /**
     * 判断 Collection 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtil.isEmpty(collection);
    }

    /**
     * 判断 Collection 是否为非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断 Map 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    /**
     * 判断 Map 是否为非空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
}
