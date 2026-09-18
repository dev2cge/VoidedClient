package uk.loqtm.voidedclient.protocol;

/** Immutable server-authoritative RPG state sent to VoidedClient. */
public record RpgStatsState(
        int level, long xpIntoLevel, long xpNeeded, int points,
        int strength, int defence, int vitality, int agility, int intelligence,
        double damageBonus, double knockbackBonus, double defenceReductionPercent,
        double healthBonus, double healthRegenPerSecond,
        double movementSpeedBonus, double attackSpeedBonus, double cooldownReduction,
        double manaCapacity, double ritualPower, double skillPower,
        int totalPoints, int spentPoints, int maxPointsPerStat, int softCap,
        long respecCooldownSeconds, String buildName, double buildScore,
        double currentMana, double manaRegenPerSecond,
        int equipmentStrength, int equipmentDefence, int equipmentVitality, int equipmentAgility, int equipmentIntelligence,
        int temporaryStrength, int temporaryDefence, int temporaryVitality, int temporaryAgility, int temporaryIntelligence,
        int equippedPieces, int activeEffects
) {
    public static RpgStatsState parse(String message) {
        if (message == null) return null;
        if (message.startsWith("VC1|RPG_STATS_V3|")) return parseV3(message);
        if (message.startsWith("VC1|RPG_STATS_V2|")) return parseV2(message);
        if (message.startsWith("VC1|RPG_STATS|")) return parseLegacy(message);
        return null;
    }

    private static RpgStatsState parseV3(String message) {
        String[] p = message.split("\\|", -1);
        if (p.length < 43) return null;
        try {
            return new RpgStatsState(
                    Integer.parseInt(p[2]), Long.parseLong(p[3]), Long.parseLong(p[4]), Integer.parseInt(p[5]),
                    Integer.parseInt(p[6]), Integer.parseInt(p[7]), Integer.parseInt(p[8]), Integer.parseInt(p[9]), Integer.parseInt(p[10]),
                    Double.parseDouble(p[11]), Double.parseDouble(p[12]), Double.parseDouble(p[13]),
                    Double.parseDouble(p[14]), Double.parseDouble(p[15]), Double.parseDouble(p[16]), Double.parseDouble(p[17]),
                    Double.parseDouble(p[18]), Double.parseDouble(p[19]), Double.parseDouble(p[20]), Double.parseDouble(p[21]),
                    Integer.parseInt(p[22]), Integer.parseInt(p[23]), Integer.parseInt(p[24]), Integer.parseInt(p[25]),
                    Long.parseLong(p[26]), p[27].isBlank() ? "Balanced" : p[27], Double.parseDouble(p[28]),
                    Double.parseDouble(p[29]), Double.parseDouble(p[30]),
                    Integer.parseInt(p[31]), Integer.parseInt(p[32]), Integer.parseInt(p[33]), Integer.parseInt(p[34]), Integer.parseInt(p[35]),
                    Integer.parseInt(p[36]), Integer.parseInt(p[37]), Integer.parseInt(p[38]), Integer.parseInt(p[39]), Integer.parseInt(p[40]),
                    Integer.parseInt(p[41]), Integer.parseInt(p[42]));
        } catch (RuntimeException ignored) { return null; }
    }

    private static RpgStatsState parseV2(String message) {
        String[] p = message.split("\\|", -1);
        if (p.length < 29) return null;
        try {
            return new RpgStatsState(
                    Integer.parseInt(p[2]), Long.parseLong(p[3]), Long.parseLong(p[4]), Integer.parseInt(p[5]),
                    Integer.parseInt(p[6]), Integer.parseInt(p[7]), Integer.parseInt(p[8]), Integer.parseInt(p[9]), Integer.parseInt(p[10]),
                    Double.parseDouble(p[11]), Double.parseDouble(p[12]), Double.parseDouble(p[13]),
                    Double.parseDouble(p[14]), Double.parseDouble(p[15]), Double.parseDouble(p[16]), Double.parseDouble(p[17]),
                    Double.parseDouble(p[18]), Double.parseDouble(p[19]), Double.parseDouble(p[20]), Double.parseDouble(p[21]),
                    Integer.parseInt(p[22]), Integer.parseInt(p[23]), Integer.parseInt(p[24]), Integer.parseInt(p[25]),
                    Long.parseLong(p[26]), p[27].isBlank() ? "Balanced" : p[27], Double.parseDouble(p[28]),
                    Double.parseDouble(p[19]), 0D, 0,0,0,0,0, 0,0,0,0,0, 0,0);
        } catch (RuntimeException ignored) { return null; }
    }

    /** Compatibility for VoidedClient 1.3 / Core 6.36 packets. */
    private static RpgStatsState parseLegacy(String message) {
        String[] p = message.split("\\|", -1);
        if (p.length < 17) return null;
        try {
            int level = Integer.parseInt(p[2]); long xpInto = Long.parseLong(p[3]); long xpNeeded = Long.parseLong(p[4]); int points = Integer.parseInt(p[5]);
            int strength = Integer.parseInt(p[6]), defence = Integer.parseInt(p[7]), vitality = Integer.parseInt(p[8]);
            int precision = Integer.parseInt(p[9]), haste = Integer.parseInt(p[10]);
            double damage = Double.parseDouble(p[11]), defenceBonus = Double.parseDouble(p[12]), health = Double.parseDouble(p[13]), attackSpeed = Double.parseDouble(p[16]);
            int total = p.length > 17 ? Integer.parseInt(p[17]) : points + strength + defence + vitality + precision + haste;
            int spent = p.length > 18 ? Integer.parseInt(p[18]) : strength + defence + vitality + precision + haste;
            int max = p.length > 19 ? Integer.parseInt(p[19]) : 100, soft = p.length > 20 ? Integer.parseInt(p[20]) : Math.min(60, max);
            long respec = p.length > 21 ? Long.parseLong(p[21]) : 0L; String build = p.length > 22 && !p[22].isBlank() ? p[22] : "Balanced";
            double score = p.length > 23 ? Double.parseDouble(p[23]) : 0D;
            return new RpgStatsState(level, xpInto, xpNeeded, points, strength, defence, vitality, precision, haste,
                    damage, 0D, defenceBonus, health, 0D, 0D, attackSpeed, 0D, 100D, 0D, 0D, total, spent, max, soft, respec, build, score,
                    100D,0D,0,0,0,0,0,0,0,0,0,0,0,0);
        } catch (RuntimeException ignored) { return null; }
    }

    public double progress() { return xpNeeded <= 0L ? 1D : Math.max(0D, Math.min(1D, (double) xpIntoLevel / (double) xpNeeded)); }
    public int stat(String stat) {
        return switch (stat) {
            case "strength" -> strength; case "defence" -> defence; case "vitality" -> vitality;
            case "agility" -> agility; case "intelligence" -> intelligence; default -> 0;
        };
    }
    public int equipment(String stat) { return switch(stat){case "strength"->equipmentStrength;case "defence"->equipmentDefence;case "vitality"->equipmentVitality;case "agility"->equipmentAgility;case "intelligence"->equipmentIntelligence;default->0;}; }
    public int temporary(String stat) { return switch(stat){case "strength"->temporaryStrength;case "defence"->temporaryDefence;case "vitality"->temporaryVitality;case "agility"->temporaryAgility;case "intelligence"->temporaryIntelligence;default->0;}; }
    public int totalStat(String stat){return Math.max(0,stat(stat)+equipment(stat)+temporary(stat));}
}
