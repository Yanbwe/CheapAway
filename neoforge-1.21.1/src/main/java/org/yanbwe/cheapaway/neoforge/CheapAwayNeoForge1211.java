package org.yanbwe.cheapaway.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.yanbwe.cheapaway.api.IPlatformHelper;
import org.yanbwe.cheapaway.core.FilterState;

@Mod(CheapAwayNeoForge1211.MOD_ID)
public class CheapAwayNeoForge1211 {
    public static final String MOD_ID = "cheapaway";
    public static final IPlatformHelper PLATFORM_HELPER = new NeoForgePlatformHelper1211();

    public CheapAwayNeoForge1211(IEventBus modBus, ModContainer container) {
        // 注册服务端配置
        container.registerConfig(ModConfig.Type.SERVER, CheapAwayServerConfig.SPEC);

        // 配置加载/重载时同步到 FilterState
        modBus.addListener(this::onConfigLoad);
        modBus.addListener(this::onConfigReload);
    }

    private void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == CheapAwayServerConfig.SPEC) {
            syncConfigToState();
        }
    }

    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == CheapAwayServerConfig.SPEC) {
            syncConfigToState();
        }
    }

    private static void syncConfigToState() {
        FilterState state = FilterState.getInstance();
        state.setFilterEnabled(CheapAwayServerConfig.INSTANCE.filterEnabled.get());
        state.setFilterThreshold(CheapAwayServerConfig.INSTANCE.filterThreshold.get());
    }
}
