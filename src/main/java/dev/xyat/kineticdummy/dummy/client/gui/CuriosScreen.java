package dev.xyat.kineticdummy.dummy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.xyat.kineticcore.api.client.AdaptiveItemGridRenderer;
import dev.xyat.kineticcore.api.client.ItemSelectorScreen;
import dev.xyat.kineticcore.api.client.ScaledScreen;
import dev.xyat.kineticdummy.dummy.DummyUtils;
import dev.xyat.kineticdummy.dummy.DummyMenu;
import dev.xyat.kineticdummy.dummy.client.NotifyManager;
import dev.xyat.kineticdummy.dummy.CuriosCompat;
import dev.xyat.kineticdummy.dummy.entity.DummyEntityTest;
import dev.xyat.kineticdummy.dummy.Network.DummyNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CuriosScreen extends ScaledScreen {
    private final Screen parent;
    private final DummyMenu menu;
    private final DummyEntityTest dummy;

    private static final int COLUMNS = 9;
    private static final int VISIBLE_ROWS = 4;
    private static final int SLOT_SIZE = 18;

    private static final ResourceLocation INVENTORY_TEX = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    private int totalSlots;
    private int maxRows;
    private int canvasW, canvasH;
    private int startX, startY;
    private int gridStartX, gridStartY;
    private int playerInvX, playerInvY;

    private float scrollOffs = 0.0F;
    private boolean isScrolling = false;
    private int maxScrollOffset;

    public CuriosScreen(Screen parent, DummyMenu menu) {
        super(Component.translatable("gui.kineticdummy.dummy.curios_ext.title"));
        this.parent = parent;
        this.menu = menu;
        this.dummy = menu.entity;
        configureResponsiveCanvas(
                640f,
                360f,
                6
        );
    }

    @Override
    protected void initScaled() {
        int cx = this.vWidth / 2;
        int cy = this.vHeight / 2;

        this.totalSlots = CuriosCompat.getSlotCount(dummy);
        this.maxRows = (int) Math.ceil((double) this.totalSlots / COLUMNS);
        this.maxScrollOffset = Math.max(0, this.maxRows - VISIBLE_ROWS);

        int gridHeight = VISIBLE_ROWS * SLOT_SIZE;
        this.canvasW = 200;
        this.canvasH = 220;
        this.startX = cx - this.canvasW / 2;
        this.startY = cy - this.canvasH / 2;

        this.gridStartX = this.startX + 15;
        this.gridStartY = this.startY + 35;

        this.playerInvX = cx - 88;
        this.playerInvY = this.gridStartY + gridHeight + 15;

        this.addRenderableWidget(Button.builder(Component.translatable("gui.kineticdummy.dummy.back"), b -> {
            if (this.minecraft != null) this.minecraft.setScreen(parent);
        }).bounds(this.startX + this.canvasW - 45, this.startY + 8, 40, 20).build());
    }

    private void notifyBlacklist() {
        NotifyManager.notify(Component.translatable("msg.kineticdummy.dummy.blacklisted"));
    }

    private void notifyNotCurio() {
        NotifyManager.notify(Component.translatable("msg.kineticdummy.dummy.not_a_curio"));
    }

    // 判断该物品是否有包含 curios 的标签
    private boolean isValidCurio(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getTags().noneMatch(t -> t.location().getNamespace().equals("curios"));
    }

    @Override
    protected void renderScaledBackground(@NotNull GuiGraphics g, int mx, int my, float pt) {
        int cx = this.vWidth / 2;

        g.fill(startX + 4, startY + 4, startX + canvasW + 4, startY + canvasH + 4, 0xAA000000);
        g.fill(startX, startY, startX + canvasW, startY + canvasH, 0xEE222222);
        g.renderOutline(startX - 1, startY - 1, canvasW + 2, canvasH + 2, 0xFF777777);

        g.drawCenteredString(this.font, this.title, cx, startY + 14, 0xFFFFAA00);

        int gridViewH = VISIBLE_ROWS * SLOT_SIZE;
        int gridViewW = COLUMNS * SLOT_SIZE;

        if (this.totalSlots <= 0) {
            g.drawCenteredString(this.font, Component.translatable("gui.kineticdummy.dummy.curios_ext.no_slots"), cx, gridStartY + gridViewH / 2, 0xFF5555);
        } else {
            g.fill(gridStartX, gridStartY, gridStartX + gridViewW, gridStartY + gridViewH, 0x80000000);

            int gridColor = 0xFFFFAA00;
            for (int c = 0; c <= COLUMNS; c++) g.fill(gridStartX + c * SLOT_SIZE, gridStartY, gridStartX + c * SLOT_SIZE + 1, gridStartY + gridViewH, gridColor);
            for (int r = 0; r <= VISIBLE_ROWS; r++) g.fill(gridStartX, gridStartY + r * SLOT_SIZE, gridStartX + gridViewW, gridStartY + r * SLOT_SIZE + 1, gridColor);

            int startRow = (int) (this.scrollOffs * this.maxScrollOffset + 0.5D);
            int startIdx = startRow * COLUMNS;
            int endIdx = Math.min(startIdx + (VISIBLE_ROWS * COLUMNS), totalSlots);

            enableVirtualScissor(
                    g,
                    gridStartX,
                    gridStartY,
                    gridStartX + gridViewW + 1,
                    gridStartY + gridViewH + 1
            );
            for (int i = startIdx; i < endIdx; i++) {
                int displayIdx = i - startIdx;
                int x = gridStartX + (displayIdx % COLUMNS) * SLOT_SIZE;
                int y = gridStartY + (displayIdx / COLUMNS) * SLOT_SIZE;

                ItemStack stack = CuriosCompat.getCurioItem(dummy, i);
                boolean hovered = mx >= x && mx < x + SLOT_SIZE && my >= y && my < y + SLOT_SIZE;
                AdaptiveItemGridRenderer.drawSlot(g, stack, x, y, SLOT_SIZE, 4, hovered);
                if (!stack.isEmpty()) {
                    g.renderItem(stack, x + 1, y + 1);
                    g.renderItemDecorations(this.font, stack, x + 1, y + 1, null);
                }
            }
            g.disableScissor();

            if (this.maxScrollOffset > 0) {
                int scrollX = gridStartX + gridViewW + 2;
                g.fill(scrollX, gridStartY, scrollX + 10, gridStartY + gridViewH, 0xFF000000);
                int thumbH = Math.max(15, (int) ((float) VISIBLE_ROWS / this.maxRows * gridViewH));
                int thumbY = gridStartY + (int) (this.scrollOffs * (gridViewH - thumbH));
                g.fill(scrollX + 1, thumbY, scrollX + 9, thumbY + thumbH, 0xFFAAAAAA);
            }
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        g.blit(INVENTORY_TEX, playerInvX, playerInvY, 0, 125, 176, 90);

        if (this.minecraft != null && this.minecraft.player != null) {
            Inventory inv = this.minecraft.player.getInventory();
            for (int i = 0; i < 36; i++) {
                int col = (i < 9) ? i : (i - 9) % 9;
                int row = (i < 9) ? 3 : (i - 9) / 9;
                int px = playerInvX + 7 + col * 18;
                int py = playerInvY + (row == 3 ? 72 : 14 + row * 18);
                ItemStack stack = inv.getItem(i);
                boolean hovered = mx >= px && mx < px + SLOT_SIZE && my >= py && my < py + SLOT_SIZE;
                AdaptiveItemGridRenderer.drawSlot(g, stack, px, py, SLOT_SIZE, 4, hovered);
                if (!stack.isEmpty()) {
                    g.renderItem(stack, px + 1, py + 1);
                    g.renderItemDecorations(this.font, stack, px + 1, py + 1, null);
                }
            }
        }
    }

    @Override
    protected void renderScaledForeground(@NotNull GuiGraphics g, int mx, int my, float pt) {
        NotifyManager.renderAt(g, this.vWidth / 2, this.playerInvY + 103);

        ItemStack hoveredStack = ItemStack.EMPTY;
        boolean isCurioHovered = false;

        if (this.totalSlots > 0) {
            int startRow = (int) (this.scrollOffs * this.maxScrollOffset + 0.5D);
            int startIdx = startRow * COLUMNS;
            int endIdx = Math.min(startIdx + (VISIBLE_ROWS * COLUMNS), totalSlots);
            for (int i = startIdx; i < endIdx; i++) {
                int displayIdx = i - startIdx;
                int x = gridStartX + (displayIdx % COLUMNS) * SLOT_SIZE;
                int y = gridStartY + (displayIdx / COLUMNS) * SLOT_SIZE;
                if (mx >= x && mx < x + 18 && my >= y && my < y + 18) {
                    hoveredStack = CuriosCompat.getCurioItem(dummy, i);
                    isCurioHovered = true;
                }
            }
        }
        if (this.minecraft != null && this.minecraft.player != null && !isCurioHovered) {
            Inventory inv = this.minecraft.player.getInventory();
            for (int i = 0; i < 36; i++) {
                int col = (i < 9) ? i : (i - 9) % 9;
                int row = (i < 9) ? 3 : (i - 9) / 9;
                int px = playerInvX + 7 + col * 18;
                int py = playerInvY + (row == 3 ? 72 : 14 + row * 18);
                if (mx >= px && mx < px + 18 && my >= py && my < py + 18) hoveredStack = inv.getItem(i);
            }
        }

        if (!hoveredStack.isEmpty() || isCurioHovered) renderSlotTooltip(g, hoveredStack, mx, my, isCurioHovered);

        ItemStack carried = null;
        if (this.minecraft != null && this.minecraft.player != null) carried = this.minecraft.player.containerMenu.getCarried();
        if (carried != null && !carried.isEmpty()) {
            g.renderItem(carried, mx - 8, my - 8);
            g.renderItemDecorations(this.font, carried, mx - 8, my - 8, null);
        }
    }

    private void renderSlotTooltip(GuiGraphics g, ItemStack stack, int mx, int my, boolean isCurioSlot) {
        List<Component> tooltip = new ArrayList<>();
        if (!stack.isEmpty()) {
            if (this.minecraft != null && this.minecraft.player != null) {
                tooltip.addAll(stack.getTooltipLines(this.minecraft.player,
                        this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
            }
        } else if (isCurioSlot) {
            tooltip.add(Component.translatable("gui.kineticdummy.dummy.slot.curio"));
        } else return;

        if (isCurioSlot) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("gui.kineticdummy.dummy.tooltip.curio.copy"));
            tooltip.add(Component.translatable("gui.kineticdummy.dummy.tooltip.curio.remove"));
        } else {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("gui.kineticdummy.dummy.tooltip.inv.copy"));
            tooltip.add(Component.translatable("gui.kineticdummy.dummy.tooltip.inv.quick"));
        }
        g.renderComponentTooltip(this.font, tooltip, mx, my);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        double vMx = toVirtualX(mx);
        double vMy = toVirtualY(my);

        if (super.mouseClicked(mx, my, btn)) return true;

        int gridViewH = VISIBLE_ROWS * SLOT_SIZE;
        if (this.maxScrollOffset > 0 && btn == 0) {
            int scrollX = gridStartX + COLUMNS * SLOT_SIZE + 2;
            if (vMx >= scrollX && vMx < scrollX + 10 && vMy >= gridStartY && vMy < gridStartY + gridViewH) {
                this.isScrolling = true;
                return true;
            }
        }

        ItemStack cursorStack = null;
        if (this.minecraft != null && this.minecraft.player != null) cursorStack = this.minecraft.player.containerMenu.getCarried();

        if (this.totalSlots > 0 && vMy >= gridStartY && vMy < gridStartY + gridViewH) {
            int startRow = (int) (this.scrollOffs * this.maxScrollOffset + 0.5D);
            int startIdx = startRow * COLUMNS;
            int endIdx = Math.min(startIdx + (VISIBLE_ROWS * COLUMNS), totalSlots);

            for (int i = startIdx; i < endIdx; i++) {
                int displayIdx = i - startIdx;
                int x = gridStartX + (displayIdx % COLUMNS) * SLOT_SIZE;
                int y = gridStartY + (displayIdx / COLUMNS) * SLOT_SIZE;

                if (vMx >= x && vMx < x + 18 && vMy >= y && vMy < y + 18) {
                    if (btn == 0) {
                        if (cursorStack != null && !cursorStack.isEmpty()) {
                            if (DummyUtils.isBlacklisted(cursorStack)) {
                                notifyBlacklist(); return true;
                            }
                            if (isValidCurio(cursorStack)) {
                                notifyNotCurio(); return true;
                            }
                            ItemStack toPlace = cursorStack.copy();
                            toPlace.setCount(1);
                            updateSlot(i, toPlace, DummyNetwork.UpdateCurioV2.fromCarried(
                                    menu.containerId, dummy.getId(), i));
                        } else {
                            openItemSelector(i);
                        }
                    } else if (btn == 1) {
                        updateSlot(i, ItemStack.EMPTY, DummyNetwork.UpdateCurioV2.clear(
                                menu.containerId, dummy.getId(), i));
                    }
                    return true;
                }
            }
        }

        if (this.minecraft != null && this.minecraft.player != null) {
            Inventory inv = this.minecraft.player.getInventory();
            for (int i = 0; i < 36; i++) {
                int col = (i < 9) ? i : (i - 9) % 9;
                int row = (i < 9) ? 3 : (i - 9) / 9;
                int px = playerInvX + 7 + col * 18;
                int py = playerInvY + (row == 3 ? 72 : 14 + row * 18);

                if (vMx >= px && vMx < px + 18 && vMy >= py && vMy < py + 18) {
                    ItemStack clickedStack = inv.getItem(i);
                    if (btn == 0 && Screen.hasShiftDown()) {
                        if (!clickedStack.isEmpty()) {
                            if (DummyUtils.isBlacklisted(clickedStack)) {
                                notifyBlacklist(); return true;
                            }
                            if (isValidCurio(clickedStack)) {
                                notifyNotCurio(); return true;
                            }
                            int emptyIdx = getFirstEmptyCurio();
                            if (emptyIdx != -1) {
                                ItemStack toPlace = clickedStack.copy();
                                toPlace.setCount(1);
                                updateSlot(emptyIdx, toPlace, DummyNetwork.UpdateCurioV2.fromInventory(
                                        menu.containerId, dummy.getId(), emptyIdx, i));
                            }
                        }
                        return true;
                    }
                    int containerSlotId = (i < 9) ? (33 + i) : (6 + (i - 9));
                    if (this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryMouseClick(this.minecraft.player.containerMenu.containerId, containerSlotId, btn, ClickType.PICKUP, this.minecraft.player);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        double vMy = toVirtualY(my);
        if (this.isScrolling) {
            int gridViewH = VISIBLE_ROWS * SLOT_SIZE;
            int thumbH = Math.max(15, (int) ((float) VISIBLE_ROWS / this.maxRows * gridViewH));
            this.scrollOffs = Mth.clamp((float) (vMy - (this.gridStartY + (thumbH / 2.0F))) / (gridViewH - thumbH), 0.0F, 1.0F);
            return true;
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        if (btn == 0) this.isScrolling = false;
        return super.mouseReleased(mx, my, btn);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        if (this.maxScrollOffset > 0) {
            this.scrollOffs = Mth.clamp(this.scrollOffs - (float) (delta * (1.0F / this.maxScrollOffset)), 0.0F, 1.0F);
            return true;
        }
        return super.mouseScrolled(mx, my, delta);
    }

    private int getFirstEmptyCurio() {
        for (int i = 0; i < totalSlots; i++) if (CuriosCompat.getCurioItem(dummy, i).isEmpty()) return i;
        return -1;
    }

    private void openItemSelector(int slotIdx) {
        if (this.minecraft != null) {
            this.minecraft.setScreen(new ItemSelectorScreen(this, selection -> {
                if (selection == null || !selection.isItem()) return;
                ItemStack newStack = selection.stack().copy();
                if (newStack.isEmpty()) return;
                newStack.setCount(1);
                if (DummyUtils.isBlacklisted(newStack)) {
                    notifyBlacklist();
                    return;
                }
                if (isValidCurio(newStack)) {
                    notifyNotCurio();
                    return;
                }
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(newStack.getItem());
                if (itemId == null) return;
                updateSlot(slotIdx, newStack, DummyNetwork.UpdateCurioV2.defaultItem(
                        menu.containerId, dummy.getId(), slotIdx, itemId));
            }));
        }
    }

    private void updateSlot(int index, ItemStack stack, DummyNetwork.UpdateCurioV2 packet) {
        CuriosCompat.setCurioItem(dummy, index, stack);
        DummyNetwork.sendToServer(packet);
    }
}
