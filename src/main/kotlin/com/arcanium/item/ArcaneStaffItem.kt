package com.arcanium.item

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
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
import net.minecraft.util.math.Box
import net.minecraft.world.World

/**
 * Арканный посох — канализирует арканную энергию в мощный AOE-выброс.
 * Применяет Wither I и Slowness II всем врагам в радиусе 6 блоков.
 * 80 зарядов, перезарядка 8 секунд.
 */
class ArcaneStaffItem(settings: Settings) : Item(settings) {

    companion object {
        private const val RADIUS = 6.0
        private const val EFFECT_TICKS = 80      // 4 секунды
        private const val COOLDOWN_TICKS = 160   // 8 секунд
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        // На клиентской стороне не обрабатываем логику
        if (world.isClient) return TypedActionResult.success(stack)

        // Определяем область поражения вокруг игрока
        val box = Box(
            user.x - RADIUS, user.y - RADIUS, user.z - RADIUS,
            user.x + RADIUS, user.y + RADIUS, user.z + RADIUS
        )
        val targets = world.getEntitiesByClass(LivingEntity::class.java, box) { it != user }

        if (targets.isEmpty()) return TypedActionResult.pass(stack)

        // Применяем эффекты ко всем целям в зоне поражения
        targets.forEach { entity ->
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER, EFFECT_TICKS, 0))
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, EFFECT_TICKS, 1))
        }

        // Визуальный эффект выброса арканной энергии
        (world as ServerWorld).spawnParticles(
            ParticleTypes.WITCH,
            user.x, user.y + 1.0, user.z,
            50, RADIUS / 2, RADIUS / 2, RADIUS / 2, 0.08
        )

        // Расходуем заряд посоха
        val slot = if (hand == Hand.MAIN_HAND) EquipmentSlot.MAINHAND else EquipmentSlot.OFFHAND
        stack.damage(1, user, slot)

        user.itemCooldownManager.set(this, COOLDOWN_TICKS)
        user.sendMessage(
            Text.literal("Арканный выброс! Поражено целей: ${targets.size}").formatted(Formatting.DARK_PURPLE),
            true
        )

        return TypedActionResult.success(stack)
    }
}
