package dev.loons.mixin.client;

import dev.loons.BobLockClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptionInstance.class)
public class MixinOptionInstance<T> {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onGet(CallbackInfoReturnable<T> cir) {
        if (BobLockClient.isRenderingHand) {
            // Check if this option instance is the bobView option
            try {
                if ((Object) this == Minecraft.getInstance().options.bobView()) {
                    cir.setReturnValue((T) Boolean.FALSE);
                }
            } catch (Exception e) {
                // Ignore errors (e.g. if Minecraft not initialized yet, though unlikely during
                // render)
            }
        }
    }
}
