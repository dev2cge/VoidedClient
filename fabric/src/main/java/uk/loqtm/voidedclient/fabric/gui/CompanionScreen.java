package uk.loqtm.voidedclient.fabric.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import uk.loqtm.voidedclient.protocol.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * Voided Network Companion 2.0.
 *
 * A consistent two-row navigation shell with fixed footer controls and
 * matching Overview / Daily Contract sub-tabs for Explore, Nether and End.
 * All gameplay data remains server-authoritative.
 */
public final class CompanionScreen extends Screen {
    private static final int PANEL_W=560, PANEL_H=340, SERVER_PAGE_SIZE=6;
    private static final int NAV_Y=38, NAV_ROW_H=24, CONTENT_Y=112, FOOTER_Y=306;

    private enum View {
        HOME, MARKET, MISSIONS, SERVICES, RPG, EXPLORE, EXPLORE_CONTRACT,
        NETHER, NETHER_CONTRACT, END, END_CONTRACT,
        SERVERS, LEADERBOARD, ACCOUNTS, ENDGAME
    }

    private final View view;
    private final Object state;
    private final Consumer<byte[]> send;
    private final int serverPage;

    private CompanionScreen(View view,Object state,Consumer<byte[]> send,int serverPage){
        super(Component.literal("Voided Network Companion"));
        this.view=view;this.state=state;this.send=send;this.serverPage=Math.max(0,serverPage);
    }

    public static CompanionScreen home(CompanionHomeState s,Consumer<byte[]> send){return new CompanionScreen(View.HOME,s,send,0);}
    public static CompanionScreen market(MarketCompanionState s,Consumer<byte[]> send){return new CompanionScreen(View.MARKET,s,send,0);}
    public static CompanionScreen missions(MissionsState s,Consumer<byte[]> send){return new CompanionScreen(View.MISSIONS,s,send,0);}
    public static CompanionScreen services(ServicesState s,Consumer<byte[]> send){return new CompanionScreen(View.SERVICES,s,send,0);}
    public static CompanionScreen leaderboard(LeaderboardState s,Consumer<byte[]> send){return new CompanionScreen(View.LEADERBOARD,s,send,0);}
    public static CompanionScreen accounts(LinkedAccountsState s,Consumer<byte[]> send){return new CompanionScreen(View.ACCOUNTS,s,send,0);}
    public static CompanionScreen servers(ServerListState s,Consumer<byte[]> send){return new CompanionScreen(View.SERVERS,s,send,0);}
    public static CompanionScreen explore(ExplorationJournalState s,Consumer<byte[]> send){return new CompanionScreen(View.EXPLORE,s,send,0);}
    public static CompanionScreen exploreContract(ExplorationContractState s,Consumer<byte[]> send){return new CompanionScreen(View.EXPLORE_CONTRACT,s,send,0);}
    public static CompanionScreen nether(NetherCompanionState s,Consumer<byte[]> send){return new CompanionScreen(View.NETHER,s,send,0);}
    public static CompanionScreen end(EndCompanionState s,Consumer<byte[]> send){return new CompanionScreen(View.END,s,send,0);}
    public static CompanionScreen rpg(RpgStatsState s,Consumer<byte[]> send){return new CompanionScreen(View.RPG,s,send,0);}
    public static CompanionScreen endgame(EndgameCompanionState s,Consumer<byte[]> send){return new CompanionScreen(View.ENDGAME,s,send,0);}

    @Override protected void init(){
        int w=Math.min(PANEL_W,width-18),left=(width-w)/2,top=Math.max(6,(height-PANEL_H)/2);

        // Consistent 5x2 primary navigation.
        nav(left+14,top+NAV_Y,100,"Home",View.HOME,()->request(VoidedProtocol.ACTION_COMPANION_HOME));
        nav(left+122,top+NAV_Y,100,"Market",View.MARKET,()->request(VoidedProtocol.ACTION_MARKET_COMPANION));
        nav(left+230,top+NAV_Y,100,"Missions",View.MISSIONS,()->request(VoidedProtocol.ACTION_MISSIONS_REQUEST));
        nav(left+338,top+NAV_Y,100,"RPG",View.RPG,()->{if(ServerContextState.isRpgEnabled())request(VoidedProtocol.ACTION_RPG_STATS);});
        nav(left+446,top+NAV_Y,100,"Explore",View.EXPLORE,()->{if(ServerContextState.isGameplayEnabled())request(VoidedProtocol.ACTION_EXPLORATION_JOURNAL);});

        nav(left+14,top+NAV_Y+28,100,"Nether",View.NETHER,()->{if(ServerContextState.isGameplayEnabled())request(VoidedProtocol.ACTION_NETHER_COMPANION);});
        nav(left+122,top+NAV_Y+28,100,"End",View.END,()->{if(ServerContextState.isGameplayEnabled())request(VoidedProtocol.ACTION_END_COMPANION);});
        nav(left+230,top+NAV_Y+28,100,"Servers",View.SERVERS,()->request(VoidedProtocol.ACTION_SERVER_LIST));
        nav(left+338,top+NAV_Y+28,100,"Services",View.SERVICES,()->request(VoidedProtocol.ACTION_SERVICES_REQUEST));
        nav(left+446,top+NAV_Y+28,100,"Accounts",View.ACCOUNTS,()->request(VoidedProtocol.ACTION_ACCOUNTS_REQUEST));

        if(view==View.EXPLORE||view==View.EXPLORE_CONTRACT)initExplore(left,top,w);
        else if(view==View.NETHER||view==View.NETHER_CONTRACT)initNether(left,top,w);
        else if(view==View.END||view==View.END_CONTRACT)initEnd(left,top,w);
        else if(view==View.HOME)initHome(left,top,w);
        else if(view==View.MARKET)initMarket(left,top,w);
        else if(view==View.MISSIONS)initMissions(left,top,w);
        else if(view==View.SERVICES)initServices(left,top,w);
        else if(view==View.RPG)initRpg(left,top,w);
        else if(view==View.SERVERS)initServers(left,top,w);
        else if(view==View.LEADERBOARD)initLeaderboard(left,top,w);
        else if(view==View.ACCOUNTS)initAccounts(left,top,w);
        else if(view==View.ENDGAME)initEndgame(left,top,w);

        addRenderableWidget(Button.builder(Component.literal("Close"),b->minecraft.gui.setScreen(null))
                .bounds(left+w-78,top+FOOTER_Y,64,22).build());
    }

    private void initHome(int left,int top,int w){
        addRenderableWidget(Button.builder(Component.literal("Leaders"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,"balance","1"))
                .bounds(left+18,top+FOOTER_Y,72,22).build());
        addRenderableWidget(Button.builder(Component.literal("Endgame"),b->request(VoidedProtocol.ACTION_ENDGAME_COMPANION))
                .bounds(left+94,top+FOOTER_Y,78,22).build());
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_COMPANION_HOME))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initMarket(int left,int top,int w){
        addRenderableWidget(Button.builder(Component.literal("Open Market"),b->request(VoidedProtocol.ACTION_MARKET_OPEN))
                .bounds(left+18,top+FOOTER_Y,100,22).build());
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_MARKET_COMPANION))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initMissions(int left,int top,int w){
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_MISSIONS_REQUEST))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initServices(int left,int top,int w){
        ServicesState s=(ServicesState)state;
        Button ec=Button.builder(Component.literal(s.enderChestUnlocked()?"EC Unlocked":"Unlock EC"),b->request(VoidedProtocol.ACTION_SERVICES_UNLOCK_EC))
                .bounds(left+18,top+FOOTER_Y,90,22).build();
        ec.active=!s.enderChestUnlocked();
        Button pv=Button.builder(Component.literal(s.personalVaultUnlocked()?"PV Unlocked":"Unlock PV"),b->request(VoidedProtocol.ACTION_SERVICES_UNLOCK_PV))
                .bounds(left+112,top+FOOTER_Y,90,22).build();
        pv.active=!s.personalVaultUnlocked()&&s.vaultReady();
        addRenderableWidget(ec);addRenderableWidget(pv);
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_SERVICES_REQUEST))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initExplore(int left,int top,int w){
        subtab(left+18,top+96,96,"Overview",view==View.EXPLORE,()->{
            if(view==View.EXPLORE_CONTRACT)request(VoidedProtocol.ACTION_EXPLORATION_JOURNAL);
        });
        subtab(left+118,top+96,112,"Daily Contract",view==View.EXPLORE_CONTRACT,()->{
            if(view!=View.EXPLORE_CONTRACT)request(VoidedProtocol.ACTION_EXPLORATION_CONTRACT);
        });
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(view==View.EXPLORE_CONTRACT?VoidedProtocol.ACTION_EXPLORATION_CONTRACT:VoidedProtocol.ACTION_EXPLORATION_JOURNAL))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initNether(int left,int top,int w){
        subtab(left+18,top+96,96,"Overview",view==View.NETHER,()->{
            if(view==View.NETHER_CONTRACT&&state instanceof NetherCompanionState)openLocal(View.NETHER);
        });
        subtab(left+118,top+96,112,"Daily Contract",view==View.NETHER_CONTRACT,()->{
            if(view!=View.NETHER_CONTRACT&&state instanceof NetherCompanionState)openLocal(View.NETHER_CONTRACT);
        });
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_NETHER_COMPANION))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initEnd(int left,int top,int w){
        subtab(left+18,top+96,96,"Overview",view==View.END,()->{
            if(view==View.END_CONTRACT&&state instanceof EndCompanionState)openLocal(View.END);
        });
        subtab(left+118,top+96,112,"Daily Contract",view==View.END_CONTRACT,()->{
            if(view!=View.END_CONTRACT&&state instanceof EndCompanionState)openLocal(View.END_CONTRACT);
        });
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_END_COMPANION))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initRpg(int left,int top,int w){
        RpgStatsState s=(RpgStatsState)state;
        String[] keys={"strength","defence","vitality","agility","intelligence"};
        int y=top+146;
        for(String key:keys){
            boolean enabled=s.points()>0&&s.stat(key)<s.maxPointsPerStat();
            Button one=Button.builder(Component.literal("+1"),b->request(VoidedProtocol.ACTION_RPG_STAT_SPEND,key,"1")).bounds(left+w-144,y,34,20).build();
            Button five=Button.builder(Component.literal("+5"),b->request(VoidedProtocol.ACTION_RPG_STAT_SPEND,key,"5")).bounds(left+w-106,y,34,20).build();
            Button max=Button.builder(Component.literal("MAX"),b->request(VoidedProtocol.ACTION_RPG_STAT_SPEND,key,"max")).bounds(left+w-68,y,44,20).build();
            one.active=enabled;five.active=enabled;max.active=enabled;
            addRenderableWidget(one);addRenderableWidget(five);addRenderableWidget(max);y+=25;
        }
        Button respec=Button.builder(Component.literal("Respec"),b->request(VoidedProtocol.ACTION_RPG_STAT_RESPEC)).bounds(left+18,top+FOOTER_Y,72,22).build();
        respec.active=s.spentPoints()>0&&s.respecCooldownSeconds()==0L;
        addRenderableWidget(respec);
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_RPG_STATS))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initLeaderboard(int left,int top,int w){
        LeaderboardState s=(LeaderboardState)state;
        addRenderableWidget(Button.builder(Component.literal("Balance"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,"balance","1")).bounds(left+18,top+108,88,20).build());
        addRenderableWidget(Button.builder(Component.literal("Playtime"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,"playtime","1")).bounds(left+110,top+108,88,20).build());
        addRenderableWidget(Button.builder(Component.literal("RPG Level"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,"rpg","1")).bounds(left+202,top+108,88,20).build());
        Button prev=Button.builder(Component.literal("<"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,s.category(),Integer.toString(Math.max(1,s.page()-1)))).bounds(left+18,top+FOOTER_Y,30,22).build();
        Button next=Button.builder(Component.literal(">"),b->request(VoidedProtocol.ACTION_LEADERBOARD_REQUEST,s.category(),Integer.toString(Math.min(s.pages(),s.page()+1)))).bounds(left+52,top+FOOTER_Y,30,22).build();
        prev.active=s.page()>1;next.active=s.page()<s.pages();addRenderableWidget(prev);addRenderableWidget(next);
    }

    private void initAccounts(int left,int top,int w){
        LinkedAccountsState s=(LinkedAccountsState)state;
        addRenderableWidget(Button.builder(Component.literal(s.discordLinked()?"Unlink Discord":"Create Link Code"),
                b->request(s.discordLinked()?VoidedProtocol.ACTION_ACCOUNTS_UNLINK:VoidedProtocol.ACTION_ACCOUNTS_LINK))
                .bounds(left+18,top+FOOTER_Y,124,22).build());
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_ACCOUNTS_REQUEST))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initEndgame(int left,int top,int w){
        addRenderableWidget(Button.builder(Component.literal("Back Home"),b->request(VoidedProtocol.ACTION_COMPANION_HOME))
                .bounds(left+18,top+FOOTER_Y,86,22).build());
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_ENDGAME_COMPANION))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void initServers(int left,int top,int w){
        ServerListState s=(ServerListState)state;
        List<ServerListState.Entry> entries=s.entries();
        int pages=Math.max(1,(entries.size()+SERVER_PAGE_SIZE-1)/SERVER_PAGE_SIZE),page=Math.min(serverPage,pages-1);
        int from=page*SERVER_PAGE_SIZE,to=Math.min(entries.size(),from+SERVER_PAGE_SIZE),y=top+120;
        for(int i=from;i<to;i++){
            ServerListState.Entry entry=entries.get(i);
            String text=entry.name()+"  •  "+(entry.available()?entry.online()+" online":"OFFLINE")+(entry.current()?"  •  CURRENT":"");
            Button button=Button.builder(Component.literal(text),b->{if(!entry.current()&&entry.available())request(VoidedProtocol.ACTION_SERVER_SWITCH,entry.id());})
                    .bounds(left+18,y,w-36,22).build();
            button.active=!entry.current()&&entry.available();addRenderableWidget(button);y+=26;
        }
        if(pages>1){
            Button prev=Button.builder(Component.literal("<"),b->openServerPage(page-1)).bounds(left+18,top+FOOTER_Y,30,22).build();
            Button next=Button.builder(Component.literal(">"),b->openServerPage(page+1)).bounds(left+52,top+FOOTER_Y,30,22).build();
            prev.active=page>0;next.active=page+1<pages;addRenderableWidget(prev);addRenderableWidget(next);
        }
        addRenderableWidget(Button.builder(Component.literal("Refresh"),b->request(VoidedProtocol.ACTION_SERVER_LIST))
                .bounds(left+w-162,top+FOOTER_Y,78,22).build());
    }

    private void nav(int x,int y,int w,String label,View base,Runnable action){
        Button b=Button.builder(Component.literal(label),btn->action.run()).bounds(x,y,w,22).build();
        b.active=!isPrimaryActive(base);addRenderableWidget(b);
    }

    private void subtab(int x,int y,int w,String label,boolean active,Runnable action){
        Button b=Button.builder(Component.literal(label),btn->action.run()).bounds(x,y,w,20).build();
        b.active=!active;addRenderableWidget(b);
    }

    private boolean isPrimaryActive(View base){
        if(base==View.EXPLORE)return view==View.EXPLORE||view==View.EXPLORE_CONTRACT;
        if(base==View.NETHER)return view==View.NETHER||view==View.NETHER_CONTRACT;
        if(base==View.END)return view==View.END||view==View.END_CONTRACT;
        return view==base;
    }

    private void openLocal(View target){
        if(minecraft!=null)minecraft.gui.setScreen(new CompanionScreen(target,state,send,serverPage));
    }
    private void openServerPage(int page){if(minecraft!=null)minecraft.gui.setScreen(new CompanionScreen(View.SERVERS,state,send,Math.max(0,page)));}

    private void request(String action){send.accept(VoidedProtocol.action(action));}
    private void request(String action,String arg){send.accept(VoidedProtocol.action(action,arg));}
    private void request(String action,String a,String b){send.accept(VoidedProtocol.action(action,a,b));}

    @Override public void extractRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta){
        int w=Math.min(PANEL_W,width-18),left=(width-w)/2,top=Math.max(6,(height-PANEL_H)/2),right=left+w;
        g.fill(left-1,top-1,right+1,top+PANEL_H+1,0xFF6D28D9);
        g.fill(left,top,right,top+PANEL_H,0xF20C0912);
        g.fill(left,top,right,top+4,0xFF8B5CF6);
        g.fill(left+10,top+32,right-10,top+94,0xFF15101F);
        g.fill(left+12,top+102,right-12,top+300,0x90130F1B);
        g.text(font,"VOIDED NETWORK",left+16,top+14,0xFFF4EEFF,true);
        g.text(font,sectionTitle(),right-16-font.width(sectionTitle()),top+14,0xFFB794F6,true);

        if(view==View.HOME)renderHome(g,left,top,w);
        else if(view==View.MARKET)renderMarket(g,left,top,w);
        else if(view==View.MISSIONS)renderMissions(g,left,top,w);
        else if(view==View.SERVICES)renderServices(g,left,top,w);
        else if(view==View.RPG)renderRpg(g,left,top,w);
        else if(view==View.EXPLORE)renderExplore(g,left,top,w);
        else if(view==View.EXPLORE_CONTRACT)renderExploreContract(g,left,top,w);
        else if(view==View.NETHER)renderNether(g,left,top,w);
        else if(view==View.NETHER_CONTRACT)renderNetherContract(g,left,top,w);
        else if(view==View.END)renderEnd(g,left,top,w);
        else if(view==View.END_CONTRACT)renderEndContract(g,left,top,w);
        else if(view==View.SERVERS)renderServers(g,left,top,w);
        else if(view==View.LEADERBOARD)renderLeaderboard(g,left,top,w);
        else if(view==View.ACCOUNTS)renderAccounts(g,left,top,w);
        else if(view==View.ENDGAME)renderEndgame(g,left,top,w);

        super.extractRenderState(g,mouseX,mouseY,delta);
    }

    private String sectionTitle(){
        return switch(view){
            case MARKET->"MARKET";
            case MISSIONS->"MISSIONS";
            case SERVICES->"SERVICES";
            case RPG->"RPG";
            case EXPLORE,EXPLORE_CONTRACT->"EXPLORATION";
            case NETHER,NETHER_CONTRACT->"NETHER";
            case END,END_CONTRACT->"END";
            case SERVERS->"SERVERS";
            case LEADERBOARD->"LEADERBOARDS";
            case ACCOUNTS->"ACCOUNTS";
            case ENDGAME->"ENDGAME";
            default->"COMPANION";
        };
    }

    private int bodyTop(){return (view==View.EXPLORE||view==View.EXPLORE_CONTRACT||view==View.NETHER||view==View.NETHER_CONTRACT||view==View.END||view==View.END_CONTRACT)?128:112;}

    private void renderHome(GuiGraphicsExtractor g,int left,int top,int w){
        CompanionHomeState s=(CompanionHomeState)state;
        int y=top+114;
        g.text(font,"Welcome back, "+s.player(),left+20,y,0xFFFFFFFF,true);
        g.text(font,s.server()+"  •  "+s.online()+" online across VoidedNetwork",left+20,y+17,0xFF9F97AC,false);
        statCard(g,left+20,y+42,164,"BALANCE",s.balance(),0xFF86EFAC);
        statCard(g,left+198,y+42,164,"RPG LEVEL",Integer.toString(s.rpgLevel()),0xFFC4B5FD);
        statCard(g,left+376,y+42,164,"DISCORD",s.discordLinked()?"LINKED":"NOT LINKED",s.discordLinked()?0xFF86EFAC:0xFFFBBF24);
        g.text(font,"Use the navigation above for progression, missions, economy and network tools.",left+20,top+274,0xFF777080,false);
    }

    private void renderMarket(GuiGraphicsExtractor g,int left,int top,int w){
        MarketCompanionState s=(MarketCompanionState)state;
        int y=top+112;
        statCard(g,left+20,y,164,"NETWORK LISTINGS",Integer.toString(s.networkListings()),0xFFC4B5FD);
        statCard(g,left+198,y,164,"YOUR LISTINGS",Integer.toString(s.ownListings()),0xFFFDE68A);
        statCard(g,left+376,y,164,"BUY / SELL",s.buyListings()+" / "+s.sellListings(),0xFF86EFAC);
        statCard(g,left+20,y+50,164,"SHOP INCOME",s.received(),0xFF86EFAC);
        statCard(g,left+198,y+50,164,"SHOP SPEND",s.spent(),0xFFFFB86B);
        statCard(g,left+376,y+50,164,"MARKET FEES",s.fees(),0xFFF0ABFC);

        g.text(font,"YOUR ACTIVE LISTINGS",left+20,y+105,0xFFE9D5FF,true);
        int rowY=y+122,shown=0;
        for(MarketCompanionState.Listing row:s.listings()){
            if(shown++>=5)break;
            g.fill(left+20,rowY-3,left+w-20,rowY+15,0xFF171221);
            String leftText=row.type()+"  "+row.item()+" x"+row.amount();
            String rightText=row.price()+"  •  "+row.server();
            g.text(font,truncate(leftText,34),left+28,rowY,0xFFFFFFFF,false);
            g.text(font,truncate(rightText,28),left+w-28-font.width(truncate(rightText,28)),rowY,0xFFAAA2B5,false);
            rowY+=20;
        }
        if(s.listings().isEmpty())g.text(font,"You do not currently own any player shops.",left+28,rowY,0xFF777080,false);
        g.text(font,"Recent market transactions: "+s.transactions(),left+20,top+286,0xFF777080,false);
    }

    private void renderServices(GuiGraphicsExtractor g,int left,int top,int w){
        ServicesState s=(ServicesState)state;
        int y=top+112;
        statCard(g,left+20,y,250,"ENDER CHEST ACCESS",
                s.enderChestUnlocked()?"UNLOCKED":"LOCKED • "+s.enderChestCost(),
                s.enderChestUnlocked()?0xFF86EFAC:0xFFFBBF24);
        statCard(g,left+290,y,250,"PERSONAL VAULT",
                s.personalVaultUnlocked()?"UNLOCKED":(s.vaultReady()?"READY • "+s.personalVaultCost():"LOCKED • "+s.bossesDefeated()+"/"+s.bossesRequired()+" bosses"),
                s.personalVaultUnlocked()?0xFF86EFAC:s.vaultReady()?0xFFFDE68A:0xFFFF8A80);

        g.text(font,"MAIN BOSS PROGRESSION",left+20,y+56,0xFFE9D5FF,true);
        if(!s.nextBoss().isEmpty())g.text(font,"Next boss: "+s.nextBoss(),left+w-20-font.width("Next boss: "+s.nextBoss()),y+56,0xFFFDE68A,false);

        int rowY=y+78;
        int n=1;
        for(ServicesState.Boss boss:s.bosses()){
            g.fill(left+20,rowY-3,left+w-20,rowY+18,0xFF171221);
            int color=boss.defeated()?0xFF86EFAC:boss.available()?0xFFFDE68A:0xFF777080;
            String marker=boss.defeated()?"✓":boss.available()?"→":"✗";
            g.text(font,n+". "+marker+" "+boss.name(),left+30,rowY,color,boss.available()||boss.defeated());
            String state=boss.defeated()?"DEFEATED":boss.available()?"CURRENT":"LOCKED";
            g.text(font,state,left+w-30-font.width(state),rowY,color,false);
            rowY+=23;n++;
        }
    }

    private void renderMissions(GuiGraphicsExtractor g,int left,int top,int w){
        MissionsState s=(MissionsState)state;
        int y=top+112;
        g.text(font,"DAILY MISSIONS",left+20,y,0xFFE9D5FF,true);
        g.text(font,"Reset: "+s.date()+"  •  Earned today: "+s.earned(),left+20,y+17,0xFF9F97AC,false);
        int rowY=y+40;
        for(MissionsState.Entry m:s.entries()){
            g.fill(left+20,rowY-4,left+w-20,rowY+30,0xFF171221);
            g.text(font,(m.complete()?"✓ ":"")+m.name(),left+30,rowY,m.complete()?0xFF86EFAC:0xFFFFFFFF,true);
            String progress=Math.min(m.progress(),m.target())+"/"+m.target()+"  •  "+m.reward();
            g.text(font,progress,left+w-30-font.width(progress),rowY,0xFFC4B5FD,false);
            g.text(font,truncate(m.description(),62),left+30,rowY+14,0xFF8F879A,false);
            rowY+=39;
        }
    }

    private void renderExplore(GuiGraphicsExtractor g,int left,int top,int w){
        ExplorationJournalState s=(ExplorationJournalState)state;int y=top+132;
        statCard(g,left+20,y,164,"TOTAL KILLS",Long.toString(s.totalKills()),0xFF86EFAC);
        statCard(g,left+198,y,164,"DISCOVERED",s.discoveredCount()+"/"+s.mobs().size(),0xFFC4B5FD);
        statCard(g,left+376,y,164,"RARITY TIER",Integer.toString(s.highestRarityTier()),0xFFFDE68A);
        int rowY=y+58,shown=0;
        for(ExplorationJournalState.MobEntry mob:s.mobs()){
            if(shown++>=6)break;
            String name=mob.discovered()?mob.name():"Unknown creature";
            String detail=mob.discovered()?mob.kills()+" defeated":"Not discovered";
            g.text(font,name,left+24,rowY,mob.discovered()?0xFFFFFFFF:0xFF777080,mob.discovered());
            g.text(font,detail,left+w-24-font.width(detail),rowY,0xFF8F879A,false);rowY+=16;
        }
    }

    private void renderExploreContract(GuiGraphicsExtractor g,int left,int top,int w){
        ExplorationContractState s=(ExplorationContractState)state;
        renderContract(g,left,top,w,s.name(),s.description(),s.progress(),s.target(),s.complete(),
                s.rewardXp()+" RPG XP",""+s.rewardAmount()+"x "+s.rewardItem(),"Resets daily • "+s.date());
    }

    private void renderNether(GuiGraphicsExtractor g,int left,int top,int w){
        NetherCompanionState s=(NetherCompanionState)state;int y=top+132;
        String xp=s.needed()<=0?"MAX":s.xp()+"/"+s.needed();
        statCard(g,left+20,y,164,"ATTUNEMENT","Lv "+s.level()+" • "+xp,0xFFFFB86B);
        statCard(g,left+198,y,164,"KILLS",Long.toString(s.kills()),0xFFFFB86B);
        statCard(g,left+376,y,164,"LANDMARKS",s.landmarks()+"/"+s.landmarkTotal(),0xFFC4B5FD);
        statCard(g,left+20,y+50,164,"VARIANT KILLS",Long.toString(s.variantKills()),0xFFFDE68A);
        statCard(g,left+198,y+50,164,"DISCOVERIES",s.variantDiscoveries()+"/4",0xFFFDE68A);
        statCard(g,left+376,y+50,164,"ORES",Long.toString(s.ores()),0xFF86EFAC);
        String boss="Infernal Sovereign • "+humanize(s.bossState());
        if(s.bossRespawnSeconds()>0)boss+=" • "+duration(s.bossRespawnSeconds());
        infoBox(g,left+20,y+110,w-40,"BOSS",boss,0xFFFF8A80);
    }

    private void renderNetherContract(GuiGraphicsExtractor g,int left,int top,int w){
        NetherCompanionState s=(NetherCompanionState)state;
        renderContract(g,left,top,w,humanize(s.contract()),"Complete today's Nether objective.",
                s.contractProgress(),s.contractTarget(),s.contractComplete(),
                "Nether progression","Gameplay money / progression rewards","Resets daily");
    }

    private void renderEnd(GuiGraphicsExtractor g,int left,int top,int w){
        EndCompanionState s=(EndCompanionState)state;int y=top+132;
        String xp=s.needed()<=0?"MAX":s.xp()+"/"+s.needed();
        statCard(g,left+20,y,164,"ATTUNEMENT","Lv "+s.level()+" • "+xp,0xFFD8B4FE);
        statCard(g,left+198,y,164,"KILLS",Long.toString(s.kills()),0xFFD8B4FE);
        statCard(g,left+376,y,164,"LANDMARKS",s.landmarkTotal()<=0?"Not scanned":s.landmarks()+"/"+s.landmarkTotal(),0xFFC4B5FD);
        statCard(g,left+20,y+50,164,"VARIANT KILLS",Long.toString(s.variantKills()),0xFFF0ABFC);
        statCard(g,left+198,y+50,164,"DISCOVERIES",s.variantDiscoveries()+"/3",0xFFF0ABFC);
        statCard(g,left+376,y+50,164,"NODES",Long.toString(s.nodes()),0xFF86EFAC);
        String boss="Void Arbiter • "+humanize(s.bossState());
        if(s.bossRespawnSeconds()>0)boss+=" • "+duration(s.bossRespawnSeconds());
        infoBox(g,left+20,y+110,w-40,"BOSS",boss,0xFFE879F9);
        String event="Activity: "+humanize(s.eventState())+(s.eventRemaining()>0?" • "+s.eventRemaining()+" enemies remain":"");
        g.text(font,event,left+28,y+157,0xFF8F879A,false);
    }

    private void renderEndContract(GuiGraphicsExtractor g,int left,int top,int w){
        EndCompanionState s=(EndCompanionState)state;
        renderContract(g,left,top,w,humanize(s.contract()),"Complete today's End objective.",
                s.contractProgress(),s.contractTarget(),s.contractComplete(),
                "End progression","Gameplay money / progression rewards","Resets daily");
    }

    private void renderRpg(GuiGraphicsExtractor g,int left,int top,int w){
        RpgStatsState s=(RpgStatsState)state;
        int y=top+112;
        g.text(font,"RPG BUILD • "+s.buildName(),left+20,y,0xFFE9D5FF,true);
        String level="Level "+s.level()+"  •  "+s.xpIntoLevel()+"/"+s.xpNeeded()+" XP  •  "+s.points()+" points available";
        g.text(font,level,left+20,y+17,0xFF9F97AC,false);
        String[] names={"Strength","Defence","Vitality","Agility","Intelligence"};
        String[] keys={"strength","defence","vitality","agility","intelligence"};
        int rowY=y+42;
        for(int i=0;i<keys.length;i++){
            g.fill(left+20,rowY-3,left+w-20,rowY+20,0xFF171221);
            g.text(font,names[i],left+30,rowY,0xFFFFFFFF,true);
            String value=s.totalStat(keys[i])+"  ("+s.stat(keys[i])+" base";
            if(s.equipment(keys[i])!=0)value+=" +"+s.equipment(keys[i])+" gear";
            if(s.temporary(keys[i])!=0)value+=" +"+s.temporary(keys[i])+" temp";
            value+=")";
            g.text(font,value,left+132,rowY,0xFFC4B5FD,false);rowY+=25;
        }
    }

    private void renderServers(GuiGraphicsExtractor g,int left,int top,int w){
        ServerListState s=(ServerListState)state;
        int pages=Math.max(1,(s.entries().size()+SERVER_PAGE_SIZE-1)/SERVER_PAGE_SIZE),page=Math.min(serverPage,pages-1);
        g.text(font,"SERVER BROWSER",left+20,top+112,0xFFE9D5FF,true);
        String p="Page "+(page+1)+" / "+pages;g.text(font,p,left+w-20-font.width(p),top+112,0xFF777080,false);
    }

    private void renderLeaderboard(GuiGraphicsExtractor g,int left,int top,int w){
        LeaderboardState s=(LeaderboardState)state;
        int y=top+142;
        g.text(font,s.category().toUpperCase()+" LEADERBOARD",left+20,y-8,0xFFE9D5FF,true);
        for(LeaderboardState.Entry e:s.entries()){
            int color=e.rank()<=3?0xFFFDE68A:0xFFFFFFFF;
            g.text(font,"#"+e.rank(),left+24,y,color,true);
            g.text(font,e.name(),left+76,y,0xFFFFFFFF,false);
            g.text(font,e.value(),left+w-24-font.width(e.value()),y,0xFFC4B5FD,false);y+=17;
        }
        g.text(font,"Your rank: "+(s.selfRank()==0?"Unranked":"#"+s.selfRank()),left+20,top+286,0xFF777080,false);
    }

    private void renderAccounts(GuiGraphicsExtractor g,int left,int top,int w){
        LinkedAccountsState s=(LinkedAccountsState)state;
        int y=top+116;
        infoBox(g,left+20,y,w-40,"MINECRAFT",s.minecraftName(),0xFFFFFFFF);
        infoBox(g,left+20,y+58,w-40,"DISCORD",s.discordLinked()?(s.discordName().isEmpty()?"Linked":s.discordName()):"Not linked",s.discordLinked()?0xFF86EFAC:0xFFFBBF24);
        if(!s.linkCode().isEmpty())infoBox(g,left+20,y+116,w-40,"LINK CODE",s.linkCode(),0xFFFDE68A);
        g.text(font,"Only server-issued account status is displayed; no account tokens are exposed.",left+20,top+286,0xFF777080,false);
    }

    private void renderEndgame(GuiGraphicsExtractor g,int left,int top,int w){
        EndgameCompanionState s=(EndgameCompanionState)state;int y=top+112;
        g.text(font,"ENDGAME PROGRESSION",left+20,y,0xFFE9D5FF,true);
        g.text(font,s.unlocked()?"Unlocked":"Locked • finish progression requirements",left+20,y+17,s.unlocked()?0xFF86EFAC:0xFFFF8A80,true);
        statCard(g,left+20,y+42,164,"ASCENSION",Integer.toString(s.ascension()),0xFFC4B5FD);
        statCard(g,left+198,y+42,164,"MARKS",Long.toString(s.marks()),0xFFFDE68A);
        statCard(g,left+376,y+42,164,"COMPLETION",s.completionPercent()+"%",s.completionComplete()?0xFF86EFAC:0xFFF0ABFC);
        String trial="Daily Trial • "+Math.min(s.trialProgress(),s.trialTarget())+"/"+s.trialTarget()
                +(s.trialClaimed()?" • CLAIMED":s.trialProgress()>=s.trialTarget()?" • READY":"");
        infoBox(g,left+20,y+98,w-40,"DAILY TRIAL",trial,s.trialClaimed()?0xFF86EFAC:0xFFFFFFFF);
        String clears="Standard "+s.standardClears()+" • Elite "+s.eliteClears()+" • Mythic "+s.mythicClears()+" • Best Asc "+s.bestAscension();
        g.text(font,clears,left+28,y+155,0xFFD8B4FE,false);
        String active=s.gauntletActive()?humanize(s.activeMode())+" Gauntlet • Round "+s.activeWave()+"/"+s.activeTotalWaves()+" • "+duration(s.remainingSeconds()):"No active Gauntlet";
        g.text(font,active,left+28,y+174,s.gauntletActive()?0xFFFDE68A:0xFF777080,s.gauntletActive());
    }

    private void renderContract(GuiGraphicsExtractor g,int left,int top,int w,String name,String description,int progress,int target,boolean complete,String rewardA,String rewardB,String footer){
        int y=top+132;
        g.text(font,"DAILY CONTRACT",left+20,y,0xFFE9D5FF,true);
        g.text(font,name,left+20,y+22,complete?0xFF86EFAC:0xFFFFFFFF,true);
        g.text(font,truncate(description,72),left+20,y+39,0xFF9F97AC,false);
        int barL=left+20,barR=left+w-20,barY=y+66;
        g.fill(barL,barY,barR,barY+12,0xFF2A2332);
        double ratio=complete?1D:Math.min(1D,Math.max(0D,progress/(double)Math.max(1,target)));
        if(ratio>0)g.fill(barL+2,barY+2,barL+2+(int)((barR-barL-4)*ratio),barY+10,complete?0xFF22C55E:0xFF8B5CF6);
        String prog=complete?"COMPLETE":Math.min(progress,target)+" / "+target;
        g.text(font,prog,left+(w-font.width(prog))/2,barY+18,complete?0xFF86EFAC:0xFFFFFFFF,true);
        infoBox(g,left+20,y+106,w-40,"REWARDS",rewardA+"  •  "+rewardB,0xFFFDE68A);
        g.text(font,footer,left+28,y+165,0xFF777080,false);
    }

    private void statCard(GuiGraphicsExtractor g,int x,int y,int w,String label,String value,int color){
        g.fill(x,y,x+w,y+40,0xFF181320);
        g.fill(x,y,x+3,y+40,0xFF6D28D9);
        g.text(font,label,x+12,y+8,0xFF777080,false);
        g.text(font,truncate(value,25),x+12,y+23,color,true);
    }

    private void infoBox(GuiGraphicsExtractor g,int x,int y,int w,String label,String value,int color){
        g.fill(x,y,x+w,y+46,0xFF181320);
        g.text(font,label,x+10,y+8,0xFF777080,false);
        g.text(font,truncate(value,72),x+10,y+25,color,true);
    }

    private static String truncate(String s,int max){if(s==null)return "";return s.length()<=max?s:s.substring(0,Math.max(0,max-1))+"…";}
    private static String humanize(String v){if(v==null||v.isEmpty())return "Unknown";String s=v.replace('_',' ').replace('-',' ');return Character.toUpperCase(s.charAt(0))+s.substring(1);}
    private static String duration(long seconds){long m=Math.max(0L,seconds)/60L,s=Math.max(0L,seconds)%60L;return m>0?m+"m "+s+"s":s+"s";}
}
