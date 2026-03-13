package com.odtheking.iqaddon.mixin;

import com.odtheking.iqaddon.features.impl.skyblock.FullBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 直接攔截染色玻璃片
@Mixin(CrossCollisionBlock.class)
public class PaneOutlineMixin {

    // 這裡攔截 getShape，這會改變遊戲畫出來的「黑色選取框」
    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void iqaddon$injectOutlineShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {

        // 呼叫你的 Kotlin 邏輯，如果功能開啟，會拿到一個 1x1x1 的形狀
        VoxelShape customShape = FullBlock.INSTANCE.getShape(state);

        // 如果有拿到自訂形狀，就強制替換掉原版的薄薄黑框
        if (customShape != null) {
            cir.setReturnValue(customShape);
        }
    }
}