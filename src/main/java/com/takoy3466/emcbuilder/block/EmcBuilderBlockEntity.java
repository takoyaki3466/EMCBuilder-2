package com.takoy3466.emcbuilder.block;

import com.takoy3466.emcbuilder.emc.EmcBuilderMaterials;
import com.takoy3466.emcbuilder.menu.EmcBuilderMenu;
import com.takoy3466.emcbuilder.registry.ModBlockEntities;
import com.takoy3466.re_buildcraft.builders.BuilderBlockEntity;
import com.takoy3466.re_buildcraft.builders.blueprint.BuilderMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * EMC ビルダー（元 {@code EMCBuilderTile extends TileBuilder} 相当）。
 * <p>
 * 建築の手順・MJ 動力・建設ロボット・緑マーカーの経路・保存は Re-BuildCraft のビルダーそのもの。
 * 元実装が {@code createBptBuilderBase} だけを差し替えていたのと同じく、材料の判定だけを
 * {@link EmcBuilderMaterials}（資材 → 共有 EMC の順）に差し替える。
 */
public class EmcBuilderBlockEntity extends BuilderBlockEntity {

    public EmcBuilderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EMC_BUILDER.get(), pos, state);
    }

    @Override
    protected BuilderMaterials createMaterials(ServerLevel serverLevel) {
        return new EmcBuilderMaterials(super.createMaterials(serverLevel), serverLevel.getServer());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EmcBuilderMenu(containerId, playerInventory, this);
    }
}
