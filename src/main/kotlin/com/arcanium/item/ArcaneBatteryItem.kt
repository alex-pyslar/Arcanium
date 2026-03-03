package com.arcanium.item

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
 * Арканный аккумулятор — накопитель арканной энергии высокой плотности.
 * Заряжает игрока: Haste II + Ночное зрение + Прыжки II на 60 секунд.
 * Перезарядка 5 минут. Не расходуется при использовании.
 */
class ArcaneBatteryItem(settings: Settings) : Item(settings) {

    companion object {
        private const val EFFECT_TICKS = 1200   // 60 секунд
        private const val COOLDOWN_TICKS = 6000 // 5 минут
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        // На клиентской стороне не обрабатываем
        if (world.isClient) return TypedActionResult.success(stack)

        // Передаём накопленную арканную энергию игроку
        user.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE, EFFECT_TICKS, 1))
        user.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION, EFFECT_TICKS, 0))
        user.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, EFFECT_TICKS, 1))

        // Электрические разряды при высвобождении энергии
        (world as ServerWorld).spawnParticles(
            ParticleTypes.ELECTRIC_SPARK,
            user.x, user.y + 1.0, user.z,
            40, 0.6, 0.6, 0.6, 0.15
        )

        user.sendMessage(
            Text.literal("Арканный заряд получен! Haste II + Ночное зрение + Прыжки II").formatted(Formatting.AQUA),
            true
        )

        // Устанавливаем перезарядку до следующей зарядки
        user.itemCooldownManager.set(this, COOLDOWN_TICKS)

        return TypedActionResult.success(stack)
    }
}
