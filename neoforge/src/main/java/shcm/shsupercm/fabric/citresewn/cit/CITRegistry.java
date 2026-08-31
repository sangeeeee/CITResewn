package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.ConstantCondition;
import shcm.shsupercm.fabric.citresewn.pack.PackParser;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static shcm.shsupercm.fabric.citresewn.CITResewn.info;
import static shcm.shsupercm.fabric.citresewn.CITResewn.logWarnLoading;

/** Runtime registry for all CIT types and conditions. */
public final class CITRegistry {
    private CITRegistry() {
    }

    public static final Map<Identifier, CITTypeContainer<? extends CITType>> TYPES = new HashMap<>();
    public static final Map<PropertyKey, CITConditionContainer<? extends CITCondition>> CONDITIONS = new HashMap<>();

    private static final Map<Class<? extends CITType>, Identifier> TYPE_TO_ID = new IdentityHashMap<>();
    private static final Map<Class<? extends CITCondition>, PropertyKey> CONDITION_TO_ID = new IdentityHashMap<>();

    public static void registerAll() {
        TYPES.clear();
        CONDITIONS.clear();
        TYPE_TO_ID.clear();
        CONDITION_TO_ID.clear();

        info("Registering CIT conditions");
        for (CITConditionContainer<?> container : BuiltinEntrypoints.conditionContainers())
            registerCondition("citresewn", container);
        for (CITConditionContainer<?> container : Platform.services(CITConditionContainer.class))
            registerCondition("citresewn", container);

        info("Registering CIT types");
        for (CITTypeContainer<?> container : BuiltinEntrypoints.typeContainers())
            registerType("citresewn", container);
        for (CITTypeContainer<?> container : Platform.services(CITTypeContainer.class))
            registerType("citresewn", container);
    }

    /** Programmatic NeoForge extension point for addon mods. */
    public static void registerCondition(String namespace, CITConditionContainer<?> container) {
        Class<? extends CITCondition> conditionClass = container.createCondition.get().getClass();
        for (String alias : container.aliases) {
            PropertyKey key = new PropertyKey(namespace, alias);
            CONDITIONS.put(key, container);
            CONDITION_TO_ID.putIfAbsent(conditionClass, key);
        }
    }

    /** Programmatic NeoForge extension point for addon mods. */
    public static void registerType(String namespace, CITTypeContainer<?> container) {
        Identifier id = Identifier.of(namespace, container.id);
        TYPES.put(id, container);
        TYPE_TO_ID.putIfAbsent(container.createType.get().getClass(), id);
    }

    public static CITCondition parseCondition(PropertyKey key, PropertyValue value, PropertyGroup properties)
            throws CITParsingException {
        CITConditionContainer<? extends CITCondition> conditionContainer = CONDITIONS.get(key);
        if (conditionContainer == null) {
            logWarnLoading(properties.messageWithDescriptorOf("Unknown condition type \"" + key + "\"", value.position()));
            return ConstantCondition.FALSE;
        }

        CITCondition condition = conditionContainer.createCondition.get();
        condition.load(key, value, properties);
        return condition;
    }

    public static CITType parseType(PropertyGroup properties) throws UnknownCITTypeException {
        Identifier type = Identifier.of("citresewn", "item");
        PropertyValue propertiesType = properties.getLastWithoutMetadata("citresewn", "type");

        if (propertiesType != null) {
            String value = propertiesType.value();
            if (!value.contains(":"))
                value = "citresewn:" + value;
            type = Identifier.tryParse(value);
        }

        CITTypeContainer<? extends CITType> typeContainer = TYPES.get(type);
        if (typeContainer == null)
            throw new UnknownCITTypeException(properties, propertiesType == null ? -1 : propertiesType.position());

        return typeContainer.createType.get();
    }

    public static Identifier idOfType(Class<? extends CITType> clazz) {
        return TYPE_TO_ID.get(clazz);
    }

    public static PropertyKey idOfCondition(Class<? extends CITCondition> clazz) {
        return CONDITION_TO_ID.get(clazz);
    }
}
