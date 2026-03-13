package com.odtheking.iqaddon.mixin;

import com.odtheking.iqaddon.features.impl.skyblock.FullBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClipContext.class)
public class ClipContextMixin {

    // 攔截視線追蹤獲取形狀的瞬間
    @Inject(method = "getBlockShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void iqaddon$injectBlockShape(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        // 呼叫你的 Kotlin 程式碼
        VoxelShape outline = FullBlock.INSTANCE.getShape(state);

        // 如果你的功能有開啟且判斷是玻璃窗，就會取代原本的形狀
        if (outline != null) {
            cir.setReturnValue(outline);
        }
    }
}