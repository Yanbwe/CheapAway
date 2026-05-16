package org.yanbwe.cheapaway.core;

/**
 * 纯函数工具类，判断物品是否应该被拾取。
 * 无副作用，不依赖外部状态。
 */
public final class RarityFilter {

    private RarityFilter() {}

    /**
     * 判断是否应该拾取物品。
     *
     * @param rarity   物品稀有度 (1-7)
     * @param threshold 过滤阈值 (>= 0)
     * @param enabled  过滤功能是否开启
     * @return true 允许拾取，false 阻止拾取
     */
    public static boolean shouldPickUp(int rarity, int threshold, boolean enabled) {
        if (!enabled || threshold == 0) {
            return true;
        }
        return rarity > threshold;
    }
}
