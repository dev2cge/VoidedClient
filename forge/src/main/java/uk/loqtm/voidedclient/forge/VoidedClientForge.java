package uk.loqtm.voidedclient.forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.SharedConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import uk.loqtm.voidedclient.protocol.VoidedProtocol;
import uk.loqtm.voidedclient.protocol.RpgStatsState;
import uk.loqtm.voidedclient.protocol.ExplorationJournalState;
import uk.loqtm.voidedclient.protocol.ExplorationContractState;
import uk.loqtm.voidedclient.forge.gui.CompanionScreen;
import uk.loqtm.voidedclient.protocol.CompanionHomeState;
import uk.loqtm.voidedclient.protocol.LeaderboardState;
import uk.loqtm.voidedclient.protocol.LinkedAccountsState;
import uk.loqtm.voidedclient.protocol.ServerListState;
import uk.loqtm.voidedclient.protocol.NetherCompanionState;
import uk.loqtm.voidedclient.protocol.EndCompanionState;
import uk.loqtm.voidedclient.protocol.EndgameCompanionState;
import uk.loqtm.voidedclient.protocol.ServerContextState;

import java.nio.charset.StandardCharsets;

/** Forge adapter using a raw custom payload so the Paper server needs no Forge networking stack. */
@Mod("voidedclient")
public final class VoidedClientForge {
    private static final Identifier CHANNEL_ID = Identifier.fromNamespaceAndPath("voidedcore", "client");
    private static final CustomPacketPayload.Type<RawPayload> PAYLOAD_TYPE = new CustomPacketPayload.Type<>(CHANNEL_ID);
    private static final StreamCodec<RegistryFriendlyByteBuf, RawPayload> PAYLOAD_CODEC = StreamCodec.of(
            (buf, payload) -> { if (payload.bytes.length > 4096) throw new IllegalArgumentException("VoidedClient payload too large"); buf.writeBytes(payload.bytes); },
            buf -> { int length = Math.min(buf.readableBytes(), 4096); byte[] data = new byte[length]; buf.readBytes(data); return new RawPayload(data); });
    private static final Channel<CustomPacketPayload> NETWORK = ChannelBuilder.named(CHANNEL_ID).optional().payloadChannel().play().bidirectional().addMain(PAYLOAD_TYPE, PAYLOAD_CODEC, VoidedClientForge::receive).build();
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("voidedclient", "controls"));
    private static final long REPAIR_REPEAT_MS = 350L;

    private final KeyMapping repair = new KeyMapping("key.voidedclient.repair", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
    private final KeyMapping rpgStats = new KeyMapping("key.voidedclient.rpg_stats", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY);
    private final KeyMapping explorationJournal = new KeyMapping("key.voidedclient.exploration_journal", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY);
    private final KeyMapping companion = new KeyMapping("key.voidedclient.companion", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, CATEGORY);
    private final KeyMapping powerStrike = skillKey("key.voidedclient.skill.power_strike", GLFW.GLFW_KEY_Z);
    private final KeyMapping bulwark = skillKey("key.voidedclient.skill.bulwark", GLFW.GLFW_KEY_X);
    private final KeyMapping secondWind = skillKey("key.voidedclient.skill.second_wind", GLFW.GLFW_KEY_C);
    private final KeyMapping dash = skillKey("key.voidedclient.skill.dash", GLFW.GLFW_KEY_V);
    private final KeyMapping arcaneSurge = skillKey("key.voidedclient.skill.arcane_surge", GLFW.GLFW_KEY_B);
    private long lastRepairSent;
    private long lastStatsSent;
    private int helloDelay = -1;

    public VoidedClientForge() {
        RegisterKeyMappingsEvent.BUS.addListener(this::keys);
        TickEvent.ClientTickEvent.Post.BUS.addListener(this::tick);
        ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(this::login);
        ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(this::logout);
    }
    private void keys(RegisterKeyMappingsEvent event) { event.register(repair); event.register(rpgStats); event.register(explorationJournal); event.register(companion); event.register(powerStrike); event.register(bulwark); event.register(secondWind); event.register(dash); event.register(arcaneSurge); }
    private void login(ClientPlayerNetworkEvent.LoggingIn event) { helloDelay = 20; lastRepairSent = 0L; lastStatsSent = 0L; }
    private void logout(ClientPlayerNetworkEvent.LoggingOut event) { helloDelay = -1; ServerContextState.clear(); }
    private void tick(TickEvent.ClientTickEvent.Post event) {
        if (helloDelay >= 0 && --helloDelay == 0) { sendHello(); helloDelay = -1; }
        Minecraft mc = Minecraft.getInstance(); if (mc.player == null || mc.gui.screen() != null) return;
        long now = System.currentTimeMillis();
        if (ServerContextState.gameplayEnabled() && repair.isDown() && now - lastRepairSent >= REPAIR_REPEAT_MS) { lastRepairSent = now; send(VoidedProtocol.action(VoidedProtocol.ACTION_REPAIR)); }
        if (ServerContextState.rpgEnabled() && rpgStats.isDown() && now - lastStatsSent >= 500L) { lastStatsSent = now; send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_STATS)); }
        while (explorationJournal.consumeClick()) send(VoidedProtocol.action(VoidedProtocol.ACTION_EXPLORATION_JOURNAL));
        while (companion.consumeClick()) send(VoidedProtocol.action(VoidedProtocol.ACTION_COMPANION_HOME));
        castIfPressed(powerStrike, "power-strike"); castIfPressed(bulwark, "bulwark"); castIfPressed(secondWind, "second-wind"); castIfPressed(dash, "dash"); castIfPressed(arcaneSurge, "arcane-surge");
    }
    private static KeyMapping skillKey(String translation, int key) { return new KeyMapping(translation, InputConstants.Type.KEYSYM, key, CATEGORY); }
    private static void castIfPressed(KeyMapping mapping, String skill) {
        while (mapping.consumeClick()) {
            String dashInput = "";
            if ("dash".equals(skill)) try { Minecraft mc = Minecraft.getInstance(); if (mc.player != null) { var movement = mc.player.input.getMoveVector(); dashInput = String.format(java.util.Locale.ROOT, "dash:%.3f,%.3f,%.3f,%.3f", mc.player.getYRot(), mc.player.getXRot(), movement.x, movement.y); } } catch (Throwable ignored) {}
            send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_SKILL_CAST, skill, dashInput));
        }
    }
    private static void sendHello() {
        String gameVersion; try { gameVersion = SharedConstants.getCurrentVersion().id(); } catch (Throwable ignored) { gameVersion = "26.2"; }
        send(VoidedProtocol.hello("forge", "1.10.0", gameVersion,
                VoidedProtocol.CAP_RPG_SKILLS + "," + VoidedProtocol.CAP_EXPLORATION_JOURNAL + "," + VoidedProtocol.CAP_EXPLORATION_CONTRACT + "," + VoidedProtocol.CAP_COMPANION_HOME + "," + VoidedProtocol.CAP_LEADERBOARDS + "," + VoidedProtocol.CAP_LINKED_ACCOUNTS + "," + VoidedProtocol.CAP_SERVER_NAVIGATION + "," + VoidedProtocol.CAP_SERVER_CONTEXT + "," + VoidedProtocol.CAP_NETHER_COMPANION + "," + VoidedProtocol.CAP_END_COMPANION + "," + VoidedProtocol.CAP_ENDGAME_COMPANION));
    }
    private static void receive(RawPayload payload, net.minecraftforge.event.network.CustomPayloadEvent.Context context) {
        if (!context.isClientSide()) return;
        String message = new String(payload.bytes(), StandardCharsets.UTF_8);
        if (message.startsWith("VC1|PROBE|")) { ServerContextState.awaitingBackend(); sendHello(); return; }
        if (message.startsWith("VC1|SERVER_CONTEXT|")) { ServerContextState state=ServerContextState.parse(message); if(state!=null)ServerContextState.update(state); return; }
        if (message.startsWith("VC1|RPG_STATS|") || message.startsWith("VC1|RPG_STATS_V2|") || message.startsWith("VC1|RPG_STATS_V3|")) { RpgStatsState state = RpgStatsState.parse(message); if (state != null) Minecraft.getInstance().gui.setScreen(CompanionScreen.rpg(state, VoidedClientForge::send)); }
        if (message.startsWith("VC1|EXPLORATION_JOURNAL|")) { ExplorationJournalState state = ExplorationJournalState.parse(message); if (state != null) Minecraft.getInstance().gui.setScreen(CompanionScreen.explore(state, VoidedClientForge::send)); }
        if (message.startsWith("VC1|EXPLORATION_CONTRACT|")) { ExplorationContractState state = ExplorationContractState.parse(message); if (state != null) Minecraft.getInstance().gui.setScreen(CompanionScreen.exploreContract(state, VoidedClientForge::send)); }
        if (message.startsWith("VC1|COMPANION_HOME|")) { CompanionHomeState state=CompanionHomeState.parse(message);if(state!=null)Minecraft.getInstance().gui.setScreen(CompanionScreen.home(state,VoidedClientForge::send)); }
        if (message.startsWith("VC1|LEADERBOARD|")) { LeaderboardState state=LeaderboardState.parse(message);if(state!=null)Minecraft.getInstance().gui.setScreen(CompanionScreen.leaderboard(state,VoidedClientForge::send)); }
        if (message.startsWith("VC1|LINKED_ACCOUNTS|")) { LinkedAccountsState state=LinkedAccountsState.parse(message);if(state!=null)Minecraft.getInstance().gui.setScreen(CompanionScreen.accounts(state,VoidedClientForge::send)); }
        if (message.startsWith("VC1|SERVER_LIST|")) { ServerListState state=ServerListState.parse(message);if(state!=null)Minecraft.getInstance().gui.setScreen(CompanionScreen.servers(state,VoidedClientForge::send)); }
        if (message.startsWith("VC1|NETHER_COMPANION|")) { NetherCompanionState state=NetherCompanionState.parse(message);if(state!=null)Minecraft.getInstance().gui.setScreen(CompanionScreen.nether(state,VoidedClientForge::send)); }
        if (message.startsWith("VC1|END_COMPANION|")) { EndCompanionState state=EndCompanionState.parse(message);if(state!=null)Minecraft.getInstance().gui.setScreen(CompanionScreen.end(state,VoidedClientForge::send)); }
        if (message.startsWith("VC1|ENDGAME_COMPANION|")) { EndgameCompanionState state=EndgameCompanionState.parse(message);if(state!=null)Minecraft.getInstance().gui.setScreen(CompanionScreen.endgame(state,VoidedClientForge::send)); }
    }
    private static void send(byte[] bytes) {
        try { Minecraft mc = Minecraft.getInstance(); if (mc.getConnection() == null) return; NETWORK.send(new RawPayload(bytes), PacketDistributor.SERVER.noArg()); } catch (Throwable ignored) {}
    }
    private record RawPayload(byte[] bytes) implements CustomPacketPayload { @Override public Type<? extends CustomPacketPayload> type() { return PAYLOAD_TYPE; } }
}
