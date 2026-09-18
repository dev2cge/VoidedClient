# Lunar Client compatibility

VoidedClient does not hook Lunar classes. On Lunar profiles that permit third-party Fabric mods, use the Fabric build matching that Minecraft version. The mod registers an ordinary Minecraft key mapping and one custom-payload channel only.

This intentionally avoids replacing or patching Lunar HUD, CPS, keystrokes, freelook, performance, cosmetics, networking or input modules. If Lunar or another mod changes the same vanilla keybinding/networking internals, normal mod conflict rules still apply; no client mod can guarantee compatibility with every possible third-party modification.
