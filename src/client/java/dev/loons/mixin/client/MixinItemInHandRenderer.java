package dev.loons.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private float customBobOffset = 0.0f;

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object redirectBobViewOption(net.minecraft.client.OptionInstance<Boolean> instance) {
        if (instance == this.minecraft.options.bobView()) {
            return false;
        }
        return instance.get();
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void onRenderArmWithItem(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand,
            float swingProgress, ItemStack stack, float equipProgress, PoseStack poseStack, MultiBufferSource buffer,
            int combinedLight, CallbackInfo ci) {
        // If View Bobbing is ON, we apply OUR custom bobbing here.
        if (this.minecraft.options.bobView().get()) {
            // Velocity check
            // Use player from args
            double velocitySqr = player.getDeltaMovement().horizontalDistanceSqr();
            boolean isMoving = velocitySqr > 0.0001;

            float targetOffset = isMoving ? -0.1f : 0.0f;
            float lerpFactor = 0.1f;

            this.customBobOffset = Mth.lerp(lerpFactor, this.customBobOffset, targetOffset);

            poseStack.translate(0.0, this.customBobOffset, 0.0);
        }
    }
}
