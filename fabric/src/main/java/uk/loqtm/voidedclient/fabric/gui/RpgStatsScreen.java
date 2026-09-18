package uk.loqtm.voidedclient.fabric.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import uk.loqtm.voidedclient.protocol.RpgStatsState;

import java.util.Locale;
import java.util.function.BiConsumer;

public final class RpgStatsScreen extends Screen {
    private static final int PANEL_HEIGHT = 372;
    private static final int ROW_TOP_OFFSET = 145;
    private static final int ROW_SPACING = 34;
    private static final String[] STAT_KEYS = {"strength", "defence", "vitality", "agility", "intelligence"};
    private static final String[] STAT_NAMES = {"Strength", "Defence", "Vitality", "Agility", "Intelligence"};
    private final RpgStatsState state;
    private final BiConsumer<String, String> spend;
    private final Runnable respec;
    private boolean respecArmed;

    public RpgStatsScreen(RpgStatsState state, BiConsumer<String, String> spend, Runnable respec) {
        super(Component.literal("Voided RPG"));
        this.state = state;
        this.spend = spend;
        this.respec = respec;
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(500, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(8, (this.height - PANEL_HEIGHT) / 2);
        int rowTop = top + ROW_TOP_OFFSET;
        addStatButtons(left, panelWidth, rowTop, "strength");
        addStatButtons(left, panelWidth, rowTop + ROW_SPACING, "defence");
        addStatButtons(left, panelWidth, rowTop + ROW_SPACING * 2, "vitality");
        addStatButtons(left, panelWidth, rowTop + ROW_SPACING * 3, "agility");
        addStatButtons(left, panelWidth, rowTop + ROW_SPACING * 4, "intelligence");

        Button respecButton = Button.builder(Component.literal("Respec"), button -> {
            if (!respecArmed) {
                respecArmed = true;
            } else {
                respecArmed = false;
                respec.run();
            }
        }).bounds(left + 12, top + 340, 72, 22).build();
        respecButton.active = state.spentPoints() > 0 && state.respecCooldownSeconds() == 0L;
        this.addRenderableWidget(respecButton);

        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> this.minecraft.gui.setScreen(null))
                .bounds(left + panelWidth - 72, top + 340, 60, 22).build());
    }

    private void addStatButtons(int left, int panelWidth, int y, String stat) {
        boolean enabled = state.points() > 0 && state.stat(stat) < state.maxPointsPerStat();
        Button one = Button.builder(Component.literal("+1"), button -> { if (canSpend(stat)) spend.accept(stat, "1"); })
                .bounds(left + panelWidth - 144, y + 5, 38, 22).build();
        Button five = Button.builder(Component.literal("+5"), button -> { if (canSpend(stat)) spend.accept(stat, "5"); })
                .bounds(left + panelWidth - 102, y + 5, 38, 22).build();
        Button max = Button.builder(Component.literal("MAX"), button -> { if (canSpend(stat)) spend.accept(stat, "max"); })
                .bounds(left + panelWidth - 60, y + 5, 48, 22).build();
        one.active = enabled; five.active = enabled; max.active = enabled;
        this.addRenderableWidget(one); this.addRenderableWidget(five); this.addRenderableWidget(max);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // Screen extracts child widgets first. Every interactive control is then repainted with
        // an explicit high-contrast surface below, avoiding resource-pack and row-overlay issues.
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int panelWidth = Math.min(500, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(8, (this.height - PANEL_HEIGHT) / 2);
        int right = left + panelWidth;
        int bottom = top + PANEL_HEIGHT;

        graphics.fill(left - 1, top - 1, right + 1, bottom + 1, 0xFF6D3FC0);
        graphics.fill(left, top, right, bottom, 0xF5110D1A);
        graphics.fill(left, top, right, top + 3, 0xFF9B5CF6);
        graphics.fill(left, top + 3, right, top + 43, 0xFF181222);
        graphics.centeredText(this.font, "VOIDED RPG", this.width / 2, top + 11, 0xFFFFFFFF);
        graphics.centeredText(this.font, state.buildName().toUpperCase(Locale.ROOT) + "  •  POWER " + fmt(state.buildScore()), this.width / 2, top + 27, 0xFFD8B4FE);

        graphics.text(this.font, "LEVEL " + state.level(), left + 14, top + 50, 0xFFFFFFFF, true);
        String xp = state.xpNeeded() <= 0 ? "MAX LEVEL" : state.xpIntoLevel() + " / " + state.xpNeeded() + " XP";
        graphics.text(this.font, xp, right - 14 - this.font.width(xp), top + 50, 0xFFE9D5FF, true);
        int barLeft = left + 14, barRight = right - 14, barTop = top + 64;
        drawProgressBar(graphics, barLeft, barRight, barTop, state.progress(), 0xFF8B5CF6);

        graphics.text(this.font, "MANA " + fmt(state.currentMana()) + " / " + fmt(state.manaCapacity()), left + 14, top + 79, 0xFF67E8F9, true);
        String manaRegen="+"+fmt(state.manaRegenPerSecond())+"/s";
        graphics.text(this.font,manaRegen,right-14-this.font.width(manaRegen),top+79,0xFF67E8F9,false);
        int manaLeft=left+14,manaRight=right-14,manaTop=top+92;
        double manaProgress=state.manaCapacity()<=0D?0D:Math.max(0D,Math.min(1D,state.currentMana()/state.manaCapacity()));
        drawProgressBar(graphics, manaLeft, manaRight, manaTop, manaProgress, 0xFF06B6D4);

        graphics.fill(left + 10, top + 105, right - 10, top + 130, 0xFF1A1523);
        graphics.text(this.font, "UNSPENT  " + state.points(), left + 16, top + 111, state.points() > 0 ? 0xFFF5D0FE : 0xFFAAA2B5, true);
        String allocation = "ALLOCATED  " + state.spentPoints() + " / " + state.totalPoints();
        graphics.text(this.font, allocation, right - 16 - this.font.width(allocation), top + 111, 0xFFD0C8DB, false);

        graphics.text(this.font, "ATTRIBUTES", left + 12, top + 134, 0xFFB794F6, true);
        String meta = "SOFT " + state.softCap() + "  •  HARD " + state.maxPointsPerStat() + "  •  " + state.equippedPieces() + " GEAR  •  " + state.activeEffects() + " EFFECTS";
        graphics.text(this.font, meta, right - 12 - this.font.width(meta), top + 134, 0xFF8F879B, false);

        int rowTop = top + ROW_TOP_OFFSET;
        statRow(graphics, left, right, rowTop, "Strength", state.strength(), "+" + fmt(state.damageBonus()) + " damage  •  +" + fmt(state.knockbackBonus()) + "% knockback", 0xFFF87171);
        statRow(graphics, left, right, rowTop + ROW_SPACING, "Defence", state.defence(), fmt(state.defenceReductionPercent()) + "% damage reduction", 0xFF60A5FA);
        statRow(graphics, left, right, rowTop + ROW_SPACING * 2, "Vitality", state.vitality(), "+" + fmt(state.healthBonus()) + " health  •  +" + fmt(state.healthRegenPerSecond()) + "/s regen", 0xFF4ADE80);
        statRow(graphics, left, right, rowTop + ROW_SPACING * 3, "Agility", state.agility(), "+" + fmt(state.movementSpeedBonus()) + "% move  •  +" + fmt(state.attackSpeedBonus()) + "% attack  •  " + fmt(state.cooldownReduction()) + "% CDR", 0xFFF0ABFC);
        statRow(graphics, left, right, rowTop + ROW_SPACING * 4, "Intelligence", state.intelligence(), "+" + fmt(state.ritualPower()) + "% ritual  •  +" + fmt(state.skillPower()) + "% skill", 0xFFFACC15);

        for (int i = 0; i < STAT_KEYS.length; i++) drawStatButtons(graphics, mouseX, mouseY, left, panelWidth, rowTop + ROW_SPACING * i, STAT_KEYS[i], STAT_NAMES[i]);
        drawStatHelp(graphics, mouseX, mouseY, left, rowTop, top);

        String respecText;
        if (state.respecCooldownSeconds() < 0L) respecText = "Respecs are disabled on this server.";
        else if (state.spentPoints() <= 0) respecText = "No allocated points to respec.";
        else if (state.respecCooldownSeconds() > 0L) respecText = "Respec available in " + duration(state.respecCooldownSeconds()) + ".";
        else if (respecArmed) respecText = "Click Respec again to confirm a full point refund.";
        else respecText = "Respec refunds all allocated points. Click once to arm.";
        boolean respecEnabled=state.spentPoints()>0&&state.respecCooldownSeconds()==0L;
        drawButton(graphics,mouseX,mouseY,left+12,top+340,72,22,"RESPEC",respecEnabled,respecArmed?0xFF92400E:0xFF4C1D95);
        drawButton(graphics,mouseX,mouseY,right-72,top+340,60,22,"CLOSE",true,0xFF374151);
        graphics.text(this.font, respecText, left + 94, top + 347, respecArmed ? 0xFFFBBF24 : 0xFFAAA2B5, false);
    }

    private void statRow(GuiGraphicsExtractor graphics, int left, int right, int y, String name, int value, String bonus, int accent) {
        graphics.fill(left + 10, y, right - 10, y + 32, 0xFF1A1523);
        graphics.fill(left + 10, y, left + 14, y + 32, accent);
        graphics.fill(right - 154, y + 3, right - 153, y + 29, 0xFF342A42);
        String key=name.toLowerCase(Locale.ROOT);
        int total=state.totalStat(key),gear=state.equipment(key),temporary=state.temporary(key);
        String breakdown=value+" base"+(gear==0?"":" +"+gear+" gear")+(temporary==0?"":temporary>0?" +"+temporary+" buff":" -"+Math.abs(temporary)+" debuff");
        String badge=total>=state.maxPointsPerStat()?"MAX":total>=state.softCap()?"MASTERY":"";
        graphics.text(this.font, name.toUpperCase(Locale.ROOT)+"  "+total, left + 22, y + 5, 0xFFFFFFFF, true);
        graphics.text(this.font, breakdown+(badge.isEmpty()?"":"  •  "+badge), left + 116, y + 5, 0xFFB9B1C5, false);
        graphics.text(this.font, bonus, left + 22, y + 18, 0xFFD3CADF, false);
    }

    private void drawStatButtons(GuiGraphicsExtractor graphics,int mouseX,int mouseY,int left,int panelWidth,int y,String stat,String displayName){
        boolean enabled=canSpend(stat);
        drawButton(graphics,mouseX,mouseY,left+panelWidth-144,y+5,38,22,"+1",enabled,0xFF5B21B6);
        drawButton(graphics,mouseX,mouseY,left+panelWidth-102,y+5,38,22,"+5",enabled,0xFF6D28D9);
        drawButton(graphics,mouseX,mouseY,left+panelWidth-60,y+5,48,22,"MAX",enabled,0xFF7C3AED);
        if(mouseY>=y+5&&mouseY<y+27&&mouseX>=left+panelWidth-144&&mouseX<left+panelWidth-12){
            String help=enabled?"Spend points on "+displayName+" — changes are validated by the server.":state.points()<=0?"No unspent stat points available.":displayName+" has reached its allocation cap.";
            int top=Math.max(8,(this.height-PANEL_HEIGHT)/2);
            graphics.text(this.font,help,left+12,top+326,enabled?0xFFD8B4FE:0xFFAAA2B5,false);
        }
    }

    private void drawStatHelp(GuiGraphicsExtractor graphics,int mouseX,int mouseY,int left,int rowTop,int top){
        String help=null;
        for(int i=0;i<STAT_KEYS.length;i++){
            int y=rowTop+ROW_SPACING*i;
            if(mouseX>=left+18&&mouseX<left+112&&mouseY>=y+2&&mouseY<y+17){
                help=switch(STAT_KEYS[i]){
                    case "strength" -> "Strength: physical damage and knockback power.";
                    case "defence" -> "Defence: reduces incoming damage with diminishing returns.";
                    case "vitality" -> "Vitality: maximum health and passive health regeneration.";
                    case "agility" -> "Agility: movement speed, attack speed and shorter skill cooldowns.";
                    case "intelligence" -> "Intelligence: mana, mana regen, ritual power and skill power.";
                    default -> null;
                };
                break;
            }
        }
        if(help!=null)graphics.text(this.font,help,left+12,top+326,0xFFE9D5FF,false);
    }

    private void drawButton(GuiGraphicsExtractor graphics,int mouseX,int mouseY,int x,int y,int width,int height,String text,boolean enabled,int base){
        boolean hover=enabled&&mouseX>=x&&mouseX<x+width&&mouseY>=y&&mouseY<y+height;
        int border=enabled?(hover?0xFFE9D5FF:0xFFB794F6):0xFF403748;
        int fill=enabled?(hover?lighten(base):base):0xFF211C29;
        int color=enabled?0xFFFFFFFF:0xFF8A8292;
        graphics.fill(x,y,x+width,y+height,border);
        graphics.fill(x+1,y+1,x+width-1,y+height-1,fill);
        graphics.centeredText(this.font,text,x+width/2,y+7,color);
    }

    private void drawProgressBar(GuiGraphicsExtractor graphics,int left,int right,int top,double progress,int color){
        graphics.fill(left,top,right,top+8,0xFF332A40);
        graphics.fill(left+1,top+1,right-1,top+7,0xFF1E1926);
        int width=Math.max(0,(int)((right-left-2)*Math.max(0D,Math.min(1D,progress))));
        graphics.fill(left+1,top+1,left+1+width,top+7,color);
    }

    private boolean canSpend(String stat){return state.points()>0&&state.stat(stat)<state.maxPointsPerStat();}
    private static int lighten(int color){int r=Math.min(255,((color>>16)&255)+26),g=Math.min(255,((color>>8)&255)+26),b=Math.min(255,(color&255)+26);return 0xFF000000|(r<<16)|(g<<8)|b;}

    private static String duration(long seconds) {
        long h = seconds / 3600L; seconds %= 3600L;
        long m = seconds / 60L; seconds %= 60L;
        if (h > 0L) return h + "h " + m + "m";
        if (m > 0L) return m + "m " + seconds + "s";
        return seconds + "s";
    }

    private static String fmt(double value) {
        return Math.abs(value - Math.rint(value)) < 0.0001D ? Integer.toString((int)Math.rint(value)) : String.format(Locale.ROOT, "%.2f", value);
    }
}
