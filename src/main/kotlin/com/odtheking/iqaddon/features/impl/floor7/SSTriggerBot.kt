package com.odtheking.iqaddon.features.impl.floor7

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.iqaddon.utils.clock.Clock
import com.odtheking.odin.features.impl.floor7.SimonSays
import com.odtheking.odin.events.*
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand


object SSTriggerBot : Module(
    name = "SS TriggerBot",
    description = "triggerbot for ss"
) {
    // These are visible settings that will render under this module in the GUI
    private val toggled by BooleanSetting("Enabled", false, desc = "")
    private val triggerBotDelay by NumberSetting("Triggerbot Delay", 200L, 70, 500, unit = "ms", desc = "Delay for Each Click")

    private val triggerBotClock = Clock(triggerBotDelay)
    private val firstClickClock = Clock(100)

    private val clickInOrderField by lazy {
        SimonSays::class.java.getDeclaredField("clickInOrder").apply { isAccessible = true }
    }
    private val clickNeededField by lazy {
        SimonSays::class.java.getDeclaredField("clickNeeded").apply { isAccessible = true }
    }

    init {
        on<RenderEvent.Extract> {
            if (!SSTriggerBot.toggled|| !triggerBotClock.hasTimePassed(triggerBotDelay) || mc.screen != null) return@on

            // 透過反射即時抓取現在的解答進度
            val clickInOrder = clickInOrderField.get(SimonSays) as ArrayList<BlockPos>
            val clickNeeded = clickNeededField.getInt(SimonSays)

            // 如果還沒有解答，或者已經點完了，就跳出
            if (clickInOrder.isEmpty() || clickNeeded >= clickInOrder.size) return@on

            // 取得玩家目前準心看著的方塊
            val hitResult = mc.hitResult as? net.minecraft.world.phys.BlockHitResult ?: return@on
            val pos = hitResult.blockPos
            // 檢查：看著的方塊的「東邊一格」是不是等於解答需要的方塊？
            if (clickInOrder.getOrNull(clickNeeded) != pos.east()) return@on

            // 核心點擊邏輯
            if (clickNeeded == 0) {
                // 避免狂點第一個按鈕導致解謎壞掉
                if (!firstClickClock.hasTimePassed()) return@on
                firstClickClock.update()
                mc.gameMode?.useItemOn(mc.player, InteractionHand.MAIN_HAND, hitResult)
                mc.player?.swing(InteractionHand.MAIN_HAND)
                return@on
            }

            // 點擊後續的按鈕
            triggerBotClock.update()
            mc.gameMode?.useItemOn(mc.player, InteractionHand.MAIN_HAND, hitResult)
            mc.player?.swing(InteractionHand.MAIN_HAND)
        }

    }

}