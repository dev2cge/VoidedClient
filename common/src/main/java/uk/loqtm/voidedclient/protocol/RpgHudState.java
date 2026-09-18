package uk.loqtm.voidedclient.protocol;

/** Compact live state used by the optional in-game RPG HUD. */
public record RpgHudState(
        double health, double maxHealth, int strength, int defence,
        double mana, double maxMana, double defenceReductionPercent,
        long powerStrikeCooldown, long bulwarkCooldown, long secondWindCooldown,
        long dashCooldown, long arcaneSurgeCooldown
) {
    public static RpgHudState parse(String message) {
        if (message == null || !message.startsWith("VC1|RPG_HUD|")) return null;
        String[] p = message.split("\\|", -1);
        if (p.length < 14) return null;
        try {
            return new RpgHudState(Double.parseDouble(p[2]), Double.parseDouble(p[3]),
                    Integer.parseInt(p[4]), Integer.parseInt(p[5]),
                    Double.parseDouble(p[6]), Double.parseDouble(p[7]), Double.parseDouble(p[8]),
                    Long.parseLong(p[9]), Long.parseLong(p[10]), Long.parseLong(p[11]),
                    Long.parseLong(p[12]), Long.parseLong(p[13]));
        } catch (RuntimeException ignored) { return null; }
    }
}
