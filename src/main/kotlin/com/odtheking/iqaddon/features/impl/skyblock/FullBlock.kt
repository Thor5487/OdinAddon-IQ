package com.odtheking.iqaddon.features.impl.skyblock

import com.odtheking.odin.features.Module
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.StainedGlassPaneBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

object FullBlock : Module(
    name = "FullBlock",
    description = "full block for glass pane"
) {

    fun getShape(state: BlockState): VoxelShape? {
        if (!enabled) return null;

        return when (state.block) {
            is StainedGlassPaneBlock -> Shapes.block()
            else -> null;
        }
    }

}