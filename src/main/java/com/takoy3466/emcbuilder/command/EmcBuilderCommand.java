package com.takoy3466.emcbuilder.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.takoy3466.emcbuilder.emc.EmcPool;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * 管理用コマンド。
 * <pre>
 * /emcbuilder emc                       共有 EMC を表示（誰でも可）
 * /emcbuilder emc set|add &lt;amount&gt;      共有 EMC を変更（OP）
 * </pre>
 */
public final class EmcBuilderCommand {
    private EmcBuilderCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("emcbuilder")
                .then(Commands.literal("emc")
                        .executes(EmcBuilderCommand::showEmc)
                        .then(Commands.literal("set").requires(s -> s.hasPermission(2))
                                .then(Commands.argument("amount", LongArgumentType.longArg(0, EmcPool.LIMIT))
                                        .executes(ctx -> setEmc(ctx, LongArgumentType.getLong(ctx, "amount"), false))))
                        .then(Commands.literal("add").requires(s -> s.hasPermission(2))
                                .then(Commands.argument("amount", LongArgumentType.longArg())
                                        .executes(ctx -> setEmc(ctx, LongArgumentType.getLong(ctx, "amount"), true))))));
    }

    private static int showEmc(CommandContext<CommandSourceStack> ctx) {
        long emc = EmcPool.get(ctx.getSource().getServer()).getEmc();
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.emcbuilder.emc.show", emc), false);
        return (int) Math.min(Integer.MAX_VALUE, emc);
    }

    private static int setEmc(CommandContext<CommandSourceStack> ctx, long amount, boolean add) {
        EmcPool pool = EmcPool.get(ctx.getSource().getServer());
        if (add) {
            if (amount >= 0) {
                pool.refund(amount);
            } else {
                pool.set(pool.getEmc() + amount);
            }
        } else {
            pool.set(amount);
        }
        long emc = pool.getEmc();
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.emcbuilder.emc.set", emc), true);
        return 1;
    }
}
