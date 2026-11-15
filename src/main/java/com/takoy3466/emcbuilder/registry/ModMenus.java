package com.takoy3466.emcbuilder.registry;

import com.takoy3466.emcbuilder.EmcBuilder;
import com.takoy3466.emcbuilder.menu.EmcBuilderMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** メニュー（コンテナ）の登録。 */
public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, EmcBuilder.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<EmcBuilderMenu>> EMC_BUILDER =
            REGISTER.register("emc_builder", () -> IMenuTypeExtension.create(EmcBuilderMenu::createClient));

    private ModMenus() {
    }
}
