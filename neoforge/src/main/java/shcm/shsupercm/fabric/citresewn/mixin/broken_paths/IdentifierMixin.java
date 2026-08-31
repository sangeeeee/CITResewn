package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.BrokenPaths;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import static shcm.shsupercm.fabric.citresewn.config.BrokenPaths.processingBrokenPaths;

/** Applies the optional broken-resource-path compatibility mode. */
@Mixin(Identifier.class)
public final class IdentifierMixin {
    @Inject(method = "isPathValid", cancellable = true, at = @At("RETURN"))
    private static void citresewn$brokenpaths$processBrokenPaths(
            String path, CallbackInfoReturnable<Boolean> callback) {
        if (!processingBrokenPaths || callback.getReturnValue())
            return;

        if (Platform.isDevelopmentEnvironment())
            CITResewn.logWarnLoading("Encountered broken path: \"" + path + "\"");
        callback.setReturnValue(true);
    }
}
