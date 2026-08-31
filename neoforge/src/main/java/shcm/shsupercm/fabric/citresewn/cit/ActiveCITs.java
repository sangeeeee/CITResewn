package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import shcm.shsupercm.fabric.citresewn.api.CITDisposable;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.core.FallbackCondition;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.core.WeightCondition;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.mixin.ModelLoaderMixin;
import shcm.shsupercm.fabric.citresewn.pack.GlobalProperties;
import shcm.shsupercm.fabric.citresewn.pack.PackParser;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Holds and manages the currently loaded CITs. */
public final class ActiveCITs {
    private static ActiveCITs active;

    private ActiveCITs() {
    }

    public static ActiveCITs getActive() {
        return active;
    }

    public static boolean isActive() {
        return active != null;
    }

    public final GlobalProperties globalProperties = new GlobalProperties();
    public final Map<Class<? extends CITType>, List<CIT<?>>> cits = new IdentityHashMap<>();

    /**
     * Called by {@link ModelLoaderMixin} during resource reload.
     */
    public static void load(ResourceManager resourceManager, Profiler profiler) {
        profiler.push("citresewn:disposing");

        for (CITDisposable disposable : Platform.services(CITDisposable.class))
            disposable.dispose();

        for (CITTypeContainer<? extends CITType> typeContainer : CITRegistry.TYPES.values())
            typeContainer.unload();

        if (active != null) {
            active.globalProperties.properties.replaceAll((key, value) -> Set.of());
            active.globalProperties.callHandlers();
            active = null;
        }

        if (!CITResewnConfig.INSTANCE.enabled) {
            profiler.pop();
            return;
        }

        ActiveCITs next = new ActiveCITs();

        profiler.swap("citresewn:load_global_properties");
        PackParser.loadGlobalProperties(resourceManager, next.globalProperties).callHandlers();

        profiler.swap("citresewn:load_cits");
        List<CIT<?>> parsed = PackParser.parseCITs(resourceManager);
        FallbackCondition.apply(parsed);

        for (CIT<?> cit : parsed)
            next.cits.computeIfAbsent(cit.type.getClass(), type -> new ArrayList<>()).add(cit);

        for (Map.Entry<Class<? extends CITType>, List<CIT<?>>> entry : next.cits.entrySet()) {
            WeightCondition.apply(entry.getValue());

            for (CITTypeContainer<? extends CITType> typeContainer : CITRegistry.TYPES.values())
                if (typeContainer.type == entry.getKey()) {
                    typeContainer.loadUntyped(entry.getValue());
                    break;
                }
        }

        profiler.pop();
        if (!parsed.isEmpty())
            active = next;
    }
}
