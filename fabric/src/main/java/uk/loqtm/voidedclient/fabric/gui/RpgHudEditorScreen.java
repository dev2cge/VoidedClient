package uk.loqtm.voidedclient.fabric.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** In-game placement editor: every HUD item can occupy its own anchor or be hidden. */
public final class RpgHudEditorScreen extends Screen {
    public RpgHudEditorScreen() { super(Component.literal("Voided RPG HUD Layout")); }

    @Override
    protected void init() {
        int left = this.width / 2 - 150;
        int top = Math.max(24, this.height / 2 - 104);
        int row = 0;
        for (RpgHudConfig.Element element : RpgHudConfig.Element.values()) {
            final RpgHudConfig.Element selected = element;
            this.addRenderableWidget(Button.builder(label(selected), button -> {
                RpgHudConfig.cycle(selected);
                button.setMessage(label(selected));
            }).bounds(left + 112, top + 38 + row * 28, 188, 22).build());
            row++;
        }
        this.addRenderableWidget(Button.builder(Component.literal("Reset default"), button -> {
            RpgHudConfig.reset();
            this.minecraft.gui.setScreen(new RpgHudEditorScreen());
        }).bounds(left, top + 184, 104, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.minecraft.gui.setScreen(null))
                .bounds(left + 238, top + 184, 62, 22).build());
    }

    private static Component label(RpgHudConfig.Element element) { return Component.literal(RpgHudConfig.anchor(element).label()); }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int left = this.width / 2 - 150;
        int top = Math.max(24, this.height / 2 - 104);
        graphics.fill(left - 10, top - 10, left + 310, top + 218, 0xE8100C18);
        graphics.fill(left - 10, top - 10, left + 310, top - 7, 0xFF8B5CF6);
        graphics.centeredText(this.font, "VOIDED RPG HUD", this.width / 2, top + 2, 0xFFE9D5FF);
        graphics.centeredText(this.font, "Choose a separate screen position for each element", this.width / 2, top + 16, 0xFFAAA2B5);
        int row = 0;
        for (RpgHudConfig.Element element : RpgHudConfig.Element.values()) {
            graphics.text(this.font, title(element), left, top + 45 + row * 28, color(element), true);
            row++;
        }
        // Extract widgets last so their native high-contrast surfaces stay above the panel.
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    private static String title(RpgHudConfig.Element element) {
        return switch (element) {
            case HEALTH -> "Health"; case STRENGTH -> "Strength"; case DEFENCE -> "Defence";
            case MANA -> "Mana"; case SKILLS -> "Skill cooldowns";
        };
    }

    private static int color(RpgHudConfig.Element element) {
        return switch (element) {
            case HEALTH -> 0xFFF87171; case STRENGTH -> 0xFFFFB4B4; case DEFENCE -> 0xFF60A5FA;
            case MANA -> 0xFF67E8F9; case SKILLS -> 0xFFD8B4FE;
        };
    }
}
