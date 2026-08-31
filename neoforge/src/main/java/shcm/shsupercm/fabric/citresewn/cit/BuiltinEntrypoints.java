package shcm.shsupercm.fabric.citresewn.cit;

import shcm.shsupercm.fabric.citresewn.api.CITConditionContainer;
import shcm.shsupercm.fabric.citresewn.api.CITGlobalProperties;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.core.FallbackCondition;
import shcm.shsupercm.fabric.citresewn.cit.builtin.conditions.core.WeightCondition;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionComponents;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionDamage;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionDamageMask;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionEnchantmentLevels;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionEnchantments;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionHand;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionItems;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionStackSize;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeArmor;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeElytra;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Explicit replacement for Fabric Loader metadata entrypoint discovery.
 */
public final class BuiltinEntrypoints {
    private BuiltinEntrypoints() {
    }

    public static List<CITConditionContainer<?>> conditionContainers() {
        return List.of(
                FallbackCondition.CONTAINER,
                WeightCondition.CONTAINER,
                ConditionComponents.CONTAINER,
                ConditionDamage.CONTAINER,
                ConditionDamageMask.CONTAINER,
                ConditionEnchantmentLevels.CONTAINER,
                ConditionEnchantments.CONTAINER,
                ConditionHand.CONTAINER,
                ConditionItems.CONTAINER,
                ConditionStackSize.CONTAINER
        );
    }

    public static List<CITTypeContainer<?>> typeContainers() {
        return List.of(
                TypeArmor.CONTAINER,
                TypeElytra.CONTAINER,
                TypeEnchantment.CONTAINER,
                TypeItem.CONTAINER
        );
    }

    public static List<CITGlobalProperties> globalProperties() {
        List<CITGlobalProperties> handlers = new ArrayList<>();
        handlers.add(FallbackCondition::globalProperty);
        handlers.add(TypeEnchantment.CONTAINER);
        return handlers;
    }
}
