package com.takoy3466.emcbuilder.block;

import com.takoy3466.re_buildcraft.builders.BuilderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * EMC ビルダーのブロック（元 {@code EMCBuilderBlock extends BlockBuilder} 相当）。
 * 向き（置いた人の方が正面、レンチで回転）、GUI を開く処理、壊したときの中身のドロップは BC のビルダーと同じ。
 */
public class EmcBuilderBlock extends BuilderBlock {

    public EmcBuilderBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EmcBuilderBlockEntity(pos, state);
    }
}
