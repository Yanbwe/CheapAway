package org.yanbwe.cheapaway.core;

import java.util.Objects;

/**
 * 丢弃面板屏幕位置 POJO。用于持久化面板的拖动位置。
 */
public class PanelPosition {

    /** 默认 X 位置（贴近背包界面右下角） */
    public static final int DEFAULT_X = 150;
    /** 默认 Y 位置 */
    public static final int DEFAULT_Y = 5;

    private int x;
    private int y;

    public PanelPosition() {
        this(DEFAULT_X, DEFAULT_Y);
    }

    public PanelPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PanelPosition that)) return false;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
