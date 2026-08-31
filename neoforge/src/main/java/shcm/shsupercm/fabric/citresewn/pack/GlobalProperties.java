package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.api.CITGlobalProperties;
import shcm.shsupercm.fabric.citresewn.cit.BuiltinEntrypoints;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Merged representation of all active {@code cit.properties} files. */
public class GlobalProperties extends PropertyGroup {
    public GlobalProperties() {
        super("global_properties", Identifier.of("citresewn", "global_properties"));
    }

    @Override
    public String getExtension() {
        return ".properties";
    }

    @Override
    public PropertyGroup load(String packName, Identifier identifier, InputStream stream)
            throws IOException, InvalidIdentifierException {
        PropertyGroup group = PropertyGroup.tryParseGroup(packName, identifier, stream);
        if (group != null)
            for (Map.Entry<PropertyKey, Set<PropertyValue>> entry : group.properties.entrySet())
                properties.computeIfAbsent(entry.getKey(), key -> new LinkedHashSet<>()).addAll(entry.getValue());
        return this;
    }

    public void callHandlers() {
        Set<CITGlobalProperties> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        for (CITGlobalProperties handler : BuiltinEntrypoints.globalProperties())
            callHandler("citresewn", handler, visited);
        for (CITGlobalProperties handler : Platform.services(CITGlobalProperties.class))
            callHandler("citresewn", handler, visited);
    }

    private void callHandler(String namespace, CITGlobalProperties handler, Set<CITGlobalProperties> visited) {
        if (!visited.add(handler))
            return;

        for (Map.Entry<PropertyKey, Set<PropertyValue>> entry : properties.entrySet()) {
            if (!entry.getKey().namespace().equals(namespace))
                continue;

            PropertyValue lastValue = null;
            for (PropertyValue value : entry.getValue())
                lastValue = value;

            try {
                handler.globalProperty(entry.getKey().path(), lastValue);
            } catch (Exception exception) {
                CITResewn.logErrorLoading(lastValue == null
                        ? "Errored while disposing global properties"
                        : "Errored while parsing global properties: Line " + lastValue.position()
                        + " of " + lastValue.propertiesIdentifier() + " in " + lastValue.packName());
                CITResewn.LOG.error("Global property handler failed", exception);
            }
        }
    }
}
