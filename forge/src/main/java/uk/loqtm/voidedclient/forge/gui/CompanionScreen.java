package uk.loqtm.voidedclient.forge.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import uk.loqtm.voidedclient.protocol.*;

import java.util.function.Consumer;

/** Unified Voided Network companion dashboard. Data is always supplied by the server. */
public final class CompanionScreen extends Screen {
    private enum View { HOME, LEADERBOARD, ACCOUNTS, SERVERS }
    private final View view;
    private final Object state;
    private final Consumer<byte[]> send;

    private CompanionScreen(View view,Object state,Consumer<byte[]> send){super(Component.literal("Voided Network"));this.view=view;this.state=state;this.send=send;}
    public static CompanionScreen home(CompanionHomeState state,Consumer<byte[]> send){return new CompanionScreen(View.HOME,state,send);}
    public static CompanionScreen leaderboard(LeaderboardState state,Consumer<byte[]> send){return new CompanionScreen(View.LEADERBOARD,state,send);}
    public static CompanionScreen accounts(LinkedAccountsState state,Consumer<byte[]> send){return new CompanionScreen(View.ACCOUNTS,state,send);}
    public static CompanionScreen servers(ServerListState state,Consumer<byte[]> send){return new CompanionScreen(View.SERVERS,state,send);}

    @Override protected void init(){
        int w=Math.min(520,width-24),left=(width-w)/2,top=Math.max(8,(height-300)/2);
        tab(left+12,top+36,72,"Home",()->request(VoidedProtocol.ACTION_COMPANION_HOME));
        tab(left+88,top+36,104,"Leaderboards",()->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,"balance","1"));
        tab(left+196,top+36,82,"Accounts",()->request(VoidedProtocol.ACTION_ACCOUNTS_REQUEST));
        tab(left+282,top+36,76,"Servers",()->request(VoidedProtocol.ACTION_SERVER_LIST));
        tab(left+362,top+36,68,"Explore",()->request(VoidedProtocol.ACTION_EXPLORATION_JOURNAL));
        tab(left+434,top+36,62,"RPG",()->request(VoidedProtocol.ACTION_RPG_STATS));
        if(view==View.HOME)initHome(left,top,w);
        else if(view==View.LEADERBOARD)initLeaderboard(left,top,w);
        else if(view==View.ACCOUNTS)initAccounts(left,top,w);
        else initServers(left,top,w);
        addRenderableWidget(Button.builder(Component.literal("Close"),b->minecraft.gui.setScreen(null)).bounds(left+w-72,top+266,60,22).build());
    }
    private void initHome(int left,int top,int w){
        addRenderableWidget(Button.builder(Component.literal("View Leaderboards"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,"balance","1")).bounds(left+24,top+166,146,24).build());
        addRenderableWidget(Button.builder(Component.literal("Linked Accounts"),b->request(VoidedProtocol.ACTION_ACCOUNTS_REQUEST)).bounds(left+176,top+166,146,24).build());
        addRenderableWidget(Button.builder(Component.literal("Server Browser"),b->request(VoidedProtocol.ACTION_SERVER_LIST)).bounds(left+328,top+166,146,24).build());
        addRenderableWidget(Button.builder(Component.literal("Exploration Journal"),b->request(VoidedProtocol.ACTION_EXPLORATION_JOURNAL)).bounds(left+100,top+202,146,24).build());
        addRenderableWidget(Button.builder(Component.literal("RPG Character"),b->request(VoidedProtocol.ACTION_RPG_STATS)).bounds(left+274,top+202,146,24).build());
    }
    private void initLeaderboard(int left,int top,int w){
        LeaderboardState s=(LeaderboardState)state;
        addRenderableWidget(Button.builder(Component.literal("Balance"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,"balance","1")).bounds(left+24,top+70,92,20).build());
        addRenderableWidget(Button.builder(Component.literal("Playtime"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,"playtime","1")).bounds(left+120,top+70,92,20).build());
        addRenderableWidget(Button.builder(Component.literal("RPG Level"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,"rpg","1")).bounds(left+216,top+70,92,20).build());
        addRenderableWidget(Button.builder(Component.literal("<"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,s.category(),Integer.toString(Math.max(1,s.page()-1)))).bounds(left+w-92,top+70,30,20).build());
        addRenderableWidget(Button.builder(Component.literal(">"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,s.category(),Integer.toString(Math.min(s.pages(),s.page()+1)))).bounds(left+w-58,top+70,30,20).build());
    }
    private void initAccounts(int left,int top,int w){
        LinkedAccountsState s=(LinkedAccountsState)state;
        String label=s.discordLinked()?"Unlink Discord":"Create Link Code";
        addRenderableWidget(Button.builder(Component.literal(label),b->request(s.discordLinked()?VoidedProtocol.ACTION_ACCOUNTS_UNLINK:VoidedProtocol.ACTION_ACCOUNTS_LINK)).bounds(left+24,top+184,148,24).build());
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_ACCOUNTS_REQUEST)).bounds(left+180,top+184,86,24).build());
    }
    private void initServers(int left,int top,int w){
        ServerListState s=(ServerListState)state;int y=top+78,index=0;
        for(ServerListState.Entry entry:s.entries()){
            if(index++>=7)break;
            String text=entry.name()+"  -  "+entry.online()+" online"+(entry.current()?"  (You are here)":"");
            Button button=Button.builder(Component.literal(text),b->{if(!entry.current())request(VoidedProtocol.ACTION_SERVER_SWITCH,entry.id());}).bounds(left+24,y,w-48,22).build();
            button.active=!entry.current();addRenderableWidget(button);y+=25;
        }
    }
    private void tab(int x,int y,int w,String label,Runnable action){addRenderableWidget(Button.builder(Component.literal(label),b->action.run()).bounds(x,y,w,22).build());}
    private void request(String action){send.accept(VoidedProtocol.action(action));}
    private void request(String action,String arg){send.accept(VoidedProtocol.action(action,arg));}
    private void request(String action,String a,String b){send.accept(VoidedProtocol.action(action,a,b));}

    @Override public void extractRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta){
        super.extractRenderState(g,mouseX,mouseY,delta);int w=Math.min(520,width-24),left=(width-w)/2,top=Math.max(8,(height-300)/2),right=left+w;
        g.fill(left-1,top-1,right+1,top+301,0xFF6D28D9);g.fill(left,top,right,top+300,0xF40D0B14);g.fill(left,top,right,top+4,0xFF8B5CF6);
        g.text(font,"VOIDED NETWORK",left+14,top+14,0xFFE9D5FF,true);g.text(font,"COMPANION",right-14-font.width("COMPANION"),top+14,0xFF8B5CF6,true);
        if(view==View.HOME)renderHome(g,left,top,w);else if(view==View.LEADERBOARD)renderLeaderboard(g,left,top,w);else if(view==View.ACCOUNTS)renderAccounts(g,left,top,w);else renderServers(g,left,top,w);
    }
    private void renderHome(GuiGraphicsExtractor g,int left,int top,int w){CompanionHomeState s=(CompanionHomeState)state;
        g.text(font,"Welcome back, "+s.player(),left+24,top+76,0xFFFFFFFF,true);g.text(font,s.server()+"  •  "+s.online()+" online",left+24,top+94,0xFFAAA2B5,false);
        card(g,left+24,top+116,142,"BALANCE",s.balance(),0xFF86EFAC);card(g,left+189,top+116,142,"RPG LEVEL",Integer.toString(s.rpgLevel()),0xFFC4B5FD);card(g,left+354,top+116,142,"DISCORD",s.discordLinked()?"LINKED":"NOT LINKED",s.discordLinked()?0xFF86EFAC:0xFFFBBF24);
        g.text(font,"Your server tools, progression and account connections in one place.",left+24,top+244,0xFF777080,false);
    }
    private void renderLeaderboard(GuiGraphicsExtractor g,int left,int top,int w){LeaderboardState s=(LeaderboardState)state;
        String title="rpg".equals(s.category())?"RPG LEVEL":s.category().toUpperCase();g.text(font,title+" LEADERBOARD",left+24,top+101,0xFFC4B5FD,true);g.text(font,"Page "+s.page()+" / "+s.pages(),left+w-24-font.width("Page "+s.page()+" / "+s.pages()),top+101,0xFFAAA2B5,false);
        int y=top+121;for(LeaderboardState.Entry e:s.entries()){int color=e.rank()<=3?0xFFFDE68A:0xFFFFFFFF;g.text(font,"#"+e.rank(),left+28,y,color,true);g.text(font,e.name(),left+70,y,0xFFFFFFFF,false);g.text(font,e.value(),left+w-28-font.width(e.value()),y,0xFFC4B5FD,false);y+=14;}
        g.text(font,"Your rank: "+(s.selfRank()==0?"Unranked":"#"+s.selfRank()),left+24,top+248,0xFFAAA2B5,false);
    }
    private void renderAccounts(GuiGraphicsExtractor g,int left,int top,int w){LinkedAccountsState s=(LinkedAccountsState)state;
        g.text(font,"LINKED ACCOUNTS",left+24,top+82,0xFFC4B5FD,true);g.fill(left+24,top+104,left+w-24,top+166,0xFF181320);
        g.text(font,"Minecraft",left+38,top+118,0xFFAAA2B5,false);g.text(font,s.minecraftName(),left+150,top+118,0xFFFFFFFF,true);
        g.text(font,"Discord",left+38,top+142,0xFFAAA2B5,false);String discord=s.discordLinked()?(s.discordName().isEmpty()?"Linked":s.discordName()):"Not linked";g.text(font,discord,left+150,top+142,s.discordLinked()?0xFF86EFAC:0xFFFBBF24,true);
        if(!s.linkCode().isEmpty()){g.text(font,"Link code: "+s.linkCode(),left+294,top+142,0xFFFDE68A,true);g.text(font,"Use this code with the Discord bot.",left+24,top+220,0xFFAAA2B5,false);}
        else g.text(font,"Only link status is sent to the client. No account tokens are exposed.",left+24,top+220,0xFF777080,false);
    }
    private void renderServers(GuiGraphicsExtractor g,int left,int top,int w){g.text(font,"SERVER BROWSER",left+24,top+62,0xFFC4B5FD,true);g.text(font,"Choose a destination. Transfers remain server-authoritative.",left+150,top+62,0xFF777080,false);}
    private void card(GuiGraphicsExtractor g,int x,int y,int w,String label,String value,int color){g.fill(x,y,x+w,y+40,0xFF181320);g.text(font,label,x+10,y+8,0xFF777080,false);g.text(font,value,x+10,y+23,color,true);}
}

