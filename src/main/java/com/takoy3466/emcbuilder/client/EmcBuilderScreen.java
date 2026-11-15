package com.takoy3466.emcbuilder.client;

import com.takoy3466.emcbuilder.EmcBuilder;
import com.takoy3466.emcbuilder.menu.EmcBuilderMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

/**
 * EMC ビルダーの GUI（元 {@code EMCBuilderGui extends GuiBuilder} 相当）。
 * <p>
 * BC のビルダーの GUI と同じ並びで、背景は元の EMC ビルダーのテクスチャ（EMC 投入スロット付き）を使う。
 * 設計図を建てている間は右側に必要アイテム欄のある幅広の背景に切り替える。共有 EMC の残量を右寄せで表示する。
 */
public class EmcBuilderScreen extends AbstractContainerScreen<EmcBuilderMenu> {
    private static final ResourceLocation TEXTURE = EmcBuilder.id("textures/gui/emc_builder.png");
    private static final ResourceLocation TEXTURE_BLUEPRINT = EmcBuilder.id("textures/gui/emc_builder_blueprint.png");
    private static final int TEXT_COLOR = 0x404040;
    private static final int BASE_WIDTH = 176;
    private static final int WIDE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 225;
    private static final int NEEDED_PANEL_HEIGHT = 140;
    private static final Component RESOURCES = Component.translatable("gui.re_buildcraft.builder.resources");
    private static final Component NEEDED = Component.translatable("gui.re_buildcraft.builder.needed");

    public EmcBuilderScreen(EmcBuilderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = BASE_WIDTH;
        this.imageHeight = IMAGE_HEIGHT;
        this.titleLabelX = 73;
        this.titleLabelY = 12;
        this.inventoryLabelY = IMAGE_HEIGHT - 97;
    }

    private boolean wide() {
        return menu.getBuilder().isBuildingBlueprint();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.index == menu.emcSlot) {
            graphics.renderComponentTooltip(font, List.of(
                    Component.translatable("gui.emcbuilder.emc_slot").withStyle(ChatFormatting.YELLOW),
                    Component.translatable("gui.emcbuilder.emc_slot.left").withStyle(ChatFormatting.GRAY),
                    Component.translatable("gui.emcbuilder.emc_slot.right").withStyle(ChatFormatting.GRAY),
                    Component.translatable("gui.emcbuilder.emc_slot.shift").withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        boolean wide = wide();
        graphics.blit(wide ? TEXTURE_BLUEPRINT : TEXTURE, leftPos, topPos, 0, 0, wide ? WIDE_WIDTH : BASE_WIDTH, imageHeight, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(font, RESOURCES, 8, 60, TEXT_COLOR, false);
        if (wide()) {
            graphics.drawString(font, NEEDED, 185, 7, TEXT_COLOR, false);
        }
        String emc = Component.translatable("gui.emcbuilder.emc", String.format("%,d", menu.getEmc())).getString();
        graphics.drawString(font, emc, 160 - font.width(emc), 48, TEXT_COLOR, false);
    }

    /** 右側の必要アイテム欄の余白をクリックしても手持ちアイテムを捨てないようにする。 */
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        if (wide() && mouseX >= guiLeft && mouseX < guiLeft + WIDE_WIDTH && mouseY >= guiTop && mouseY < guiTop + NEEDED_PANEL_HEIGHT) {
            return false;
        }
        return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton);
    }
}
