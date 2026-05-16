package org.yanbwe.cheapaway.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.yanbwe.cheapaway.api.IPlatformHelper;
import org.yanbwe.cheapaway.core.FilterState;

@Mod(CheapAwayForge.MOD_ID)
public class CheapAwayForge {
    public static final String MOD_ID = "cheapaway";
    public static final IPlatformHelper PLATFORM_HELPER = new ForgePlatformHelper();

    public CheapAwayForge() {
        // 注册服务端配置
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CheapAwayServerConfig.SPEC);

        // 注册网络
        Network.register();

        // 从配置文件初始化 FilterState
        FilterState state = FilterState.getInstance();
        state.setFilterEnabled(CheapAwayServerConfig.INSTANCE.filterEnabled.get());
        state.setFilterThreshold(CheapAwayServerConfig.INSTANCE.filterThreshold.get());

        // 配置文件重载时同步 FilterState
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onConfigReload);
    }

    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == CheapAwayServerConfig.SPEC) {
            FilterState state = FilterState.getInstance();
            state.setFilterEnabled(CheapAwayServerConfig.INSTANCE.filterEnabled.get());
            state.setFilterThreshold(CheapAwayServerConfig.INSTANCE.filterThreshold.get());
        }
    }
}
