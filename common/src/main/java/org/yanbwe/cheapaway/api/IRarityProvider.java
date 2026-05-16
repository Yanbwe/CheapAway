package org.yanbwe.cheapaway.api;

/**
 * 稀有度提供者接口。各平台实现对接对应版本的 RarityCore API。
 * 使用 Object 参数以兼容 common 模块不依赖 Minecraft 类。
 */
public interface IRarityProvider {
    /**
     * 获取物品的稀有度。
     * @param itemStack Minecraft ItemStack 对象（各平台实现自行强转）
     * @return 稀有度值 (1-7)，默认返回 1
     */
    int getRarity(Object itemStack);
}
