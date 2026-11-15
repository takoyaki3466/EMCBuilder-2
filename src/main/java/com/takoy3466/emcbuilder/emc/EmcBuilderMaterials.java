package com.takoy3466.emcbuilder.emc;

import com.takoy3466.re_buildcraft.builders.blueprint.BuilderMaterials;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * EMC ビルダーの材料判定（元 {@code EMCBuilderBpt} 相当）。
 * <p>
 * 元実装の {@code checkRequirements} / {@code useItem} と同じく、1 マスごとに:
 * <ol>
 *   <li>資材スロットと隣接インベントリに必要なアイテムがすべてあれば、それを使う（BC のビルダーと同じ）。</li>
 *   <li>無ければ、必要なアイテムすべての EMC 合計が共有プール以下なら、その分の EMC を使う。
 *       EMC 値が 0 のアイテムが 1 つでも含まれていれば EMC では建てられない。</li>
 * </ol>
 * テンプレート（{@link #takeAny()}）は元実装と同じく EMC を使わず、BC のビルダーのまま。
 */
public final class EmcBuilderMaterials extends BuilderMaterials {
    private final MinecraftServer server;

    public EmcBuilderMaterials(BuilderMaterials items, MinecraftServer server) {
        super(items);
        this.server = server;
    }

    @Override
    public boolean hasAll(List<ItemStack> requirements) {
        return super.hasAll(requirements) || canPayWithEmc(requirements);
    }

    @Override
    public void useAll(List<ItemStack> requirements) {
        if (super.hasAll(requirements)) {
            super.useAll(requirements);
            return;
        }
        long cost = EmcValues.costOf(requirements);
        if (cost >= 0) {
            EmcPool.get(server).withdraw(cost);
        }
    }

    private boolean canPayWithEmc(List<ItemStack> requirements) {
        long cost = EmcValues.costOf(requirements);
        return cost != EmcValues.NO_VALUE && cost <= EmcPool.get(server).getEmc();
    }
}
