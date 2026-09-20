package uk.loqtm.voidedclient.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import uk.loqtm.voidedclient.protocol.VoidedProtocol;
import uk.loqtm.voidedclient.protocol.RpgStatsState;
import uk.loqtm.voidedclient.protocol.RpgHudState;
import uk.loqtm.voidedclient.protocol.ExplorationJournalState;
import uk.loqtm.voidedclient.protocol.ExplorationContractState;
import uk.loqtm.voidedclient.fabric.gui.RpgStatsScreen;
import uk.loqtm.voidedclient.fabric.gui.RpgHudRenderer;
import uk.loqtm.voidedclient.fabric.gui.RpgHudEditorScreen;
import uk.loqtm.voidedclient.fabric.gui.ExplorationJournalScreen;
import uk.loqtm.voidedclient.fabric.gui.ExplorationContractScreen;
import uk.loqtm.voidedclient.fabric.gui.CompanionScreen;
import uk.loqtm.voidedclient.protocol.CompanionHomeState;
import uk.loqtm.voidedclient.protocol.LeaderboardState;
import uk.loqtm.voidedclient.protocol.LinkedAccountsState;
import uk.loqtm.voidedclient.protocol.ServerListState;
import uk.loqtm.voidedclient.protocol.NetherCompanionState;

import java.nio.charset.StandardCharsets;

/** Minecraft 26.2 Fabric/Lunar-safe adapter. No mixins or Lunar-private hooks. */
public final class VoidedClientFabric implements ClientModInitializer {
    private static final Identifier CHANNEL = Identifier.fromNamespaceAndPath("voidedcore", "client");
    private static final CustomPacketPayload.Type<RawPayload> TYPE = new CustomPacketPayload.Type<>(CHANNEL);
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("voidedclient", "controls"));
    private static final long REPAIR_REPEAT_MS = 350L;

    private KeyMapping repair;
    private KeyMapping rpgStats;
    private KeyMapping rpgHudLayout;
    private KeyMapping explorationJournal;
    private KeyMapping companion;
    private KeyMapping powerStrike, bulwark, secondWind, dash, arcaneSurge;
    private long lastRepairSent;
    private long lastStatsSent;
    private int helloDelay = -1;

    @Override public void onInitializeClient() {
        PayloadTypeRegistry.serverboundPlay().register(TYPE, RawPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TYPE, RawPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            String message = new String(payload.bytes(), StandardCharsets.UTF_8);
            if (message.startsWith("VC1|PROBE|")) { context.client().execute(VoidedClientFabric::sendHello); return; }
            if (message.startsWith("VC1|RPG_STATS|") || message.startsWith("VC1|RPG_STATS_V2|") || message.startsWith("VC1|RPG_STATS_V3|")) {
                RpgStatsState state = RpgStatsState.parse(message);
                if (state != null) context.client().execute(() -> context.client().gui.setScreen(new RpgStatsScreen(state,
                        (stat, amount) -> send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_STAT_SPEND, stat, amount)),
                        () -> send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_STAT_RESPEC)))));
            }
            if (message.startsWith("VC1|RPG_HUD|")) { RpgHudState state = RpgHudState.parse(message); if (state != null) context.client().execute(() -> RpgHudRenderer.update(state)); }
            if (message.startsWith("VC1|RPG_FX|")) { String skill = message.substring("VC1|RPG_FX|".length()); context.client().execute(() -> RpgHudRenderer.flash(skill)); }
            if (message.startsWith("VC1|EXPLORATION_JOURNAL|")) {
                ExplorationJournalState state = ExplorationJournalState.parse(message);
                if (state != null) context.client().execute(() -> context.client().gui.setScreen(new ExplorationJournalScreen(state,
                        () -> send(VoidedProtocol.action(VoidedProtocol.ACTION_EXPLORATION_CONTRACT)))));
            }
            if (message.startsWith("VC1|EXPLORATION_CONTRACT|")) {
                ExplorationContractState state = ExplorationContractState.parse(message);
                if (state != null) context.client().execute(() -> context.client().gui.setScreen(new ExplorationContractScreen(state,
                        () -> send(VoidedProtocol.action(VoidedProtocol.ACTION_EXPLORATION_JOURNAL)))));
            }
            if (message.startsWith("VC1|COMPANION_HOME|")) { CompanionHomeState state=CompanionHomeState.parse(message); if(state!=null)context.client().execute(()->context.client().gui.setScreen(CompanionScreen.home(state,VoidedClientFabric::send))); }
            if (message.startsWith("VC1|LEADERBOARD|")) { LeaderboardState state=LeaderboardState.parse(message); if(state!=null)context.client().execute(()->context.client().gui.setScreen(CompanionScreen.leaderboard(state,VoidedClientFabric::send))); }
            if (message.startsWith("VC1|LINKED_ACCOUNTS|")) { LinkedAccountsState state=LinkedAccountsState.parse(message); if(state!=null)context.client().execute(()->context.client().gui.setScreen(CompanionScreen.accounts(state,VoidedClientFabric::send))); }
            if (message.startsWith("VC1|SERVER_LIST|")) { ServerListState state=ServerListState.parse(message); if(state!=null)context.client().execute(()->context.client().gui.setScreen(CompanionScreen.servers(state,VoidedClientFabric::send))); }
            if (message.startsWith("VC1|NETHER_COMPANION|")) { NetherCompanionState state=NetherCompanionState.parse(message); if(state!=null)context.client().execute(()->context.client().gui.setScreen(CompanionScreen.nether(state,VoidedClientFabric::send))); }
            if (message.startsWith("VC1|END_COMPANION|")) { EndCompanionState state=EndCompanionState.parse(message); if(state!=null)context.client().execute(()->context.client().gui.setScreen(CompanionScreen.end(state,VoidedClientFabric::send))); }
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath("voidedclient", "rpg_hud"), RpgHudRenderer::render);
        repair = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.voidedclient.repair", InputConstants.Type.KEYSYM, InputConstants.KEY_R, CATEGORY));
        rpgStats = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.voidedclient.rpg_stats", InputConstants.Type.KEYSYM, InputConstants.KEY_J, CATEGORY));
        rpgHudLayout = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.voidedclient.rpg_hud_layout", InputConstants.Type.KEYSYM, InputConstants.KEY_H, CATEGORY));
        explorationJournal = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.voidedclient.exploration_journal", InputConstants.Type.KEYSYM, InputConstants.KEY_K, CATEGORY));
        companion = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.voidedclient.companion", InputConstants.Type.KEYSYM, InputConstants.KEY_U, CATEGORY));
        powerStrike = skillKey("key.voidedclient.skill.power_strike", InputConstants.KEY_Z);
        bulwark = skillKey("key.voidedclient.skill.bulwark", InputConstants.KEY_X);
        secondWind = skillKey("key.voidedclient.skill.second_wind", InputConstants.KEY_C);
        dash = skillKey("key.voidedclient.skill.dash", InputConstants.KEY_V);
        arcaneSurge = skillKey("key.voidedclient.skill.arcane_surge", InputConstants.KEY_B);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> { helloDelay = 20; lastRepairSent = 0L; lastStatsSent = 0L; });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> { helloDelay = -1; RpgHudRenderer.clear(); });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (helloDelay >= 0 && --helloDelay == 0) { sendHello(); helloDelay = -1; }
            if (client.player == null || client.gui.screen() != null) return;
            long now = System.currentTimeMillis();
            if (repair.isDown() && now - lastRepairSent >= REPAIR_REPEAT_MS) { lastRepairSent = now; send(VoidedProtocol.action(VoidedProtocol.ACTION_REPAIR)); }
            if (rpgStats.isDown() && now - lastStatsSent >= 500L) { lastStatsSent = now; send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_STATS)); }
            while (rpgHudLayout.consumeClick()) client.gui.setScreen(new RpgHudEditorScreen());
            while (explorationJournal.consumeClick()) send(VoidedProtocol.action(VoidedProtocol.ACTION_EXPLORATION_JOURNAL));
            while (companion.consumeClick()) send(VoidedProtocol.action(VoidedProtocol.ACTION_COMPANION_HOME));
            castIfPressed(powerStrike, "power-strike"); castIfPressed(bulwark, "bulwark"); castIfPressed(secondWind, "second-wind"); castIfPressed(dash, "dash"); castIfPressed(arcaneSurge, "arcane-surge");
        });
    }

    private static KeyMapping skillKey(String translation, int key) { return KeyMappingHelper.registerKeyMapping(new KeyMapping(translation, InputConstants.Type.KEYSYM, key, CATEGORY)); }
    private static void castIfPressed(KeyMapping mapping, String skill) {
        while (mapping.consumeClick()) {
            String dashInput = "";
            if ("dash".equals(skill)) {
                try { var player = net.minecraft.client.Minecraft.getInstance().player; if (player != null) { var movement = player.input.getMoveVector(); dashInput = String.format(java.util.Locale.ROOT, "dash:%.3f,%.3f,%.3f,%.3f", player.getYRot(), player.getXRot(), movement.x, movement.y); } } catch (Throwable ignored) {}
            }
            send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_SKILL_CAST, skill, dashInput));
        }
    }
    private static void sendHello() {
        String gameVersion; try { gameVersion = SharedConstants.getCurrentVersion().id(); } catch (Throwable ignored) { gameVersion = "26.2"; }
        send(VoidedProtocol.hello("fabric", "1.11.0", gameVersion,
                VoidedProtocol.CAP_RPG_UI + "," + VoidedProtocol.CAP_RPG_STAT_SPEND + "," + VoidedProtocol.CAP_RPG_STAT_RESPEC + "," + VoidedProtocol.CAP_RPG_STATS_V2 + "," + VoidedProtocol.CAP_RPG_STATS_V3 + "," + VoidedProtocol.CAP_RPG_HUD + "," + VoidedProtocol.CAP_RPG_SKILLS + "," + VoidedProtocol.CAP_RPG_SKILL_FX + "," + VoidedProtocol.CAP_EXPLORATION_JOURNAL + "," + VoidedProtocol.CAP_EXPLORATION_CONTRACT + "," + VoidedProtocol.CAP_COMPANION_HOME + "," + VoidedProtocol.CAP_LEADERBOARDS + "," + VoidedProtocol.CAP_LINKED_ACCOUNTS + "," + VoidedProtocol.CAP_SERVER_NAVIGATION + "," + VoidedProtocol.CAP_NETHER_COMPANION + "," + VoidedProtocol.CAP_END_COMPANION));
    }
    private static void send(byte[] bytes) { try { ClientPlayNetworking.send(new RawPayload(bytes)); } catch (Throwable ignored) {} }

    public record RawPayload(byte[] bytes) implements CustomPacketPayload {
        public static final StreamCodec<RegistryFriendlyByteBuf, RawPayload> CODEC = StreamCodec.of(
                (buf, payload) -> { if (payload.bytes.length > 4096) throw new IllegalArgumentException("VoidedClient payload too large"); buf.writeBytes(payload.bytes); },
                buf -> { int length = Math.min(buf.readableBytes(), 4096); byte[] data = new byte[length]; buf.readBytes(data); return new RawPayload(data); });
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
}
