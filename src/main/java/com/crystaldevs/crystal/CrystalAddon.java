package com.crystaldevs.crystal;

import com.crystaldevs.crystal.commands.*;
import com.crystaldevs.crystal.gui.CrystalHud;
import com.crystaldevs.crystal.modules.crash.*;
import com.crystaldevs.crystal.modules.dupe.DupeModule;
import com.crystaldevs.crystal.modules.dupe.ItemFrameDupe;
import com.crystaldevs.crystal.modules.dupe.XsDupe;
import com.crystaldevs.crystal.modules.misc.*;
import com.crystaldevs.crystal.modules.movement.BoatFling;
import com.crystaldevs.crystal.modules.movement.BoatPhase;
import com.crystaldevs.crystal.modules.movement.NoJumpCooldown;
import com.crystaldevs.crystal.modules.player.HeadRoll;
import com.crystaldevs.crystal.modules.player.LeftHanded;
import com.crystaldevs.crystal.modules.render.BoosieFade;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrystalAddon extends MeteorAddon {
    /*             LOGGING              */
    public static final Logger LOG = LogManager.getLogger("Crystal");
    /*----------------------------------*/

    /*           CATEGORIES             */
    public static final Category CRYSTAL_DUPE_CATEGORY = new Category("Dupes", Items.BARRIER.getDefaultStack());
    public static final Category CRYSTAL_CRASH_CATEGORY = new Category("Crash", Items.BARRIER.getDefaultStack());
    /*----------------------------------*/

    /*            HUDGROUPS             */
    public static final HudGroup HUD_GROUP = new HudGroup("Crystal");
    /*----------------------------------*/

    @Override
    public void onInitialize() {
        LOG.info("Initializing Crystal...");

        Modules modules = Modules.get();
        Hud hud = Hud.get();

        /*           DUPE             */
        modules.add(new DupeModule());
        modules.add(new XsDupe());
        modules.add(new ItemFrameDupe());
        /*----------------------------*/

        /*          MOVEMENT          */
        modules.add(new NoJumpCooldown());
        modules.add(new BoatPhase());
        modules.add(new BoatFling());
        /*----------------------------*/

        /*            World           */
        // None yet
        /*----------------------------*/

        /*           RENDER           */
        modules.add(new BoosieFade());
        /*----------------------------*/

        /*            MISC            */
        modules.add(new ServerOpNuke());
        modules.add(new FakeHacker());
        modules.add(new SecretClose());
        modules.add(new MassPayout());
        modules.add(new ExtraKnockback());
        modules.add(new PingSpoofer());
        modules.add(new AntiCrash());
        modules.add(new CrackedBruteforce());
        modules.add(new Invincibility());
        modules.add(new MassMessage());
        modules.add(new AntiCoordinateLeak());
        modules.add(new PacketChoker());
        /*----------------------------*/

        /*           PLAYER           */
        modules.add(new HeadRoll());
        modules.add(new LeftHanded());
        /*----------------------------*/

        /*           CRASH            */
        modules.add(new StorageCrash());
        modules.add(new LecternCrash());
        modules.add(new BookCrash());
        modules.add(new MovementCrash());
        modules.add(new ExceptionCrash());
        modules.add(new CraftingCrash());
        modules.add(new CreativeCrash());
        modules.add(new AdvancedCrash());
        modules.add(new SignCrash());
        modules.add(new PacketFlooder());
        modules.add(new JigSawCrash());
        modules.add(new TradeCrash());
        modules.add(new UDPFlood());
        modules.add(new BungeeCrash());
        modules.add(new SwingCrash());
        modules.add(new AutoLagSign());
        modules.add(new ArmorStandCrash());
        /*----------------------------*/

        /*         COMMANDS           */
        Commands.add(new ArmorCommand());
        Commands.add(new CenterCommand());
        Commands.add(new DesyncCommand());
        Commands.add(new UUIDCommand());
        Commands.add(new Coordinates());
        Commands.add(new DupeReal());
        Commands.add(new PanicCommand());
        Commands.add(new LatencyCommand());
        Commands.add(new Title());
        Commands.add(new ClearCommand());
        Commands.add(new IPLookupCommand());
        Commands.add(new DNSLookupCommand());
        Commands.add(new IPBlacklistCommand());
        Commands.add(new PingCommand());
        Commands.add(new SubnetCalculatorCommand());
        Commands.add(new SpoofNameCommand());
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
        /*----------------------------*/

        /*            HUD             */
        hud.register(CrystalHud.INFO);
        /*----------------------------*/

        LOG.info("Finished initializing Crystal Addon.");
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CRYSTAL_DUPE_CATEGORY);
        Modules.registerCategory(CRYSTAL_CRASH_CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.crystaldevs.crystal";
    }
}