package org.yanbwe.cheapaway.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class CheapAwayServerConfig {

    public final ModConfigSpec.BooleanValue filterEnabled;
    public final ModConfigSpec.IntValue filterThreshold;

    CheapAwayServerConfig(ModConfigSpec.Builder builder) {
        builder.push("filter");

        this.filterEnabled = builder
                .comment("Whether the auto-pickup filter is enabled by default on world start.")
                .define("filterEnabled", true);

        this.filterThreshold = builder
                .comment("Default rarity threshold. Items with rarity <= this value won't be picked up.")
                .defineInRange("filterThreshold", 3, 0, 7);

        builder.pop();
    }

    public static final ModConfigSpec SPEC;
    public static final CheapAwayServerConfig INSTANCE;

    static {
        Pair<CheapAwayServerConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(CheapAwayServerConfig::new);
        SPEC = pair.getRight();
        INSTANCE = pair.getLeft();
    }
}
