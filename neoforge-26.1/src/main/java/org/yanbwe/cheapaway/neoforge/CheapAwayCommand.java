package org.yanbwe.cheapaway.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.yanbwe.cheapaway.core.FilterState;

@EventBusSubscriber(modid = CheapAwayNeoForge261.MOD_ID)
public class CheapAwayCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("cheapaway")
                        .then(Commands.literal("threshold")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 7))
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            FilterState.getInstance().setFilterThreshold(value);
                                            ctx.getSource().sendSystemMessage(
                                                    Component.literal("§aThreshold set to: " + value)
                                            );
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("toggle")
                                .executes(ctx -> {
                                    FilterState state = FilterState.getInstance();
                                    state.toggle();
                                    String status = state.isFilterEnabled() ? "§aON" : "§cOFF";
                                    ctx.getSource().sendSystemMessage(
                                            Component.literal("§ePickup filter: " + status)
                                    );
                                    return 1;
                                })
                        )
                        .then(Commands.literal("status")
                                .executes(ctx -> {
                                    FilterState state = FilterState.getInstance();
                                    String status = state.isFilterEnabled() ? "§aON" : "§cOFF";
                                    ctx.getSource().sendSystemMessage(
                                            Component.literal("§e--- CheapAway Status ---\n"
                                                    + "Filter: " + status + "\n"
                                                    + "Threshold: §b" + state.getFilterThreshold())
                                    );
                                    return 1;
                                })
                        )
        );
    }
}
