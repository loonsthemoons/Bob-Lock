package dev.loons.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.loons.BobLockClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private float customBobOffset = 0.0f;

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void onRenderItemHead(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand,
            float swingProgress, ItemStack stack, float equipProgress, PoseStack poseStack, MultiBufferSource buffer,
            int combinedLight, CallbackInfo ci) {
        // Check the option BEFORE setting the flag, so we get the real value (not our
        // forced-false value)
        boolean bobEnabled = this.minecraft.options.bobView().get();

        BobLockClient.isRenderingHand = true;

        // Custom Bobbing Application
        if (bobEnabled) {
            // Check input keys instead of velocity for immediate response
            boolean isMoving = this.minecraft.options.keyUp.isDown() ||
                    this.minecraft.options.keyDown.isDown() ||
                    this.minecraft.options.keyLeft.isDown() ||
                    this.minecraft.options.keyRight.isDown();

            // Reduced target offset to -0.05f (was -0.1f)
            float targetOffset = isMoving ? -0.05f : 0.0f;
            // Lower lerp factor for smoother/slower transition (was 0.1f)
            float lerpFactor = 0.05f;

            this.customBobOffset = Mth.lerp(lerpFactor, this.customBobOffset, targetOffset);

            // Apply translation to the PoseStack
            poseStack.translate(0.0, this.customBobOffset, 0.0);
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void onRenderItemReturn(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand,
            float swingProgress, ItemStack stack, float equipProgress, PoseStack poseStack, MultiBufferSource buffer,
            int combinedLight, CallbackInfo ci) {
        BobLockClient.isRenderingHand = false;
    }
}
