package org.yanbwe.cheapaway.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.yanbwe.cheapaway.core.PanelPosition;

/**
 * 背包批量丢弃浮动面板。通过 ScreenEvent.Init.Post 注入到 InventoryScreen。
 */
@Mod.EventBusSubscriber(modid = CheapAwayForge.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DiscardPanel extends AbstractWidget {

    private static final int PANEL_WIDTH = 120;
    private static final int PANEL_HEIGHT = 20;
    private static final Component PLACEHOLDER = Component.literal("3");

    private final PanelPosition position;
    private final EditBox inputBox;
    private boolean confirmDiscard = false;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    public DiscardPanel(PanelPosition position) {
        super(position.getX(), position.getY(), PANEL_WIDTH, PANEL_HEIGHT, Component.empty());
        this.position = position;
        this.inputBox = new EditBox(Minecraft.getInstance().font,
                this.getX() + 16, this.getY() + 1, 50, 18, PLACEHOLDER);
        this.inputBox.setValue("3");
        this.inputBox.setFilter(s -> s.matches("\\d*"));
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        position.setX(x);
        inputBox.setX(x + 16);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        position.setY(y);
        inputBox.setY(y + 1);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        // 拖拽手柄
        int handleColor = dragging ? 0xFFAAAAAA : 0xFF666666;
        graphics.fill(this.getX(), this.getY(), this.getX() + 12, this.getY() + PANEL_HEIGHT, handleColor);
        graphics.drawString(font, "≡", this.getX() + 2, this.getY() + 5, 0xFFFFFFFF);

        // 输入框背景
        graphics.fill(this.getX() + 13, this.getY(), this.getX() + 80, this.getY() + PANEL_HEIGHT, 0xCC000000);
        this.inputBox.render(graphics, mouseX, mouseY, partialTick);

        // 丢弃按钮（垃圾桶）
        int btnColor = confirmDiscard ? 0xFFFF4444 : 0xFF444444;
        graphics.fill(this.getX() + 82, this.getY(), this.getX() + PANEL_WIDTH, this.getY() + PANEL_HEIGHT, btnColor);
        graphics.drawString(font, "🗑", this.getX() + 84, this.getY() + 2, 0xFFFFFFFF);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        this.inputBox.setFocused(focused);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        // 手柄拖拽
        if (mouseX >= this.getX() && mouseX <= this.getX() + 12
                && mouseY >= this.getY() && mouseY <= this.getY() + PANEL_HEIGHT) {
            dragging = true;
            dragOffsetX = (int) mouseX - this.getX();
            dragOffsetY = (int) mouseY - this.getY();
            return true;
        }

        // 输入框 — 先 setFocused 再调 mouseClicked，确保 canConsumeInput 通过
        if (mouseX >= this.inputBox.getX() && mouseX <= this.inputBox.getX() + this.inputBox.getWidth()
                && mouseY >= this.inputBox.getY() && mouseY <= this.inputBox.getY() + this.inputBox.getHeight()) {
            this.inputBox.setFocused(true);
            this.inputBox.mouseClicked(mouseX, mouseY, button);
            return true;
        }

        // 丢弃按钮
        if (mouseX >= this.getX() + 82 && mouseX <= this.getX() + PANEL_WIDTH
                && mouseY >= this.getY() && mouseY <= this.getY() + PANEL_HEIGHT) {
            if (!confirmDiscard) {
                confirmDiscard = true;
            } else {
                int threshold = parseIntOrDefault(inputBox.getValue(), 3);
                Network.INSTANCE.sendToServer(new Network.DiscardRequestPacket(threshold));
                confirmDiscard = false;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            int newX = (int) mouseX - dragOffsetX;
            int newY = (int) mouseY - dragOffsetY;
            // 限制在屏幕范围内
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null) {
                newX = Math.max(0, Math.min(newX, mc.screen.width - PANEL_WIDTH));
                newY = Math.max(0, Math.min(newY, mc.screen.height - PANEL_HEIGHT));
            }
            this.setX(newX);
            this.setY(newY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.inputBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return this.inputBox.charTyped(codePoint, modifiers);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    private static int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
    }

    // --- Screen injection ---

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen) {
            DiscardPanel panel = new DiscardPanel(new PanelPosition());
            event.addListener(panel);
        }
    }
}
