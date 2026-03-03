package com.arcanium.item

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

/**
 * Механическая дрель — высокочастотный инструмент арканной технологии.
 * При активации применяет Haste III на 30 секунд и выпускает облако пара.
 * 64 заряда, перезарядка 2 минуты.
 */
class MechanicalDrillItem(settings: Settings) : Item(settings) {

    companion object {
        private const val EFFECT_TICKS = 600    // 30 секунд
        private const val COOLDOWN_TICKS = 2400 // 2 минуты
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        // На клиентской стороне не обрабатываем
        if (world.isClient) return TypedActionResult.success(stack)

        // Применяем Haste III для ускоренной добычи блоков
        user.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE, EFFECT_TICKS, 2))

        // Облако пара от работающей дрели
        (world as ServerWorld).spawnParticles(
            ParticleTypes.LARGE_SMOKE,
            user.x, user.y + 1.0, user.z,
            25, 0.4, 0.4, 0.4, 0.05
        )

        // Искры от бура при работе
        world.spawnParticles(
            ParticleTypes.CRIT,
            user.x, user.y + 0.5, user.z,
            20, 0.3, 0.3, 0.3, 0.1
        )

        // Уменьшаем прочность инструмента на 1 заряд
        val slot = if (hand == Hand.MAIN_HAND) EquipmentSlot.MAINHAND else EquipmentSlot.OFFHAND
        stack.damage(1, user, slot)

        // Устанавливаем перезарядку
        user.itemCooldownManager.set(this, COOLDOWN_TICKS)
        user.sendMessage(
            Text.literal("Дрель активирована! Haste III на 30с").formatted(Formatting.GRAY),
            true
        )

        return TypedActionResult.success(stack)
    }
}
