package org.yanbwe.cheapaway.neoforge;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.yanbwe.cheapaway.core.FilterState;

public class Network {

    public record FilterTogglePayload(boolean enabled) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<FilterTogglePayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CheapAwayNeoForge261.MOD_ID, "filter_toggle"));
        public static final StreamCodec<RegistryFriendlyByteBuf, FilterTogglePayload> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.BOOL, FilterTogglePayload::enabled,
                        FilterTogglePayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record DiscardRequestPayload(int threshold) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<DiscardRequestPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CheapAwayNeoForge261.MOD_ID, "discard_request"));
        public static final StreamCodec<RegistryFriendlyByteBuf, DiscardRequestPayload> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, DiscardRequestPayload::threshold,
                        DiscardRequestPayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(FilterTogglePayload.TYPE, FilterTogglePayload.STREAM_CODEC, Network::handleToggle);
        registrar.playToServer(DiscardRequestPayload.TYPE, DiscardRequestPayload.STREAM_CODEC, Network::handleDiscard);
    }

    public static void handleToggle(FilterTogglePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer) {
                FilterState.getInstance().setFilterEnabled(payload.enabled());
            }
        });
    }

    public static void handleDiscard(DiscardRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            int threshold = payload.threshold();

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.isEmpty()) continue;

                int rarity = CheapAwayNeoForge261.PLATFORM_HELPER
                        .getRarityProvider().getRarity(stack);
                if (rarity <= threshold) {
                    player.drop(player.getInventory().removeItemNoUpdate(i), true, false);
                }
            }
        });
    }
}
