package com.takoy3466.emcbuilder.registry;

import com.takoy3466.emcbuilder.EmcBuilder;
import com.takoy3466.emcbuilder.block.EmcBuilderBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** ブロックエンティティの登録。 */
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EmcBuilder.MODID);

    @SuppressWarnings("DataFlowIssue") // データフィクサー型は不要
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmcBuilderBlockEntity>> EMC_BUILDER = REGISTER.register("emc_builder",
            () -> BlockEntityType.Builder.of(EmcBuilderBlockEntity::new, ModBlocks.EMC_BUILDER.get()).build(null));

    private ModBlockEntities() {
    }
}
