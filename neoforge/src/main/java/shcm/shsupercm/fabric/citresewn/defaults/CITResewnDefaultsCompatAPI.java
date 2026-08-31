package shcm.shsupercm.fabric.citresewn.defaults;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeArmor;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeElytra;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.util.function.BiFunction;
import java.util.function.Function;

/** ServiceLoader-based compatibility hook for NeoForge addon mods. */
public abstract class CITResewnDefaultsCompatAPI {
    public static final String ENTRYPOINT = "citresewn:defaults_compat";

    public static void initAll() {
        for (CITResewnDefaultsCompatAPI compatibility : Platform.services(CITResewnDefaultsCompatAPI.class))
            compatibility.onInitializeClient();
    }

    public void onInitializeClient() {
    }

    protected final void typeArmorRedirectSlotGetter(
            BiFunction<LivingEntity, EquipmentSlot, ItemStack> redirect) {
        TypeArmor.CONTAINER.getItemInSlotCompatRedirects.add(redirect);
    }

    protected final void typeElytraRedirectSlotGetter(Function<LivingEntity, ItemStack> redirect) {
        TypeElytra.CONTAINER.getItemInSlotCompatRedirects.add(redirect);
    }
}
