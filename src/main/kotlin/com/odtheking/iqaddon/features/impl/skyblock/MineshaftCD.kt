package com.odtheking.iqaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.*
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.render.textDim

object MineshaftCD : Module (
    name = "Mineshaft",
    description = "Features for Mineshaft"
) {
    private val cd_display by BooleanSetting("CD Display for Mineshaft", false, "")
    private var triggerTime = 0L;
    private val cooldownDuration = 30_000L;
    private var display: Boolean = false;

    init {
        on<ChatPacketEvent> {
            if (value.matches(Regex(".+\nentered Glacite Mineshafts!\n.+"))) {
                triggerTime = System.currentTimeMillis();
            }
        }

    }
    private val timerHud = HUD("Mineshaft Cooldown Timer", "Shows cooldown for mineshaft") {
        val currentTime = System.currentTimeMillis()
        val timePassed = currentTime - triggerTime
        val timeLeft = cooldownDuration - timePassed

        if (it) {
            textDim("§e冷卻時間: §c30.0s", 0, 0, Colors.WHITE)
        }
        else if (timeLeft > 0) {
            val secondsLeft = String.format("%.1f", timeLeft / 1000f)
            textDim("§e冷卻時間: §c${secondsLeft}s", 0, 0, Colors.WHITE)
        }
        else {
            0 to 0
        }
    }
}