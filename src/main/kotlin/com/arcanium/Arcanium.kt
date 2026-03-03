package com.arcanium

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Arcanium : ModInitializer {

    const val MOD_ID = "arcanium"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        ModItems.register()
        ModBlocks.register()
        LOGGER.info("Arcanium — арканные технологии пробуждаются!")
    }
}
