package uk.loqtm.voidedclient.protocol;

import java.nio.charset.StandardCharsets;

public final class VoidedProtocol {
    public static final String CHANNEL = "voidedcore:client";
    public static final String VERSION = "VC1";
    public static final String ACTION_REPAIR = "mending.repair";
    public static final String ACTION_RPG_STATS = "rpg.stats";
    public static final String ACTION_RPG_STAT_SPEND = "rpg.stat.spend";
    public static final String ACTION_RPG_STAT_RESPEC = "rpg.stat.respec";
    public static final String ACTION_RPG_SKILL_CAST = "rpg.skill.cast";
    public static final String ACTION_EXPLORATION_JOURNAL = "exploration.journal";
    public static final String ACTION_EXPLORATION_CONTRACT = "exploration.contract";
    public static final String ACTION_COMPANION_HOME = "companion.home";
    public static final String ACTION_LEADERBOARD_REQUEST = "leaderboard.request";
    public static final String ACTION_ACCOUNTS_REQUEST = "accounts.request";
    public static final String ACTION_ACCOUNTS_LINK = "accounts.link";
    public static final String ACTION_ACCOUNTS_UNLINK = "accounts.unlink";
    public static final String ACTION_SERVER_LIST = "server.list";
    public static final String ACTION_SERVER_SWITCH = "server.switch";
    public static final String ACTION_NETHER_COMPANION = "nether.companion";
    public static final String ACTION_END_COMPANION = "end.companion";
    public static final String CAP_RPG_UI = "rpg-ui";
    public static final String CAP_RPG_STAT_SPEND = "rpg-stat-spend";
    public static final String CAP_RPG_STAT_RESPEC = "rpg-stat-respec";
    public static final String CAP_RPG_STATS_V2 = "rpg-stats-v2";
    public static final String CAP_RPG_STATS_V3 = "rpg-stats-v3";
    public static final String CAP_RPG_HUD = "rpg-hud";
    public static final String CAP_RPG_SKILLS = "rpg-skills";
    public static final String CAP_RPG_SKILL_FX = "rpg-skill-fx";
    public static final String CAP_EXPLORATION_JOURNAL = "exploration-journal";
    public static final String CAP_EXPLORATION_CONTRACT = "exploration-contract";
    public static final String CAP_COMPANION_HOME = "companion-home-v1";
    public static final String CAP_LEADERBOARDS = "leaderboards-v1";
    public static final String CAP_LINKED_ACCOUNTS = "linked-accounts-v1";
    public static final String CAP_SERVER_NAVIGATION = "server-navigation-v1";
    public static final String CAP_NETHER_COMPANION = "nether-companion-v1";
    public static final String CAP_END_COMPANION = "end-companion-v1";
    private VoidedProtocol() {}

    public static byte[] hello(String loader, String modVersion, String minecraftVersion) { return hello(loader, modVersion, minecraftVersion, ""); }
    public static byte[] hello(String loader, String modVersion, String minecraftVersion, String capabilities) {
        return (VERSION + "|HELLO|" + safe(loader) + "|" + safe(modVersion) + "|" + safe(minecraftVersion) + "|" + safeLong(capabilities, 384)).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] action(String action) { return (VERSION + "|ACTION|" + safe(action)).getBytes(StandardCharsets.UTF_8); }
    public static byte[] action(String action, String arg1) { return (VERSION + "|ACTION|" + safe(action) + "|" + safe(arg1)).getBytes(StandardCharsets.UTF_8); }
    public static byte[] action(String action, String arg1, String arg2) { return (VERSION + "|ACTION|" + safe(action) + "|" + safe(arg1) + "|" + safe(arg2)).getBytes(StandardCharsets.UTF_8); }
    public static String safe(String s) { return safeLong(s, 64); }
    private static String safeLong(String s, int max) {
        if (s == null) return "";
        String v = s.replace('|','_').replace('\n','_').replace('\r','_').trim();
        return v.length() > max ? v.substring(0, max) : v;
    }
}
