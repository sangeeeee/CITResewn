package shcm.shsupercm.fabric.citresewn.config;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Selects optional core mixins before the normal NeoForge mod constructor runs. */
public final class CITResewnMixinConfiguration implements IMixinConfigPlugin {
    private static final String MIXINS_ROOT = "shcm.shsupercm.fabric.citresewn.mixin";

    private boolean brokenPaths;
    private final Set<String> mods = new HashSet<>();
    private final Set<String> announcedCompatMods = new HashSet<>();

    @Override
    public void onLoad(String mixinPackage) {
        brokenPaths = CITResewnConfig.INSTANCE.broken_paths;
        mods.addAll(Platform.getLoadedModIds());
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith(MIXINS_ROOT))
            return false;

        String relativeName = mixinClassName.substring(MIXINS_ROOT.length() + 1);
        if (relativeName.startsWith("broken_paths"))
            return brokenPaths;

        if (relativeName.startsWith("compat.")) {
            String compatibilityName = relativeName.substring("compat.".length());
            int separator = compatibilityName.indexOf('.');
            if (separator < 0)
                return false;

            String modId = compatibilityName.substring(0, separator);
            if (!mods.contains(modId))
                return false;

            if (announcedCompatMods.add(modId))
                CITResewn.info("Loading compatibility for " + modId);
        }

        return true;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
