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

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void onRenderItemHead(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand,
            float swingProgress, ItemStack stack, float equipProgress, PoseStack poseStack, MultiBufferSource buffer,
            int combinedLight, CallbackInfo ci) {
        BobLockClient.isRenderingHand = true;

        if (this.minecraft.options.bobView().get()) {
            double velocitySqr = player.getDeltaMovement().horizontalDistanceSqr();
            boolean isMoving = velocitySqr > 0.0001;

            float targetOffset = isMoving ? -0.1f : 0.0f;
            float lerpFactor = 0.1f;

            this.customBobOffset = Mth.lerp(lerpFactor, this.customBobOffset, targetOffset);
            poseStack.translate(0.0, this.customBobOffset, 0.0);
        }
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void onRenderItemReturn(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand,
            float swingProgress, ItemStack stack, float equipProgress, PoseStack poseStack, MultiBufferSource buffer,
            int combinedLight, CallbackInfo ci) {
        BobLockClient.isRenderingHand = false;
    }
}
