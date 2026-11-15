package com.takoy3466.emcbuilder.menu;

import com.takoy3466.emcbuilder.emc.EmcPool;
import com.takoy3466.emcbuilder.emc.EmcValues;
import com.takoy3466.emcbuilder.network.EmcSyncPayload;
import com.takoy3466.emcbuilder.registry.ModMenus;
import com.takoy3466.re_buildcraft.builders.BuilderBlockEntity;
import com.takoy3466.re_buildcraft.builders.BuilderMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * EMC ビルダーの GUI メニュー（元 {@code EMCBuilderContainer extends CraftingBuilder} 相当）。
 * <p>
 * スロットの並び（設計図・資材 27・プレイヤー・必要アイテム欄）は BC のビルダーと同じで、
 * 最後に EMC 投入スロット (133, 27) を足す。
 * EMC 投入スロットの操作（元実装と同じ）: 右クリック = 1 個、左クリック = 手持ち全部、
 * Shift + クリック = 手持ちとインベントリ内の同じアイテム全部を EMC に変換する。
 * EMC の加算はサーバー側でのみ行い、残量は {@link EmcSyncPayload} でクライアントへ送る（long のため）。
 */
public class EmcBuilderMenu extends BuilderMenu {
    private static final int EMC_SLOT_X = 133;
    private static final int EMC_SLOT_Y = 27;

    private final Player player;
    /** EMC 投入スロットの番号（BC のビルダーのスロットの後ろ）。 */
    public final int emcSlot;

    // サーバー側: 前回送った値 / クライアント側: 受け取った値
    private long lastSentEmc = -1;
    private long emc;

    public EmcBuilderMenu(int containerId, Inventory playerInventory, BuilderBlockEntity builder) {
        super(ModMenus.EMC_BUILDER.get(), containerId, playerInventory, builder);
        this.player = playerInventory.player;
        this.emcSlot = addSlot(new EmcInputSlot(EMC_SLOT_X, EMC_SLOT_Y)).index;
    }

    public static EmcBuilderMenu createClient(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        if (playerInventory.player.level().getBlockEntity(extraData.readBlockPos()) instanceof BuilderBlockEntity builder) {
            return new EmcBuilderMenu(containerId, playerInventory, builder);
        }
        throw new IllegalStateException("EMC builder menu opened for a position without a builder");
    }

    // ---------------------------------------------------------------- EMC 投入

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId == emcSlot) {
            if (!player.level().isClientSide) {
                insertEmc(button, clickType, player);
            }
            return; // クライアントは予測せず、サーバーからの同期結果を待つ
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return slot.index != emcSlot && super.canDragTo(slot);
    }

    private void insertEmc(int button, ClickType clickType, Player player) {
        ItemStack carried = getCarried();
        long unitValue = EmcValues.sellValue(carried);
        MinecraftServer server = player.getServer();
        if (carried.isEmpty() || unitValue <= 0 || server == null) {
            return;
        }
        EmcPool pool = EmcPool.get(server);
        if (clickType == ClickType.PICKUP && button == 1) {
            convert(pool, carried, unitValue, 1);
        } else if (clickType == ClickType.PICKUP && button == 0) {
            convert(pool, carried, unitValue, carried.getCount());
        } else if (clickType == ClickType.QUICK_MOVE) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.items.size() && !pool.isFull(); i++) {
                ItemStack stack = inventory.items.get(i);
                if (ItemStack.isSameItemSameComponents(stack, carried)) {
                    convert(pool, stack, unitValue, stack.getCount());
                }
            }
            convert(pool, carried, unitValue, carried.getCount());
        }
        setCarried(carried.isEmpty() ? ItemStack.EMPTY : carried);
        broadcastChanges();
    }

    private static void convert(EmcPool pool, ItemStack stack, long unitValue, int count) {
        int accepted = pool.deposit(unitValue, Math.min(count, stack.getCount()));
        stack.shrink(accepted);
    }

    // ---------------------------------------------------------------- EMC の同期

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.getServer() == null) {
            return;
        }
        long current = EmcPool.get(serverPlayer.getServer()).getEmc();
        if (current != lastSentEmc && serverPlayer.connection != null && serverPlayer.connection.hasChannel(EmcSyncPayload.TYPE)) {
            lastSentEmc = current;
            PacketDistributor.sendToPlayer(serverPlayer, new EmcSyncPayload(containerId, current));
        }
    }

    /** クライアント側: 受信した EMC 残量を反映する。 */
    public void setEmc(long emc) {
        this.emc = emc;
    }

    /** クライアント側: 共有 EMC の残量（表示用）。 */
    public long getEmc() {
        return emc;
    }
}
