package uk.loqtm.voidedclient.protocol;

/** Server-authoritative endgame/Ascension snapshot. */
public record EndgameCompanionState(
        boolean unlocked, int ascension, long marks,
        int trialProgress, int trialTarget, boolean trialClaimed, long lifetimeTrials,
        long totalClears, long standardClears, long eliteClears, long mythicClears,
        long bestAscension, String bestMode,
        boolean gauntletActive, String activeMode, int activeWave, int activeTotalWaves,
        long remainingSeconds, int completionPercent, boolean completionComplete) {

    public static EndgameCompanionState parse(String payload) {
        if (payload == null) return null;
        String[] p = payload.split("\\|", -1);
        if (p.length < 22 || !"VC1".equals(p[0]) || !"ENDGAME_COMPANION".equals(p[1])) return null;
        try {
            return new EndgameCompanionState(
                    Boolean.parseBoolean(p[2]), Integer.parseInt(p[3]), Long.parseLong(p[4]),
                    Integer.parseInt(p[5]), Integer.parseInt(p[6]), Boolean.parseBoolean(p[7]), Long.parseLong(p[8]),
                    Long.parseLong(p[9]), Long.parseLong(p[10]), Long.parseLong(p[11]), Long.parseLong(p[12]),
                    Long.parseLong(p[13]), p[14],
                    Boolean.parseBoolean(p[15]), p[16], Integer.parseInt(p[17]), Integer.parseInt(p[18]),
                    Long.parseLong(p[19]), Integer.parseInt(p[20]), Boolean.parseBoolean(p[21]));
        } catch (RuntimeException ignored) { return null; }
    }
}
