package org.yanbwe.cheapaway.core;

import org.yanbwe.cheapaway.config.ModConfig;

/**
 * 会话级别的过滤状态单例。存储当前游戏会话中的过滤开关和阈值。
 * 通过命令或热键修改的值仅在本次会话中生效，不会写回配置文件。
 */
public class FilterState {

    private static final FilterState INSTANCE = new FilterState();

    private boolean filterEnabled;
    private int filterThreshold;

    private FilterState() {
        this.filterEnabled = true;
        this.filterThreshold = 3;
    }

    public static FilterState getInstance() {
        return INSTANCE;
    }

    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    public void setFilterEnabled(boolean enabled) {
        this.filterEnabled = enabled;
    }

    public void toggle() {
        this.filterEnabled = !this.filterEnabled;
    }

    public int getFilterThreshold() {
        return filterThreshold;
    }

    public void setFilterThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must be >= 0, got: " + threshold);
        }
        this.filterThreshold = threshold;
    }

    /**
     * 从配置文件恢复默认值。
     */
    public void reset(ModConfig config) {
        this.filterEnabled = config.isFilterEnabled();
        this.filterThreshold = config.getFilterThreshold();
    }
}
