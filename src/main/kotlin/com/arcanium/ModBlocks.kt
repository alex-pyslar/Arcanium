package com.arcanium

import com.arcanium.block.ArcaneConduitBlock
import com.arcanium.block.ArcaneConveyorBlock
import com.arcanium.block.ArcaneReactorBlock
import com.arcanium.block.RunicTrapBlock
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModBlocks {

    // ─── Декоративные и магические блоки ─────────────────────────────────────

    /** Блок арканиума — декоративный, светится (luminance 12), твёрдый как обсидиан */
    val ARCANIUM_BLOCK: Block = registerBlock(
        "arcanium_block",
        Block(
            AbstractBlock.Settings.copy(Blocks.OBSIDIAN)
                .strength(4.0f)
                .luminance { 12 }
                .requiresTool()
        )
    )

    /** Арканный проводник — стоя на нём: Speed II + Strength I на 20с */
    val ARCANE_CONDUIT: Block = registerBlock(
        "arcane_conduit",
        ArcaneConduitBlock(
            AbstractBlock.Settings.copy(Blocks.STONE)
                .strength(2.0f)
                .luminance { 8 }
        )
    )

    /** Руническая ловушка — невидимая, при наступлении: Wither II + Blindness на 4с */
    val RUNIC_TRAP: Block = registerBlock(
        "runic_trap",
        RunicTrapBlock(
            AbstractBlock.Settings.create()
                .strength(0.5f)
                .noCollision()
                .noBlockBreakParticles()
        )
    )

    // ─── Технологические блоки (автоматизация базы) ───────────────────────────

    /** Арканный реактор — энергетическое ядро базы, стоя: Regeneration II + Haste I + Night Vision на 30с */
    val ARCANE_REACTOR: Block = registerBlock(
        "arcane_reactor",
        ArcaneReactorBlock(
            AbstractBlock.Settings.copy(Blocks.OBSIDIAN)
                .strength(5.0f)
                .luminance { 15 }
                .requiresTool()
        )
    )

    /** Арканный конвейер — транспортный блок, стоя: Speed III + Jump Boost I (обновляется каждые 5с) */
    val ARCANE_CONVEYOR: Block = registerBlock(
        "arcane_conveyor",
        ArcaneConveyorBlock(
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
                .strength(1.5f)
                .luminance { 6 }
        )
    )

    // ─── Вспомогательные функции ──────────────────────────────────────────────

    private fun registerBlock(name: String, block: Block): Block {
        val id = Identifier.of(Arcanium.MOD_ID, name)
        // Регистрируем блок и его предметную форму (BlockItem) одновременно
        Registry.register(Registries.BLOCK, id, block)
        Registry.register(Registries.ITEM, id, BlockItem(block, Item.Settings()))
        return block
    }

    fun register() {
        // Добавляем все блоки во вкладку строительных блоков творческого режима
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register { entries ->
            entries.add(ARCANIUM_BLOCK)
            entries.add(ARCANE_CONDUIT)
            entries.add(RUNIC_TRAP)
            // Технологические блоки
            entries.add(ARCANE_REACTOR)
            entries.add(ARCANE_CONVEYOR)
        }
        Arcanium.LOGGER.info("Блоки зарегистрированы")
    }
}
