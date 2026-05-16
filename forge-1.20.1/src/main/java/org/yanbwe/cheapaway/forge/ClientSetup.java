package org.yanbwe.cheapaway.forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.yanbwe.cheapaway.core.FilterState;

@Mod.EventBusSubscriber(modid = CheapAwayForge.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static final Lazy<KeyMapping> TOGGLE_KEY = Lazy.of(() ->
            new KeyMapping(
                    "key.cheapaway.toggle",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_G,
                    "key.categories.cheapaway"
            )
    );

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY.get());
    }

    @Mod.EventBusSubscriber(modid = CheapAwayForge.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class KeyHandler {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            while (TOGGLE_KEY.get().consumeClick()) {
                FilterState state = FilterState.getInstance();
                boolean newState = !state.isFilterEnabled();
                state.setFilterEnabled(newState);
                String msg = newState ? "§aPickup filter ON" : "§cPickup filter OFF";
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.displayClientMessage(
                            Component.literal(msg), true
                    );
                }
                Network.INSTANCE.sendToServer(new Network.FilterTogglePacket(newState));
            }
        }
    }
}
