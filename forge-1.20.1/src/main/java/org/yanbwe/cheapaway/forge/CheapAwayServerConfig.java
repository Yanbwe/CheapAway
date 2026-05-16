package org.yanbwe.cheapaway.forge;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class CheapAwayServerConfig {

    public final ForgeConfigSpec.BooleanValue filterEnabled;
    public final ForgeConfigSpec.IntValue filterThreshold;

    CheapAwayServerConfig(ForgeConfigSpec.Builder builder) {
        builder.push("filter");

        this.filterEnabled = builder
                .comment("Whether the auto-pickup filter is enabled by default on world start.")
                .define("filterEnabled", true);

        this.filterThreshold = builder
                .comment("Default rarity threshold. Items with rarity <= this value won't be picked up.")
                .defineInRange("filterThreshold", 3, 0, 7);

        builder.pop();
    }

    public static final ForgeConfigSpec SPEC;
    public static final CheapAwayServerConfig INSTANCE;

    static {
        Pair<CheapAwayServerConfig, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(CheapAwayServerConfig::new);
        SPEC = pair.getRight();
        INSTANCE = pair.getLeft();
    }
}
