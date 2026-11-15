package com.takoy3466.emcbuilder.registry;

import com.takoy3466.emcbuilder.EmcBuilder;
import com.takoy3466.emcbuilder.block.EmcBuilderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/** ブロックの登録。 */
public final class ModBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(EmcBuilder.MODID);

    public static final DeferredBlock<EmcBuilderBlock> EMC_BUILDER = REGISTER.registerBlock("emc_builder", EmcBuilderBlock::new,
            BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F, 6.0F).sound(SoundType.METAL));

    private ModBlocks() {
    }
}
