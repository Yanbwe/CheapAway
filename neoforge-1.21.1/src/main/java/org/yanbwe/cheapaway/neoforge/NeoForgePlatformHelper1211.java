package org.yanbwe.cheapaway.neoforge;

import net.minecraft.world.item.ItemStack;
import org.yanbwe.cheapaway.api.IPlatformHelper;
import org.yanbwe.cheapaway.api.IRarityProvider;
import org.yanbwe.raritycore.api.RarityCoreAPI;

public class NeoForgePlatformHelper1211 implements IPlatformHelper {

    private static final IRarityProvider RARITY_PROVIDER = itemStack -> {
        if (itemStack instanceof ItemStack is) {
            return RarityCoreAPI.getRarity(is);
        }
        return 1;
    };

    @Override
    public IRarityProvider getRarityProvider() {
        return RARITY_PROVIDER;
    }

    @Override
    public String getPlatformName() {
        return "neoforge-1.21.1";
    }
}
