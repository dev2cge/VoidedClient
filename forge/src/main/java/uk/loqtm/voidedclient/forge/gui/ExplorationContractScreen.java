package uk.loqtm.voidedclient.forge.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import uk.loqtm.voidedclient.protocol.ExplorationContractState;

import java.util.Locale;

public final class ExplorationContractScreen extends Screen {
    private static final int PANEL_HEIGHT=244;
    private final ExplorationContractState state;
    private final Runnable back;
    public ExplorationContractScreen(ExplorationContractState state,Runnable back){super(Component.literal("Daily Expedition"));this.state=state;this.back=back;}
    @Override protected void init(){int w=Math.min(430,width-24),left=(width-w)/2,top=Math.max(8,(height-PANEL_HEIGHT)/2);if(back!=null)addRenderableWidget(Button.builder(Component.literal("Back to Journal"),b->back.run()).bounds(left+12,top+210,112,22).build());addRenderableWidget(Button.builder(Component.literal("Close"),b->minecraft.gui.setScreen(null)).bounds(left+w-72,top+210,60,22).build());}
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta){super.extractRenderState(g,mouseX,mouseY,delta);int w=Math.min(430,width-24),left=(width-w)/2,top=Math.max(8,(height-PANEL_HEIGHT)/2),right=left+w;g.fill(left-1,top-1,right+1,top+PANEL_HEIGHT+1,0xFF5B21B6);g.fill(left,top,right,top+PANEL_HEIGHT,0xF5100D18);g.fill(left,top,right,top+3,0xFF8B5CF6);g.centeredText(font,"DAILY EXPEDITION",width/2,top+14,0xFFC4B5FD);g.centeredText(font,state.name().toUpperCase(Locale.ROOT),width/2,top+34,0xFFFFFFFF);g.centeredText(font,state.description(),width/2,top+57,0xFFB9AFC8);int barL=left+28,barR=right-28,barY=top+85;g.fill(barL,barY,barR,barY+12,0xFF30283B);g.fill(barL+2,barY+2,barL+2+(int)((barR-barL-4)*state.progressRatio()),barY+10,state.complete()?0xFF22C55E:0xFF8B5CF6);String progress=state.complete()?"COMPLETE":Math.min(state.progress(),state.target())+" / "+state.target();g.centeredText(font,progress,width/2,top+103,state.complete()?0xFF86EFAC:0xFFE9D5FF);g.fill(left+20,top+126,right-20,top+190,0xFF181320);g.text(font,"REWARDS",left+32,top+137,0xFFC4B5FD,true);g.text(font,state.rewardXp()+" RPG XP",left+32,top+155,0xFFFFFFFF,false);g.text(font,state.rewardAmount()+"x "+title(state.rewardItem()),left+32,top+171,0xFFFFFFFF,false);String detail="elite-hunt".equals(state.type())?"Requires rarity tier "+state.minimumRarityTier()+"+":"Resets daily at 00:00 UTC";g.text(font,detail,right-32-font.width(detail),top+155,0xFFAAA2B5,false);String date="Contract date: "+state.date();g.text(font,date,right-32-font.width(date),top+171,0xFF777080,false);}
    private static String title(String value){StringBuilder out=new StringBuilder();for(String p:value.split(" "))if(!p.isEmpty()){if(out.length()>0)out.append(' ');out.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));}return out.toString();}
}
