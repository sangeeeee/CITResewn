package shcm.shsupercm.fabric.citresewn.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Small NeoForge-only platform boundary used by the otherwise loader-neutral
 * CIT implementation.
 */
public final class Platform {
    private Platform() {
    }

    public static boolean isModLoaded(String modId) {
        String normalized = normalizeModId(modId);

        ModList loaded = ModList.get();
        if (loaded != null)
            return loaded.isLoaded(normalized);

        LoadingModList loading = LoadingModList.get();
        return loading != null && loading.getMods().stream()
                .anyMatch(mod -> mod.getModId().equals(normalized));
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static String getModVersion(String modId) {
        ModList loaded = ModList.get();
        if (loaded == null)
            return "unknown";

        return loaded.getModContainerById(normalizeModId(modId))
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("unknown");
    }

    /**
     * Works both during early Mixin selection and after normal mod loading.
     */
    public static Set<String> getLoadedModIds() {
        Set<String> ids = new HashSet<>();

        ModList loaded = ModList.get();
        if (loaded != null) {
            loaded.getMods().forEach(mod -> ids.add(normalizeModId(mod.getModId())));
            return ids;
        }

        LoadingModList loading = LoadingModList.get();
        if (loading != null)
            loading.getMods().forEach(mod -> ids.add(normalizeModId(mod.getModId())));

        return ids;
    }

    public static <T> Iterable<T> services(Class<T> type) {
        return ServiceLoader.load(type, Platform.class.getClassLoader());
    }

    public static String normalizeModId(String modId) {
        return modId.replace('-', '_');
    }
}
