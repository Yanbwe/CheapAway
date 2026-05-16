package org.yanbwe.cheapaway.config;

import org.yanbwe.cheapaway.core.FilterState;

public class ModConfig {
    private boolean filterEnabled = true;
    private int filterThreshold = 3;

    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    public void setFilterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
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
     * 用当前配置值初始化全局 FilterState。
     */
    public FilterState createFilterState() {
        FilterState state = FilterState.getInstance();
        state.reset(this);
        return state;
    }

    /**
     * 创建当前配置的副本。
     */
    public ModConfig copy() {
        ModConfig c = new ModConfig();
        c.filterEnabled = this.filterEnabled;
        c.filterThreshold = this.filterThreshold;
        return c;
    }
}
