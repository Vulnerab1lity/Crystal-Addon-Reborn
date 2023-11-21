package com.crystaldevs.crystal;

import com.crystaldevs.crystal.gui.CrystalHud;
import com.crystaldevs.crystal.modules.misc.*;
import com.crystaldevs.crystal.modules.crash.*;
import com.crystaldevs.crystal.modules.dupe.*;
import com.crystaldevs.crystal.modules.movement.*;
import com.crystaldevs.crystal.modules.player.*;
import com.crystaldevs.crystal.modules.render.*;
import com.crystaldevs.crystal.modules.world.*;
import com.crystaldevs.crystal.commands.*;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.item.Items;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URL;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;



public class CrystalAddon extends MeteorAddon {
    /*             FABRIC               */
    public static final String MOD_ID = "crystal";
    public static final String NAME;
    public static final ModMetadata MOD_META;
    public static final Logger LOG = LogManager.getLogger("Crystal");

    static {
        MOD_META = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();
        NAME = MOD_META.getName();
        String versionString = MOD_META.getVersion().getFriendlyString();
        if (versionString.contains("-")) versionString = versionString.split("-")[0];
        if (versionString.equals("${version}")) versionString = "0.0.0.0";
    }
    /*----------------------------------*/


    /*           CATEGORIES             */
    public static final ThreadLocal<Category> CRYSTAL_DUPE_CATEGORY = ThreadLocal.withInitial(() -> new Category("Dupes", Items.BARRIER.getDefaultStack()));
    public static final ThreadLocal<Category> CRYSTAL_CRASH_CATEGORY = ThreadLocal.withInitial(() -> new Category("Crash", Items.BARRIER.getDefaultStack()));
    /*----------------------------------*/

    /*            HUDGROUPS             */
    public static final HudGroup HUD_GROUP =  new HudGroup("Crystal");
    /*----------------------------------*/

    /*            VARIABLES             */
    String SkiddedModID = "zeigeist-addon";

    public static final List<BiConsumer<GuiTheme, WContainer>> myWidgets = new ArrayList<>();

    public static final ThreadLocal<CrystalAddon> INSTANCE = new ThreadLocal<CrystalAddon>();
    /*----------------------------------*/

    @Override
    public void onInitialize() {
        INSTANCE.set(this);

        if (FabricLoader.getInstance().isModLoaded(SkiddedModID)) {
            throw new RuntimeException("Why the fuck are you using " + SkiddedModID + ", it is the most skidded addon. The developer skidded from Tokyo, Crystal, and Allah. It also has a lot of obfuscated code with base64 so you should probably delete it.");
        }


        LOG.info("Initializing Crystal...");

        Modules modules = Modules.get();

        /*           DUPE             */
        modules.add(new DupeModule());
        modules.add(new XsDupe());
        modules.add(new ItemFrameDupe());

        LOG.info("Added dupe modules.");
        /*----------------------------*/

        /*          MOVEMENT          */
        modules.add(new ElytraFix());
        modules.add(new Boost());
        modules.add(new Jetpack());
        modules.add(new Glide());
        modules.add(new NoJumpCooldown());
        modules.add(new BoatPhase());
        modules.add(new BoatFling());

        LOG.info("Added movement modules.");
        /*----------------------------*/

        /*            World           */
        modules.add(new RandomBuild());

        LOG.info("Added building modules.");
        /*----------------------------*/

        /*           RENDER           */
        modules.add(new BoosieFade());

        LOG.info("Added render modules.");
        /*----------------------------*/

        /*            MISC            */
        modules.add(new ServerOpNuke());
        modules.add(new FakeHacker());
        modules.add(new SecretClose());
        modules.add(new MassPayout());
        modules.add(new ExtraKnockback());
        modules.add(new PingSpoofer());
        modules.add(new AntiCrash());
        modules.add(new Invincibility());
        modules.add(new MassMessage());
        modules.add(new AntiCoordinateLeak());
        modules.add(new PacketChoker());

        LOG.info("Added misc modules.");
        /*----------------------------*/

        /*           PLAYER           */
        modules.add(new HeadRoll());
        modules.add(new LeftHanded());
        modules.add(new GhostMode());
        modules.add(new VehicleOneHit());

        LOG.info("Added player modules.");
        /*----------------------------*/

        /*           CRASH            */
        modules.add(new PositionCrash());
        modules.add(new StorageCrash());
        modules.add(new LecternCrash());
        modules.add(new VehicleCrash());
        modules.add(new BookCrash());
        modules.add(new NullExceptionCrash());
        modules.add(new MovementCrash());
        modules.add(new ExceptionCrash());
        modules.add(new EntityCrash());
        modules.add(new CraftingCrash());
        modules.add(new CreativeCrash());
        modules.add(new AdvancedCrash());
        modules.add(new SignCrash());
        modules.add(new LagMessage());
        modules.add(new PacketFlooder());
        modules.add(new JigSawCrash());
        modules.add(new TradeCrash());
        modules.add(new WorldBorderCrash());
        modules.add(new UDPFlood());
        modules.add(new BungeeCrash());
        modules.add(new SwingCrash());
        modules.add(new AutoLagSign());
        modules.add(new ArmorStandCrash());

        LOG.info("Added crash modules.");
        /*----------------------------*/

        /*         COMMANDS           */
        Commands.add(new CenterCommand());
        Commands.add(new ItemCommand());
        Commands.add(new TeleportCommand());
        Commands.add(new DesyncCommand());
        Commands.add(new UUIDCommand());
        Commands.add(new ClearChatCommand());
        Commands.add(new Coordinates());
        Commands.add(new Disconnect());
        Commands.add(new DupeReal());
        Commands.add(new GiveCommand());
        Commands.add(new LocateCommand());
        Commands.add(new PanicCommand());
        Commands.add(new LatencyCommand());
        Commands.add(new SaveSkinCommand());
        Commands.add(new SeedCommand());
        Commands.add(new ServerCommand());
        Commands.add(new SetBlockCommand());
        Commands.add(new SetVelocityCommand());
        Commands.add(new Title());
        Commands.add(new ClearCommand());
        Commands.add(new DropAllCommand());
        Commands.add(new IPLookupCommand());
        Commands.add(new DNSLookupCommand());
        Commands.add(new IPBlacklistCommand());
        Commands.add(new PingCommand());
        Commands.add(new SubnetCalculatorCommand());
        Commands.add(new SpoofNameCommand());
        Commands.add(new SpoofServerBrandCommand());
        Commands.add(new SpoofUUIDCommand());
        Commands.add(new WebhookDeleteCommand());
        Commands.add(new WebhookSendCommand());
        Commands.add(new NetProxyCommand());
        Commands.add(new NetProxyDisconnectCommand());
        Commands.add(new TwoBTwoTSeenCommand());
        Commands.add(new TwoBTwoTStatsCommand());
        Commands.add(new GeneratePasswordCommand());
        Commands.add(new CheckSSLCommand());
        Commands.add(new TracerouteCommand());
        LOG.info("Added commands.");
        /*----------------------------*/

        /*            HUD             */
        Hud.get().register(CrystalHud.INFO);
        LOG.info("Added HUD.");
        /*----------------------------*/

        /*           Config           */
        Config.get().customWindowTitle.set(true);
        Config.get().customWindowTitleText.set("Crystal Addon by SpecKeef");
        LOG.info("Applied custom configs.");
        /*----------------------------*/
        LOG.info("Crystal modules loaded");
        ChatUtils.info("Crystal modules loaded");
        MeteorClient.EVENT_BUS.subscribe(this);
    }


    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CRYSTAL_DUPE_CATEGORY.get());
        Modules.registerCategory(CRYSTAL_CRASH_CATEGORY.get());
    }

    @Override
    public String getWebsite() {
        return "https://crystaladdon.vercel.app";
    }

    @Override
    public String getPackage() {
        return "com.crystaldevs.crystal";
    }
}
