package com.odtheking.iqaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.*
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.render.textDim

object MineshaftCD : Module (
    name = "Mineshaft",
    description = "Features for Mineshaft"
) {
    private var triggerTime = 0L;
    private val cooldownDuration = 30_000L;

    init {
        on<ChatPacketEvent> {
            if (value.matches(Regex(".+\n.+entered Glacite Mineshafts!\n.+"))) {
                triggerTime = System.currentTimeMillis();
            }
        }

    }
    private val timerHud by HUD("Mineshaft CD Hud", "Shows cooldown for mineshaft") {
        val currentTime = System.currentTimeMillis()
        val timePassed = currentTime - triggerTime
        val timeLeft = cooldownDuration - timePassed

        if (!enabled) 0 to 0

        if (it) {
            textDim("§bMineshaft CD: §a30.0s", 0, 0, Colors.WHITE)
        }
        else if (timeLeft > 0) {
            val secondsLeft = String.format("%.1f", timeLeft / 1000f)
            textDim("§bMineshaft CD: ${secondsLeft}s", 0, 0, Colors.WHITE)
        }
        else {
            0 to 0
        }
    }
}