package uk.loqtm.voidedclient.forge;

import com.mojang.blaze3d.platform.InputConstants;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import uk.loqtm.voidedclient.protocol.VoidedProtocol;

/** Forge adapter using a raw vanilla custom-payload packet so the Paper server needs no Forge networking stack. */
@Mod(value="voidedclient", dist=Dist.CLIENT)
public final class VoidedClientForge {
    private static final ResourceLocation CHANNEL = new ResourceLocation("voidedcore", "client");
    private static final long REPAIR_REPEAT_MS = 350L;
    private final KeyMapping repair = new KeyMapping("key.voidedclient.repair", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.voidedclient");
    private final KeyMapping rpgStats = new KeyMapping("key.voidedclient.rpg_stats", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.voidedclient");
    private final KeyMapping powerStrike = skillKey("key.voidedclient.skill.power_strike", GLFW.GLFW_KEY_Z);
    private final KeyMapping bulwark = skillKey("key.voidedclient.skill.bulwark", GLFW.GLFW_KEY_X);
    private final KeyMapping secondWind = skillKey("key.voidedclient.skill.second_wind", GLFW.GLFW_KEY_C);
    private final KeyMapping dash = skillKey("key.voidedclient.skill.dash", GLFW.GLFW_KEY_V);
    private final KeyMapping arcaneSurge = skillKey("key.voidedclient.skill.arcane_surge", GLFW.GLFW_KEY_B);
    private long lastRepairSent;
    private long lastStatsSent;
    private int helloDelay = -1;

    public VoidedClientForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::keys);
        MinecraftForge.EVENT_BUS.addListener(this::tick);
        MinecraftForge.EVENT_BUS.addListener(this::login);
        MinecraftForge.EVENT_BUS.addListener(this::logout);
    }

    private void keys(RegisterKeyMappingsEvent event) { event.register(repair); event.register(rpgStats); event.register(powerStrike); event.register(bulwark); event.register(secondWind); event.register(dash); event.register(arcaneSurge); }
    private void login(ClientPlayerNetworkEvent.LoggingIn event) { helloDelay = 20; lastRepairSent = 0L; lastStatsSent = 0L; }
    private void logout(ClientPlayerNetworkEvent.LoggingOut event) { helloDelay = -1; }

    private void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (helloDelay >= 0 && --helloDelay == 0) { sendHello(); helloDelay = -1; }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        long now = System.currentTimeMillis();
        if (repair.isDown() && now - lastRepairSent >= REPAIR_REPEAT_MS) { lastRepairSent = now; send(VoidedProtocol.action(VoidedProtocol.ACTION_REPAIR)); }
        if (rpgStats.isDown() && now - lastStatsSent >= 500L) { lastStatsSent = now; send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_STATS)); }
        castIfPressed(powerStrike, "power-strike"); castIfPressed(bulwark, "bulwark"); castIfPressed(secondWind, "second-wind"); castIfPressed(dash, "dash"); castIfPressed(arcaneSurge, "arcane-surge");
    }

    private static KeyMapping skillKey(String translation, int key) { return new KeyMapping(translation, InputConstants.Type.KEYSYM, key, "category.voidedclient"); }
    private static void castIfPressed(KeyMapping mapping, String skill) {
        while (mapping.consumeClick()) {
            String look = "";
            if ("dash".equals(skill)) try {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    look = String.format(java.util.Locale.ROOT, "look:%.3f,%.3f", mc.player.getYRot(), mc.player.getXRot());
                }
            } catch (Throwable ignored) {}
            send(VoidedProtocol.action(VoidedProtocol.ACTION_RPG_SKILL_CAST, skill, look));
        }
    }

    private static void sendHello() {
        send(VoidedProtocol.hello("forge", "1.8.0", SharedConstants.getCurrentVersion().getName(), VoidedProtocol.CAP_RPG_SKILLS));
    }

    private static void send(byte[] bytes) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getConnection() == null) return;
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer(bytes.length));
            buf.writeBytes(bytes);
            mc.getConnection().send(new ServerboundCustomPayloadPacket(CHANNEL, buf));
        } catch (Throwable ignored) {
            // Optional companion: never make an unrelated server unusable if it does not understand the channel.
        }
    }
}
