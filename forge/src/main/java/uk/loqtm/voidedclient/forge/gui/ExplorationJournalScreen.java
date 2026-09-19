package uk.loqtm.voidedclient.forge.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import uk.loqtm.voidedclient.protocol.ExplorationJournalState;

import java.util.Locale;

public final class ExplorationJournalScreen extends Screen {
    private static final int PAGE_SIZE = 6;
    private static final int PANEL_HEIGHT = 354;
    private final ExplorationJournalState state;
    private final Runnable openContract;
    private int page;
    private Button previous;
    private Button next;

    public ExplorationJournalScreen(ExplorationJournalState state) {
        this(state, null);
    }

    public ExplorationJournalScreen(ExplorationJournalState state, Runnable openContract) {
        super(Component.literal("Exploration Journal"));
        this.state = state;
        this.openContract = openContract;
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(520, width - 24);
        int left = (width - panelWidth) / 2;
        int top = Math.max(8, (height - PANEL_HEIGHT) / 2);
        previous = addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (page > 0) page--;
            syncButtons();
        }).bounds(left + 12, top + 321, 28, 22).build());
        next = addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if (page < pages() - 1) page++;
            syncButtons();
        }).bounds(left + 44, top + 321, 28, 22).build());
        if(openContract!=null)addRenderableWidget(Button.builder(Component.literal("View Contract"),button->openContract.run())
                .bounds(left+82,top+321,102,22).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), button -> minecraft.gui.setScreen(null))
                .bounds(left + panelWidth - 72, top + 321, 60, 22).build());
        syncButtons();
    }

    private void syncButtons() {
        if (previous != null) previous.active = page > 0;
        if (next != null) next.active = page < pages() - 1;
    }

    private int pages() { return Math.max(1, (state.mobs().size() + PAGE_SIZE - 1) / PAGE_SIZE); }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int panelWidth = Math.min(520, width - 24);
        int left = (width - panelWidth) / 2;
        int top = Math.max(8, (height - PANEL_HEIGHT) / 2);
        int right = left + panelWidth;
        int bottom = top + PANEL_HEIGHT;
        graphics.fill(left - 1, top - 1, right + 1, bottom + 1, 0xFF5B21B6);
        graphics.fill(left, top, right, bottom, 0xF5100D18);
        graphics.fill(left, top, right, top + 3, 0xFF8B5CF6);
        graphics.fill(left, top + 3, right, top + 48, 0xFF181222);
        graphics.centeredText(font, "EXPLORATION JOURNAL", width / 2, top + 11, 0xFFFFFFFF);
        graphics.centeredText(font, state.discoveredCount() + " / " + state.mobs().size()
                + " CREATURES DISCOVERED  •  " + state.totalKills() + " KILLS", width / 2, top + 28, 0xFFC4B5FD);

        drawContract(graphics, left + 12, right - 12, top + 57);
        int start = page * PAGE_SIZE;
        int gap = 8;
        int cardWidth = (panelWidth - 40) / 3;
        for (int slot = 0; slot < PAGE_SIZE; slot++) {
            int index = start + slot;
            if (index >= state.mobs().size()) break;
            int x = left + 12 + (slot % 3) * (cardWidth + gap);
            int y = top + 98 + (slot / 3) * 108;
            drawMobCard(graphics, state.mobs().get(index), x, y, cardWidth, 100);
        }
        graphics.text(font, "PAGE " + (page + 1) + " / " + pages(), left + 82, top + 328, 0xFFAAA2B5, false);
        graphics.centeredText(font, "Undiscovered creatures remain hidden until their first defeat.", width / 2, top + 307, 0xFF8F879B);
    }

    private void drawContract(GuiGraphicsExtractor graphics, int left, int right, int y) {
        graphics.fill(left, y, right, y + 31, 0xFF1A1523);
        graphics.fill(left, y, left + 4, y + 31, state.contractComplete() ? 0xFF4ADE80 : 0xFFA855F7);
        String name = state.contractName().isEmpty() ? "Daily Expedition" : state.contractName();
        graphics.text(font, name.toUpperCase(Locale.ROOT), left + 10, y + 5, 0xFFFFFFFF, true);
        String progress = state.contractComplete() ? "COMPLETE" : Math.min(state.contractProgress(), state.contractTarget()) + " / " + state.contractTarget();
        graphics.text(font, progress, right - 10 - font.width(progress), y + 5,
                state.contractComplete() ? 0xFF86EFAC : 0xFFE9D5FF, true);
        int barLeft = left + 10, barRight = right - 10, barTop = y + 19;
        graphics.fill(barLeft, barTop, barRight, barTop + 6, 0xFF31283E);
        double ratio = state.contractComplete() ? 1D : Math.min(1D, state.contractProgress() / (double)Math.max(1, state.contractTarget()));
        graphics.fill(barLeft + 1, barTop + 1, barLeft + 1 + (int)((barRight - barLeft - 2) * ratio), barTop + 5,
                state.contractComplete() ? 0xFF22C55E : 0xFF8B5CF6);
    }

    private void drawMobCard(GuiGraphicsExtractor graphics, ExplorationJournalState.MobEntry mob,
                             int x, int y, int cardWidth, int cardHeight) {
        boolean discovered = mob.discovered();
        graphics.fill(x, y, x + cardWidth, y + cardHeight, discovered ? accent(mob.id()) : 0xFF25222C);
        graphics.fill(x + 1, y + 1, x + cardWidth - 1, y + cardHeight - 1, discovered ? 0xFF17131F : 0xFF0B0A0E);
        graphics.fill(x + 5, y + 5, x + cardWidth - 5, y + 66, discovered ? 0xFF20192B : 0xFF09090C);
        drawMobIcon(graphics, mob.id(), x + cardWidth / 2, y + 35, discovered);
        graphics.centeredText(font, discovered ? mob.name().toUpperCase(Locale.ROOT) : "???",
                x + cardWidth / 2, y + 72, discovered ? 0xFFFFFFFF : 0xFF4B4652);
        String status = discovered ? mob.kills() + (mob.kills() == 1 ? " KILL" : " KILLS") : "UNDISCOVERED";
        graphics.centeredText(font, status, x + cardWidth / 2, y + 86, discovered ? 0xFFB9AFC8 : 0xFF302D35);
    }

    private void drawMobIcon(GuiGraphicsExtractor graphics, String id, int centerX, int centerY, boolean discovered) {
        int dark = discovered ? 0xFF19151F : 0xFF111115;
        int primary = discovered ? accent(id) : 0xFF17171C;
        int light = discovered ? lighten(primary) : 0xFF202027;
        if (id.contains("crawler")) {
            graphics.fill(centerX - 18, centerY - 7, centerX + 18, centerY + 7, primary);
            graphics.fill(centerX - 11, centerY - 13, centerX + 11, centerY + 12, light);
            for (int side = -1; side <= 1; side += 2) {
                graphics.fill(centerX + side * 14, centerY - 12, centerX + side * 22, centerY - 9, dark);
                graphics.fill(centerX + side * 14, centerY + 9, centerX + side * 22, centerY + 12, dark);
            }
        } else {
            graphics.fill(centerX - 9, centerY - 17, centerX + 9, centerY + 1, light);
            graphics.fill(centerX - 12, centerY + 2, centerX + 12, centerY + 22, primary);
            graphics.fill(centerX - 17, centerY + 5, centerX - 12, centerY + 18, dark);
            graphics.fill(centerX + 12, centerY + 5, centerX + 17, centerY + 18, dark);
            if (id.contains("miner")) {
                graphics.fill(centerX + 15, centerY - 12, centerX + 18, centerY + 21, 0xFF62546C);
                graphics.fill(centerX + 8, centerY - 15, centerX + 24, centerY - 11, discovered ? 0xFFA78BFA : dark);
            } else {
                graphics.fill(centerX + 15, centerY - 13, centerX + 18, centerY + 20, discovered ? 0xFFC4B5FD : dark);
                graphics.fill(centerX + 18, centerY - 13, centerX + 23, centerY - 9, discovered ? 0xFF60A5FA : dark);
                graphics.fill(centerX + 18, centerY + 16, centerX + 23, centerY + 20, discovered ? 0xFF60A5FA : dark);
            }
        }
    }

    private static int accent(String id) {
        if (id.contains("miner")) return 0xFF8B5CF6;
        if (id.contains("crawler")) return 0xFF22D3EE;
        if (id.contains("archer")) return 0xFF60A5FA;
        return 0xFFA855F7;
    }

    private static int lighten(int color) {
        int red = Math.min(255, ((color >> 16) & 255) + 38);
        int green = Math.min(255, ((color >> 8) & 255) + 38);
        int blue = Math.min(255, (color & 255) + 38);
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }
}
