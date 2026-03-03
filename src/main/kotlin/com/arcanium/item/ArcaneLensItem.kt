package com.arcanium.item

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
 * Арканная линза — обнаруживает все живые существа в радиусе 16 блоков,
 * применяя эффект Glowing и делая их видимыми сквозь стены на 10 секунд.
 * Перезарядка 30 секунд.
 */
class ArcaneLensItem(settings: Settings) : Item(settings) {

    companion object {
        private const val RADIUS = 16.0
        private const val GLOW_TICKS = 200       // 10 секунд
        private const val COOLDOWN_TICKS = 600   // 30 секунд
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        // На клиентской стороне не обрабатываем
        if (world.isClient) return TypedActionResult.success(stack)

        // Формируем область сканирования вокруг игрока
        val box = Box(
            user.x - RADIUS, user.y - RADIUS, user.z - RADIUS,
            user.x + RADIUS, user.y + RADIUS, user.z + RADIUS
        )
        val targets = world.getEntitiesByClass(LivingEntity::class.java, box) { it != user }

        // Применяем Glowing ко всем обнаруженным существам
        targets.forEach { entity ->
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, GLOW_TICKS, 0))
        }

        // Визуальный эффект арканного сканирования
        (world as ServerWorld).spawnParticles(
            ParticleTypes.GLOW,
            user.x, user.y + 1.5, user.z,
            40, RADIUS / 3, RADIUS / 3, RADIUS / 3, 0.0
        )

        user.sendMessage(
            Text.literal("Арканное сканирование: обнаружено существ — ${targets.size}").formatted(Formatting.AQUA),
            true
        )

        user.itemCooldownManager.set(this, COOLDOWN_TICKS)

        return TypedActionResult.success(stack)
    }
}
