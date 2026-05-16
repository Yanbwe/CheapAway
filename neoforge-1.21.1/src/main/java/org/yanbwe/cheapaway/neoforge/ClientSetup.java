package org.yanbwe.cheapaway.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import org.yanbwe.cheapaway.core.FilterState;

@EventBusSubscriber(modid = CheapAwayNeoForge1211.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
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

    @EventBusSubscriber(modid = CheapAwayNeoForge1211.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static class KeyHandler {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
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
                PacketDistributor.sendToServer(new Network.FilterTogglePayload(newState));
            }
        }
    }
}
