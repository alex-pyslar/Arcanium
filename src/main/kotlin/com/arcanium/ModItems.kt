package com.arcanium

import com.arcanium.item.ArcaneStaffItem
import com.arcanium.item.ArcaneBatteryItem
import com.arcanium.item.ArcaneLensItem
import com.arcanium.item.EtherFlaskItem
import com.arcanium.item.GravitonPulserItem
import com.arcanium.item.MechanicalDrillItem
import com.arcanium.item.RuneBladeItem
import com.arcanium.item.RunicCompassItem
import com.arcanium.item.VoidGauntletItem
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModItems {

    // ─── Материалы ───────────────────────────────────────────────────────────

    /** Осколок арканиума — базовый крафтовый ингредиент мода */
    val ARCANIUM_SHARD: Item = register("arcanium_shard", Item(Item.Settings()))

    // ─── Магическое оружие и инструменты ─────────────────────────────────────

    /** Арканный посох — AOE Wither I + Slowness II в радиусе 6, кд 8с */
    val ARCANE_STAFF: Item = register(
        "arcane_staff",
        ArcaneStaffItem(Item.Settings().maxCount(1).maxDamage(80))
    )

    /** Рунный клинок — при ударе: Огонь 5с + Wither I 4с */
    val RUNE_BLADE: Item = register(
        "rune_blade",
        RuneBladeItem(Item.Settings().maxCount(1).maxDamage(256))
    )

    /** Перчатка пустоты — при ударе: Levitation I 3с + Blindness 2с */
    val VOID_GAUNTLET: Item = register(
        "void_gauntlet",
        VoidGauntletItem(Item.Settings().maxCount(1).maxDamage(128))
    )

    /** Эфирная колба — Absorption III + Resistance II на 20с, кд 3мин */
    val ETHER_FLASK: Item = register(
        "ether_flask",
        EtherFlaskItem(Item.Settings().maxCount(1))
    )

    /** Рунный компас — сохранение и телепорт к якорю */
    val RUNIC_COMPASS: Item = register(
        "runic_compass",
        RunicCompassItem(Item.Settings().maxCount(1))
    )

    /** Арканная линза — Glowing всем существам в радиусе 16 на 10с, кд 30с */
    val ARCANE_LENS: Item = register(
        "arcane_lens",
        ArcaneLensItem(Item.Settings().maxCount(1))
    )

    // ─── Технологические инструменты (автоматизация) ──────────────────────────

    /** Механическая дрель — Haste III на 30с, 64 заряда, кд 2мин */
    val MECHANICAL_DRILL: Item = register(
        "mechanical_drill",
        MechanicalDrillItem(Item.Settings().maxCount(1).maxDamage(64))
    )

    /** Арканный аккумулятор — Haste II + Ночное зрение + Прыжки II на 60с, кд 5мин */
    val ARCANE_BATTERY: Item = register(
        "arcane_battery",
        ArcaneBatteryItem(Item.Settings().maxCount(1))
    )

    /** Гравитонный импульсатор — AOE knockback радиус 8, 48 зарядов, кд 12с */
    val GRAVITON_PULSER: Item = register(
        "graviton_pulser",
        GravitonPulserItem(Item.Settings().maxCount(1).maxDamage(48))
    )

    // ─── Вспомогательные функции ──────────────────────────────────────────────

    private fun register(name: String, item: Item): Item =
        Registry.register(Registries.ITEM, Identifier.of(Arcanium.MOD_ID, name), item)

    fun register() {
        // Добавляем все предметы во вкладку инструментов творческого режима
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register { entries ->
            entries.add(ARCANIUM_SHARD)
            entries.add(ARCANE_STAFF)
            entries.add(RUNE_BLADE)
            entries.add(VOID_GAUNTLET)
            entries.add(ETHER_FLASK)
            entries.add(RUNIC_COMPASS)
            entries.add(ARCANE_LENS)
            // Технологические предметы
            entries.add(MECHANICAL_DRILL)
            entries.add(ARCANE_BATTERY)
            entries.add(GRAVITON_PULSER)
        }
        Arcanium.LOGGER.info("Предметы зарегистрированы")
    }
}
