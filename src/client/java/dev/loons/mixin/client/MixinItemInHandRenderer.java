package dev.loons.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.loons.BobLockClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private float customBobOffset = 0.0f;

    @Unique
    private long lastInputTime = 0;

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void onRenderItemHead(CallbackInfo ci) {
        BobLockClient.wasBobbingEnabled = this.minecraft.options.bobView().get();
        BobLockClient.isRenderingHand = true;
    }

    @ModifyVariable(method = "renderArmWithItem", at = @At("HEAD"), argsOnly = true)
    private PoseStack applyCustomBobbing(PoseStack poseStack) {
        if (BobLockClient.wasBobbingEnabled) {
            // Check input keys instead of velocity for immediate response
            boolean isInputActive = this.minecraft.options.keyUp.isDown() ||
                    this.minecraft.options.keyDown.isDown() ||
                    this.minecraft.options.keyLeft.isDown() ||
                    this.minecraft.options.keyRight.isDown();

            if (isInputActive) {
                this.lastInputTime = System.currentTimeMillis();
            }

            // Add a small delay before returning to verify stop moving
            long timeSinceInput = System.currentTimeMillis() - this.lastInputTime;
            boolean shouldBeLowered = isInputActive || timeSinceInput < 90;

            float targetOffset = shouldBeLowered ? -0.08f : 0.0f;
            // Lower lerp factor for smoother/slower transition
            float lerpFactor = 0.03f;

            this.customBobOffset = Mth.lerp(lerpFactor, this.customBobOffset, targetOffset);

            // Apply translation to the PoseStack
            poseStack.translate(0.0, this.customBobOffset, 0.0);
        }
        return poseStack;
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void onRenderItemReturn(CallbackInfo ci) {
        BobLockClient.isRenderingHand = false;
    }
}
