package org.yanbwe.cheapaway.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.yanbwe.cheapaway.core.FilterState;

@Mod.EventBusSubscriber(modid = CheapAwayForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CheapAwayCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("cheapaway")
                        .requires(source -> source.hasPermission(0))
                        .then(Commands.literal("threshold")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 7))
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            FilterState.getInstance().setFilterThreshold(value);
                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal("§aThreshold set to: " + value),
                                                    false
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
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("§ePickup filter: " + status),
                                            false
                                    );
                                    return 1;
                                })
                        )
                        .then(Commands.literal("status")
                                .executes(ctx -> {
                                    FilterState state = FilterState.getInstance();
                                    String status = state.isFilterEnabled() ? "§aON" : "§cOFF";
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("§e--- CheapAway Status ---\n"
                                                    + "Filter: " + status + "\n"
                                                    + "Threshold: §b" + state.getFilterThreshold()),
                                            false
                                    );
                                    return 1;
                                })
                        )
        );
    }
}
