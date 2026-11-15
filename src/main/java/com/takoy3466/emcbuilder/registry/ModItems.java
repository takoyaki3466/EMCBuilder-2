package com.takoy3466.emcbuilder.registry;

import com.takoy3466.emcbuilder.EmcBuilder;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** アイテムの登録。 */
public final class ModItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(EmcBuilder.MODID);

    public static final DeferredItem<BlockItem> EMC_BUILDER = REGISTER.registerSimpleBlockItem(ModBlocks.EMC_BUILDER);

    private ModItems() {
    }
}
