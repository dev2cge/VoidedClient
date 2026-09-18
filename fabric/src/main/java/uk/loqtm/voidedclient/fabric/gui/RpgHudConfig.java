package uk.loqtm.voidedclient.fabric.gui;

import net.fabricmc.loader.api.FabricLoader;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Properties;

/** Persistent per-element HUD placement. Nothing here affects server-authoritative values. */
public final class RpgHudConfig {
    public enum Element { HEALTH, STRENGTH, DEFENCE, MANA, SKILLS }
    public enum Anchor {
        HOTBAR_LEFT_TOP("Hotbar left (top)"), HOTBAR_LEFT_BOTTOM("Hotbar left (bottom)"),
        HOTBAR_RIGHT_TOP("Hotbar right (top)"), HOTBAR_RIGHT_BOTTOM("Hotbar right (bottom)"),
        HOTBAR_ABOVE("Above hotbar"), TOP_LEFT("Top left"), TOP_RIGHT("Top right"),
        BOTTOM_LEFT("Bottom left"), BOTTOM_RIGHT("Bottom right"), HIDDEN("Hidden");
        private final String label;
        Anchor(String label) { this.label = label; }
        public String label() { return label; }
        public Anchor next() { Anchor[] all = values(); return all[(ordinal() + 1) % all.length]; }
    }

    private static final EnumMap<Element, Anchor> anchors = new EnumMap<>(Element.class);
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("voidedclient-rpg-hud.properties");
    static { resetDefaults(); load(); }
    private RpgHudConfig() {}

    public static Anchor anchor(Element element) { return anchors.get(element); }
    public static void cycle(Element element) { anchors.put(element, anchor(element).next()); save(); }
    public static void reset() { resetDefaults(); save(); }

    private static void resetDefaults() {
        anchors.put(Element.HEALTH, Anchor.HOTBAR_LEFT_TOP);
        anchors.put(Element.STRENGTH, Anchor.HOTBAR_LEFT_BOTTOM);
        anchors.put(Element.DEFENCE, Anchor.HOTBAR_RIGHT_TOP);
        anchors.put(Element.MANA, Anchor.HOTBAR_RIGHT_BOTTOM);
        anchors.put(Element.SKILLS, Anchor.HOTBAR_ABOVE);
    }

    private static void load() {
        if (!Files.isRegularFile(FILE)) return;
        Properties values = new Properties();
        try (InputStream input = Files.newInputStream(FILE)) {
            values.load(input);
            for (Element element : Element.values()) {
                String raw = values.getProperty(element.name().toLowerCase(Locale.ROOT));
                if (raw != null) try { anchors.put(element, Anchor.valueOf(raw)); } catch (IllegalArgumentException ignored) {}
            }
        } catch (Exception ignored) {}
    }

    private static void save() {
        Properties values = new Properties();
        for (Element element : Element.values()) values.setProperty(element.name().toLowerCase(Locale.ROOT), anchor(element).name());
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream output = Files.newOutputStream(FILE)) { values.store(output, "VoidedClient RPG HUD layout"); }
        } catch (Exception ignored) {}
    }
}
