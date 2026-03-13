package com.odtheking.iqaddon

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.ModuleManager
import com.odtheking.iqaddon.commands.odinAddonCommand
import com.odtheking.iqaddon.features.impl.skyblock.FullBlock
import com.odtheking.iqaddon.features.impl.floor7.SSTriggerBot
import com.odtheking.iqaddon.features.impl.floor7.WitherAimBot
import com.odtheking.iqaddon.features.impl.skyblock.MineshaftCD
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object OdinAddon : ClientModInitializer {

    override fun onInitializeClient() {
        println("Odin Addon IQ200 initialized!")

        // Register commands by adding to the array
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            arrayOf(odinAddonCommand).forEach { commodore -> commodore.register(dispatcher) }
        }

        // Register objects to event bus by adding to the list
        listOf(this).forEach { EventBus.subscribe(it) }

        // Register modules by adding to the list
        ModuleManager.registerModules(ModuleConfig("OdinAddon-IQ.json"), FullBlock, MineshaftCD, WitherAimBot)
    }
}
