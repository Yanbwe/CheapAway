package org.yanbwe.cheapaway.neoforge;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import org.yanbwe.cheapaway.core.FilterState;
import org.yanbwe.cheapaway.core.RarityFilter;

@EventBusSubscriber(modid = CheapAwayNeoForge1211.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PickupHandler {

    @SubscribeEvent
    public static void onItemPickupPre(ItemEntityPickupEvent.Pre event) {
        FilterState state = FilterState.getInstance();
        if (!state.isFilterEnabled()) return;

        ItemStack stack = event.getItemEntity().getItem();
        int rarity = CheapAwayNeoForge1211.PLATFORM_HELPER.getRarityProvider().getRarity(stack);

        if (!RarityFilter.shouldPickUp(rarity, state.getFilterThreshold(), true)) {
            event.setCanPickup(net.neoforged.neoforge.common.util.TriState.FALSE);
        }
    }
}
