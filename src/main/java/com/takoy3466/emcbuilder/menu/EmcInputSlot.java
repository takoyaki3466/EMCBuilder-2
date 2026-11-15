package com.takoy3466.emcbuilder.menu;

import com.takoy3466.emcbuilder.emc.EmcValues;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;

/**
 * EMC 投入スロット（元 {@code EMCBuilderSlot} 相当）。
 * <p>
 * 中身を持たない「吸い込み口」で、クリック処理は {@link EmcBuilderMenu#clicked} が行う。
 * EMC 値を持つアイテムだけを受け付ける。
 */
public class EmcInputSlot extends Slot {

    public EmcInputSlot(int x, int y) {
        super(new SimpleContainer(1), 0, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return EmcValues.sellValue(stack) > 0;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void set(ItemStack stack) {
        // 中身を保持しない
    }

    @Override
    public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
        // 中身を保持しない
    }
}
