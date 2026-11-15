package com.takoy3466.emcbuilder;

import com.mojang.logging.LogUtils;
import com.takoy3466.emcbuilder.command.EmcBuilderCommand;
import com.takoy3466.emcbuilder.network.ModNetwork;
import com.takoy3466.emcbuilder.registry.ModBlockEntities;
import com.takoy3466.emcbuilder.registry.ModBlocks;
import com.takoy3466.emcbuilder.registry.ModItems;
import com.takoy3466.emcbuilder.registry.ModMenus;
import com.takoy3466.re_buildcraft.api.power.PoweredBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

/**
 * EMC Builder Standalone のエントリーポイント。
 * <p>
 * CompactEngine の「EMCビルダー」を NeoForge 1.21.1 向けに移植した Re-BuildCraft のアドオン。
 * 建築（設計図・MJ 動力・建設ロボット）は Re-BuildCraft のビルダーを継承して使い、
 * EMC 値の取得は ProjectE に委ねる（どちらも必須前提）。
 */
@Mod(EmcBuilder.MODID)
public final class EmcBuilder {
    public static final String MODID = "emcbuilder";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EmcBuilder(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.REGISTER.register(modEventBus);
        ModItems.REGISTER.register(modEventBus);
        ModBlockEntities.REGISTER.register(modEventBus);
        ModMenus.REGISTER.register(modEventBus);

        modEventBus.addListener(ModNetwork::register);
        modEventBus.addListener(EmcBuilder::registerCapabilities);
        modEventBus.addListener(EmcBuilder::addCreativeTabItems);

        NeoForge.EVENT_BUS.addListener(EmcBuilder::registerCommands);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        EmcBuilderCommand.register(event.getDispatcher());
    }

    /** BC のビルダーと同じく、MJ の受け手と資材スロット（パイプ・ホッパー用）を公開する。 */
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        PoweredBlockEntity.registerCapabilities(event, ModBlockEntities.EMC_BUILDER.get());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.EMC_BUILDER.get(),
                (builder, side) -> builder.getMaterials());
    }

    private static void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.EMC_BUILDER.get());
        }
    }
}
