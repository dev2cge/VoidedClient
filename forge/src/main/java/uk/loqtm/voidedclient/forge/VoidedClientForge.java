package uk.loqtm.voidedclient.forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.SharedConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import org.lwjgl.glfw.GLFW;
import uk.loqtm.voidedclient.protocol.VoidedProtocol;

import java.nio.charset.StandardCharsets;

/** Minecraft 26.2 Forge adapter. The optional channel remains compatible with Paper/vanilla servers. */
@Mod("voidedclient")
public final class VoidedClientForge {
    private static final Identifier CHANNEL_ID = Identifier.fromNamespaceAndPath("voidedcore", "client");
    private static final CustomPacketPayload.Type<RawPayload> TYPE = new CustomPacketPayload.Type<>(CHANNEL_ID);
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("voidedclient", "controls"));
    private static final Channel<CustomPacketPayload> NETWORK = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath("voidedclient", "bridge"))
            .optionalServer()
            .payloadChannel()
            .play()
            .add(TYPE, RawPayload.CODEC, VoidedClientForge::handlePayload)
            .build();
    private static final long REPAIR_REPEAT_MS = 350L;
    private static volatile Connection connection;

    private final KeyMapping repair = new KeyMapping("key.voidedclient.repair", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
    private final KeyMapping rpgStats = new KeyMapping("key.voidedclient.rpg_stats", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY);
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

    private void keys(RegisterKeyMappingsEvent event) {
        event.register(repair);
        event.register(rpgStats);
        event.register(powerStrike);
        event.register(bulwark);
        event.register(secondWind);
        event.register(dash);
        event.register(arcaneSurge);
    }

    private void login(ClientPlayerNetworkEvent.LoggingIn event) {
        connection = event.getConnection();
        helloDelay = 20;
        lastRepairSent = 0L;
        lastStatsSent = 0L;
    }

    private void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        helloDelay = -1;
        connection = null;
    }

    private void tick(TickEvent.ClientTickEvent.Post event) {
        if (helloDelay >= 0 && --helloDelay == 0) {
            sendHello();
            helloDelay = -1;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gui.screen() != null) return;
        long now = System.currentTimeMillis();
        if (repair.isDown() && now - lastRepairSent >= REPAIR_REPEAT_MS) {
            lastRepairSent = now;
            send(VoidedProtocol.action(VoidedProtocol.ACTION_REPAIR));
        }
        if (rpgStats.isDown() && now - lastStatsSent >= 500L) {
            lastStatsSent = now;
            send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_STATS));
        }
        castIfPressed(powerStrike, "power-strike");
        castIfPressed(bulwark, "bulwark");
        castIfPressed(secondWind, "second-wind");
        castIfPressed(dash, "dash");
        castIfPressed(arcaneSurge, "arcane-surge");
    }

    private static KeyMapping skillKey(String translation, int key) {
        return new KeyMapping(translation, InputConstants.Type.KEYSYM, key, CATEGORY);
    }

    private static void castIfPressed(KeyMapping mapping, String skill) {
        while (mapping.consumeClick()) {
            String movement = "";
            if ("dash".equals(skill)) {
                try {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player != null) {
                        var velocity = mc.player.getDeltaMovement();
                        movement = String.format(java.util.Locale.ROOT, "%.5f,%.5f", velocity.x, velocity.z);
                    }
                } catch (Throwable ignored) {
                }
            }
            send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_SKILL_CAST, skill, movement));
        }
    }

    private static void handlePayload(RawPayload payload, CustomPayloadEvent.Context context) {
        if (!context.isClientSide()) return;
        String message = new String(payload.bytes(), StandardCharsets.UTF_8);
        if (message.startsWith("VC1|PROBE|")) context.enqueueWork(VoidedClientForge::sendHello);
        context.setPacketHandled(true);
    }

    private static void sendHello() {
        String gameVersion;
        try {
            gameVersion = SharedConstants.getCurrentVersion().id();
        } catch (Throwable ignored) {
            gameVersion = "26.2";
        }
        send(VoidedProtocol.hello("forge", "1.7.1", gameVersion, VoidedProtocol.CAP_RPG_SKILLS));
    }

    private static void send(byte[] bytes) {
        try {
            Connection current = connection;
            if (current == null || bytes.length > 512) return;
            NETWORK.send(new RawPayload(bytes), current);
        } catch (Throwable ignored) {
            // Optional companion: never make an unrelated server unusable if it does not understand the channel.
        }
    }

    public record RawPayload(byte[] bytes) implements CustomPacketPayload {
        public static final StreamCodec<RegistryFriendlyByteBuf, RawPayload> CODEC = StreamCodec.of(
                (buf, payload) -> {
                    if (payload.bytes.length > 512) throw new IllegalArgumentException("VoidedClient payload too large");
                    buf.writeBytes(payload.bytes);
                },
                buf -> {
                    int length = Math.min(buf.readableBytes(), 512);
                    byte[] data = new byte[length];
                    buf.readBytes(data);
                    return new RawPayload(data);
                });

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
