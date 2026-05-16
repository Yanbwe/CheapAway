package org.yanbwe.cheapaway.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.yanbwe.cheapaway.core.PanelPosition;

@EventBusSubscriber(modid = CheapAwayNeoForge261.MOD_ID, value = Dist.CLIENT)
public class DiscardPanel extends AbstractWidget {

    private static final int PANEL_WIDTH = 120;
    private static final int PANEL_HEIGHT = 20;
    private static final Component NARRATION = Component.literal("Discard Panel");

    private final PanelPosition position;
    private final EditBox inputBox;
    private boolean confirmDiscard = false;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    public DiscardPanel(PanelPosition position) {
        super(position.getX(), position.getY(), PANEL_WIDTH, PANEL_HEIGHT, Component.empty());
        this.position = position;
        Font font = Minecraft.getInstance().font;
        this.inputBox = new EditBox(font, this.getX() + 16, this.getY() + 1, 50, 18, NARRATION);
        this.inputBox.setValue("3");
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
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        // 拖拽手柄
        int handleColor = dragging ? 0xFFAAAAAA : 0xFF666666;
        graphics.fill(this.getX(), this.getY(), this.getX() + 12, this.getY() + PANEL_HEIGHT, handleColor);
        graphics.text(font, Component.literal("\u2261"), this.getX() + 2, this.getY() + 5, 0xFFFFFFFF);

        // 输入框背景
        graphics.fill(this.getX() + 13, this.getY(), this.getX() + 80, this.getY() + PANEL_HEIGHT, 0xCC000000);
        this.inputBox.extractRenderState(graphics, mouseX, mouseY, partialTick);

        // 丢弃按钮
        int btnColor = confirmDiscard ? 0xFFFF4444 : 0xFF444444;
        graphics.fill(this.getX() + 82, this.getY(), this.getX() + PANEL_WIDTH, this.getY() + PANEL_HEIGHT, btnColor);
        graphics.text(font, Component.literal("\uD83D\uDDD1"), this.getX() + 84, this.getY() + 2, 0xFFFFFFFF);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        this.inputBox.setFocused(focused);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!isMouseOver(event.x(), event.y())) return false;

        // 拖拽手柄
        if (event.x() >= this.getX() && event.x() <= this.getX() + 12) {
            dragging = true;
            dragOffsetX = (int) event.x() - this.getX();
            dragOffsetY = (int) event.y() - this.getY();
            return true;
        }

        // 输入框 — 先 setFocused 再调 mouseClicked
        if (event.x() >= this.inputBox.getX() && event.x() <= this.inputBox.getX() + this.inputBox.getWidth()
                && event.y() >= this.inputBox.getY() && event.y() <= this.inputBox.getY() + this.inputBox.getHeight()) {
            this.inputBox.setFocused(true);
            this.inputBox.mouseClicked(event, doubleClick);
            return true;
        }

        // 丢弃按钮
        if (event.x() >= this.getX() + 82 && event.x() <= this.getX() + PANEL_WIDTH) {
            if (!confirmDiscard) {
                confirmDiscard = true;
            } else {
                int threshold = parseIntOrDefault(inputBox.getValue(), 3);
                ClientPacketDistributor.sendToServer(new Network.DiscardRequestPayload(threshold));
                confirmDiscard = false;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging) {
            int newX = (int) event.x() - dragOffsetX;
            int newY = (int) event.y() - dragOffsetY;
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
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = false;
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return this.inputBox.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return this.inputBox.charTyped(event);
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
