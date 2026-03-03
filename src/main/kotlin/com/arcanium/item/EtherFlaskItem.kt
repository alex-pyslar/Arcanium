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
 * Эфирная колба — колба со сконцентрированной арканной эссенцией.
 * Даёт Absorption III + Resistance II на 20 секунд.
 * Перезарядка 3 минуты.
 */
class EtherFlaskItem(settings: Settings) : Item(settings) {

    companion object {
        private const val EFFECT_TICKS = 400    // 20 секунд
        private const val COOLDOWN_TICKS = 3600 // 3 минуты
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        // На клиентской стороне не обрабатываем
        if (world.isClient) return TypedActionResult.success(stack)

        // Вливаем арканную эссенцию — поглощение и сопротивление урону
        user.addStatusEffect(StatusEffectInstance(StatusEffects.ABSORPTION, EFFECT_TICKS, 2))
        user.addStatusEffect(StatusEffectInstance(StatusEffects.RESISTANCE, EFFECT_TICKS, 1))

        // Визуальный эффект поглощения эссенции
        (world as ServerWorld).spawnParticles(
            ParticleTypes.TOTEM_OF_UNDYING,
            user.x, user.y + 1.0, user.z,
            30, 0.5, 0.6, 0.5, 0.05
        )

        user.sendMessage(
            Text.literal("Арканная эссенция поглощена!").formatted(Formatting.LIGHT_PURPLE),
            true
        )

        // Устанавливаем перезарядку
        user.itemCooldownManager.set(this, COOLDOWN_TICKS)

        return TypedActionResult.success(stack)
    }
}
