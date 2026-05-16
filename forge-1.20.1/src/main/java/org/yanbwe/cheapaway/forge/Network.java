package org.yanbwe.cheapaway.forge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.yanbwe.cheapaway.core.FilterState;

import java.util.function.Supplier;

public class Network {

    private static final String PROTOCOL = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CheapAwayForge.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++, FilterTogglePacket.class,
                FilterTogglePacket::encode, FilterTogglePacket::decode, FilterTogglePacket::handle);
        INSTANCE.registerMessage(id++, DiscardRequestPacket.class,
                DiscardRequestPacket::encode, DiscardRequestPacket::decode, DiscardRequestPacket::handle);
    }

    // --- FilterTogglePacket ---
    public static class FilterTogglePacket {
        private final boolean enabled;

        public FilterTogglePacket(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() { return enabled; }

        public static void encode(FilterTogglePacket msg, FriendlyByteBuf buf) {
            buf.writeBoolean(msg.enabled);
        }

        public static FilterTogglePacket decode(FriendlyByteBuf buf) {
            return new FilterTogglePacket(buf.readBoolean());
        }

        public static void handle(FilterTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender != null) {
                    FilterState.getInstance().setFilterEnabled(msg.enabled);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    // --- DiscardRequestPacket ---
    public static class DiscardRequestPacket {
        private final int threshold;

        public DiscardRequestPacket(int threshold) {
            this.threshold = threshold;
        }

        public int getThreshold() { return threshold; }

        public static void encode(DiscardRequestPacket msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.threshold);
        }

        public static DiscardRequestPacket decode(FriendlyByteBuf buf) {
            return new DiscardRequestPacket(buf.readInt());
        }

        public static void handle(DiscardRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;

                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (stack.isEmpty()) continue;

                    int rarity = CheapAwayForge.PLATFORM_HELPER
                            .getRarityProvider().getRarity(stack);
                    if (rarity <= msg.getThreshold()) {
                        player.drop(player.getInventory().removeItemNoUpdate(i), true, false);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
