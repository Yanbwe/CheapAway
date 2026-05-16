package org.yanbwe.cheapaway.forge;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.yanbwe.cheapaway.core.FilterState;
import org.yanbwe.cheapaway.core.RarityFilter;

@Mod.EventBusSubscriber(modid = CheapAwayForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PickupHandler {

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (!event.isCancelable()) return;

        FilterState state = FilterState.getInstance();
        if (!state.isFilterEnabled()) return;

        ItemEntity itemEntity = event.getItem();
        ItemStack stack = itemEntity.getItem();

        int rarity = CheapAwayForge.PLATFORM_HELPER.getRarityProvider().getRarity(stack);
        if (!RarityFilter.shouldPickUp(rarity, state.getFilterThreshold(), true)) {
            event.setCanceled(true);
        }
    }
}
