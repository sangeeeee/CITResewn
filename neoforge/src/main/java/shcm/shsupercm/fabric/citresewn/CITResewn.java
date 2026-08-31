package shcm.shsupercm.fabric.citresewn;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shcm.shsupercm.fabric.citresewn.cit.CITRegistry;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.defaults.CITResewnDefaultsCompatAPI;

/**
 * NeoForge entrypoint for the combined core and defaults modules.
 */
@Mod("citresewn")
public final class CITResewn {
    public static final Logger LOG = LogManager.getLogger("CITResewn");
    public static CITResewn INSTANCE;

    public CITResewn(IEventBus modBus) {
        INSTANCE = this;

        // The mod contains client mixins and deliberately does no work on a
        // dedicated server. Keeping this guard here also avoids loading any
        // Minecraft client class on the server.
        if (FMLEnvironment.dist != Dist.CLIENT)
            return;

        CITRegistry.registerAll();
        CITResewnDefaultsCompatAPI.initAll();
        NeoForge.EVENT_BUS.addListener(CITResewnCommand::register);
    }

    public static void info(String message) {
        LOG.info("[citresewn] " + message);
    }

    public static void logWarnLoading(String message) {
        if (!CITResewnConfig.INSTANCE.mute_warns)
            LOG.warn("[citresewn] " + message);
    }

    public static void logErrorLoading(String message) {
        if (!CITResewnConfig.INSTANCE.mute_errors)
            LOG.error("[citresewn] " + message);
    }
}
