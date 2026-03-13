package com.odtheking.iqaddon.features.impl.floor7

import com.odtheking.iqaddon.utils.render.Render2DUtils
import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.phys.Vec3
import kotlin.math.atan2
import kotlin.math.sqrt

object WitherAimBot : Module(
    "Wither Aimbot",
    description = "Aimbot for withers in F7/M7"
) {
    private val fovRadius by NumberSetting("Radius", 50f, 10f, 200f, 1f
                                            , "Radius for Auto-Aiming");
    private val circleToggled by BooleanSetting("Circle", false, "Display Circle")

    private val lineThickness by NumberSetting(name = "Thickness", default = 1f, min = 1f, max = 5f, increment = 1f
                                            , desc = "Thickness of Line").withDependency { circleToggled }
    private val circleColor by ColorSetting(name = "Color", default = Colors.WHITE, allowAlpha = true, desc = "Color of Circle").withDependency { circleToggled }

    private var currentTarget: Entity? = null
    private var isHoldingCorrectWeapon = false

    init {
        HudRenderCallback.EVENT.register { guiGraphics, tickDelta ->

            if (enabled && circleToggled) {
                val window = mc.window

                val centerX = window.guiScaledWidth / 2f
                val centerY = window.guiScaledHeight / 2f

                Render2DUtils.drawHollowCircle(
                    guiGraphics,
                    centerX,
                    centerY,
                    fovRadius,
                    circleColor,
                    lineThickness*2
                )
            }

        }

        WorldRenderEvents.END_EXTRACTION.register{context ->
            val player = mc.player
            val level = mc.level
            if (!enabled || level == null || player == null) {
                currentTarget = null
                return@register
            }

            val partialTick = context.tickCounter().getGameTimeDeltaPartialTick(true)

            val window = mc.window
            val centerX = window.guiScaledWidth / 2f
            val centerY = window.guiScaledHeight / 2f
            val radiusSq = fovRadius * fovRadius

            var closestDistanceSq = Float.MAX_VALUE
            var bestTarget: Entity? = null

            level.entitiesForRendering().forEach { entity ->
                if (entity is WitherBoss && entity.isAlive && entity.invulnerableTicks <= 0) {

                    val lerpedPos = entity.getPosition(partialTick)
                    val targetPos = Vec3(lerpedPos.x, lerpedPos.y + 1.25, lerpedPos.z)

                    val screenPos = Render2DUtils.worldToScreen(targetPos)

                    if (screenPos != null) {
                        val dx = screenPos.x - centerX
                        val dy = screenPos.y - centerY
                        val distanceSq = (dx * dx) + (dy * dy)

                        if (distanceSq <= radiusSq && distanceSq < closestDistanceSq) {
                            closestDistanceSq = distanceSq
                            bestTarget = entity
                        }
                    }
                }
            }

            currentTarget = bestTarget

            if (bestTarget != null) {
                if (!mc.options.keyAttack.isDown) return@register

                val mainHandItem = player.mainHandItem
                val itemName = mainHandItem.hoverName.string
                isHoldingCorrectWeapon = itemName.contains("Hyperion", ignoreCase = true) ||
                        itemName.contains("Claymore", ignoreCase = true)

                if (!isHoldingCorrectWeapon) return@register

                // 玩家自己的眼睛也需要插值，確保視角計算沒有誤差
                val playerLerpedPos = player.getPosition(partialTick)
                val playerEyePos = Vec3(playerLerpedPos.x, playerLerpedPos.y + player.eyeHeight, playerLerpedPos.z)

                // 目標瞄準點
                val targetLerpedPos = bestTarget!!.getPosition(partialTick)
                val targetAimPos = Vec3(targetLerpedPos.x, targetLerpedPos.y + 1.25, targetLerpedPos.z)

                val dx = targetAimPos.x - playerEyePos.x
                val dy = targetAimPos.y - playerEyePos.y
                val dz = targetAimPos.z - playerEyePos.z

                val distXZ = sqrt(dx * dx + dz * dz)
                val yaw = Math.toDegrees(atan2(dz, dx)).toFloat() - 90f
                val pitch = -Math.toDegrees(atan2(dy, distXZ)).toFloat()

                player.yRot = yaw
                player.xRot = pitch
                player.yRotO = yaw
                player.xRotO = pitch
            }
        }
    }

}