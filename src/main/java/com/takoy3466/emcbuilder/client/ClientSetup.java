package com.takoy3466.emcbuilder.client;

import com.takoy3466.emcbuilder.EmcBuilder;
import com.takoy3466.emcbuilder.registry.ModBlockEntities;
import com.takoy3466.emcbuilder.registry.ModMenus;
import com.takoy3466.re_buildcraft.client.BuilderRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** クライアント専用の登録処理。 */
@EventBusSubscriber(modid = EmcBuilder.MODID, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.EMC_BUILDER.get(), EmcBuilderScreen::new);
    }

    /** 作業範囲のレーザーと建設ロボットの描画は BC のビルダーと同じ描画を使う。 */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.EMC_BUILDER.get(), BuilderRenderer::new);
    }
}
