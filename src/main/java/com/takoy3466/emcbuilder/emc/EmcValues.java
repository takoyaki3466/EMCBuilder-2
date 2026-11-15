package com.takoy3466.emcbuilder.emc;

import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * アイテムの EMC 価値を ProjectE から取得する窓口（元 {@code CE.getEMC} 相当。元は EE2 の値を参照していた）。
 * <p>
 * EMC 値が定義されていないアイテムは 0 を返し、EMC による生成は不可として扱う。
 */
public final class EmcValues {
    /** {@link #costOf(List)} が「値の無いアイテムが含まれる」ことを示す戻り値。 */
    public static final long NO_VALUE = -1;

    private EmcValues() {
    }

    /** 建築でアイテム 1 個を EMC から生成する際のコスト（ProjectE の購入価格）。 */
    public static long buildValue(ItemStack stack) {
        return stack.isEmpty() ? 0 : IEMCProxy.INSTANCE.getValue(stack.copyWithCount(1));
    }

    /** アイテム 1 個を EMC 投入スロットに入れた際に得られる EMC（ProjectE の売却価格）。 */
    public static long sellValue(ItemStack stack) {
        return stack.isEmpty() ? 0 : IEMCProxy.INSTANCE.getSellValue(stack.copyWithCount(1));
    }

    /**
     * 必要アイテム一覧を EMC で生成する合計コスト。
     *
     * @return 合計 EMC。値の無いアイテムが含まれる場合は {@link #NO_VALUE}。溢れる場合は {@link Long#MAX_VALUE}
     */
    public static long costOf(List<ItemStack> requirements) {
        long total = 0;
        for (ItemStack stack : requirements) {
            long unit = buildValue(stack);
            if (unit <= 0) {
                return NO_VALUE;
            }
            try {
                total = Math.addExact(total, Math.multiplyExact(unit, (long) stack.getCount()));
            } catch (ArithmeticException overflow) {
                return Long.MAX_VALUE;
            }
        }
        return total;
    }
}
