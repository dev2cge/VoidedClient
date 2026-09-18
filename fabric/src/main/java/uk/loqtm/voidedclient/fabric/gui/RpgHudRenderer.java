package uk.loqtm.voidedclient.fabric.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import uk.loqtm.voidedclient.protocol.RpgHudState;
import java.util.Locale;

/** Separate, low-profile HUD elements with per-element persisted placement. */
public final class RpgHudRenderer {
    private static volatile RpgHudState state;
    private static volatile String flashSkill;
    private static volatile long flashUntil;

    private RpgHudRenderer() {}
    public static void update(RpgHudState next) { state = next; }
    public static void clear() { state = null; flashSkill = null; }
    public static void flash(String skill) { flashSkill = displaySkill(skill); flashUntil = System.currentTimeMillis() + 1100L; }

    public static void render(GuiGraphicsExtractor graphics, net.minecraft.client.DeltaTracker tickCounter) {
        RpgHudState value = state;
        Minecraft minecraft = Minecraft.getInstance();
        if (value == null || minecraft.player == null) return;
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        draw(graphics, minecraft, RpgHudConfig.Element.HEALTH, "❤ " + fmt(value.health()) + "/" + fmt(value.maxHealth()), value.maxHealth() <= 0D ? 0D : value.health() / value.maxHealth(), 0xFFF87171, width, height);
        draw(graphics, minecraft, RpgHudConfig.Element.STRENGTH, "⚔ " + value.strength() + " STR", -1D, 0xFFFF9B9B, width, height);
        draw(graphics, minecraft, RpgHudConfig.Element.DEFENCE, "◆ " + value.defence() + " DEF  " + fmt(value.defenceReductionPercent()) + "%", -1D, 0xFF60A5FA, width, height);
        draw(graphics, minecraft, RpgHudConfig.Element.MANA, "✦ " + fmt(value.mana()) + "/" + fmt(value.maxMana()), value.maxMana() <= 0D ? 0D : value.mana() / value.maxMana(), 0xFF67E8F9, width, height);
        String skills = "PWR " + cd(value.powerStrikeCooldown()) + "  BLW " + cd(value.bulwarkCooldown()) + "  WND " + cd(value.secondWindCooldown())
                + "  DSH " + cd(value.dashCooldown()) + "  ARC " + cd(value.arcaneSurgeCooldown());
        draw(graphics, minecraft, RpgHudConfig.Element.SKILLS, skills, -1D, 0xFFD8B4FE, width, height);

        if (flashSkill != null && System.currentTimeMillis() < flashUntil) {
            int alpha = (int)Math.max(55D, Math.min(255D, (flashUntil - System.currentTimeMillis()) / 1100D * 255D));
            int color = (alpha << 24) | 0x00E9D5FF;
            graphics.centeredText(minecraft.font, "✦ " + flashSkill + " ✦", width / 2, height / 2 + 22, color);
            graphics.fill(width / 2 - 48, height / 2 + 34, width / 2 + 48, height / 2 + 36, color);
        }
    }

    private static void draw(GuiGraphicsExtractor graphics, Minecraft minecraft, RpgHudConfig.Element element,
                             String text, double progress, int color, int screenWidth, int screenHeight) {
        RpgHudConfig.Anchor anchor = RpgHudConfig.anchor(element);
        if (anchor == RpgHudConfig.Anchor.HIDDEN) return;
        int textWidth = minecraft.font.width(text);
        int x, y;
        switch (anchor) {
            case HOTBAR_LEFT_TOP -> { x = screenWidth / 2 - 101 - textWidth; y = screenHeight - 48; }
            case HOTBAR_LEFT_BOTTOM -> { x = screenWidth / 2 - 101 - textWidth; y = screenHeight - 34; }
            case HOTBAR_RIGHT_TOP -> { x = screenWidth / 2 + 101; y = screenHeight - 48; }
            case HOTBAR_RIGHT_BOTTOM -> { x = screenWidth / 2 + 101; y = screenHeight - 34; }
            case HOTBAR_ABOVE -> { x = (screenWidth - textWidth) / 2; y = screenHeight - 63; }
            case TOP_LEFT -> { x = 8; y = 8 + element.ordinal() * 14; }
            case TOP_RIGHT -> { x = screenWidth - textWidth - 8; y = 8 + element.ordinal() * 14; }
            case BOTTOM_LEFT -> { x = 8; y = screenHeight - 18 - element.ordinal() * 14; }
            case BOTTOM_RIGHT -> { x = screenWidth - textWidth - 8; y = screenHeight - 18 - element.ordinal() * 14; }
            default -> { return; }
        }
        graphics.text(minecraft.font, text, x, y, color, true);
        if (progress >= 0D) {
            int barWidth = Math.max(36, textWidth);
            graphics.fill(x, y + 10, x + barWidth, y + 12, 0xB0201A29);
            graphics.fill(x, y + 10, x + (int)(barWidth * clamp(progress)), y + 12, color);
        }
    }

    private static double clamp(double value) { return Math.max(0D, Math.min(1D, value)); }
    private static String fmt(double value) { return Math.abs(value - Math.rint(value)) < 0.0001D ? Integer.toString((int)Math.rint(value)) : String.format(Locale.ROOT, "%.1f", value); }
    private static String cd(long seconds) { return seconds <= 0L ? "✓" : seconds + "s"; }
    private static String displaySkill(String skill) {
        if (skill == null) return "RPG Skill";
        String[] parts = skill.replace('_', '-').split("-");
        StringBuilder out = new StringBuilder();
        for (String part : parts) if (!part.isEmpty()) out.append(out.length() == 0 ? "" : " ").append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        return out.length() == 0 ? "RPG Skill" : out.toString();
    }
}
