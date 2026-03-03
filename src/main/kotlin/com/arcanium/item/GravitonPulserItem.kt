package com.arcanium.item

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
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
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * Гравитонный импульсатор — устройство управления гравитационным полем.
 * Отталкивает все живые существа в радиусе 8 блоков и подбрасывает вверх.
 * 48 зарядов, перезарядка 12 секунд.
 */
class GravitonPulserItem(settings: Settings) : Item(settings) {

    companion object {
        private const val RADIUS = 8.0
        private const val COOLDOWN_TICKS = 240   // 12 секунд
        private const val KNOCKBACK_FORCE = 2.5  // сила горизонтального отталкивания
        private const val LIFT_FORCE = 0.8       // сила вертикального подъёма
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        // На клиентской стороне не обрабатываем
        if (world.isClient) return TypedActionResult.success(stack)

        // Сканируем область радиуса для поиска целей
        val box = Box(
            user.x - RADIUS, user.y - RADIUS, user.z - RADIUS,
            user.x + RADIUS, user.y + RADIUS, user.z + RADIUS
        )
        val targets = world.getEntitiesByClass(LivingEntity::class.java, box) { it != user }

        targets.forEach { entity ->
            // Вычисляем нормализованный вектор от центра импульса к цели
            val dir = Vec3d(
                entity.x - user.x,
                entity.y - user.y + 0.5,
                entity.z - user.z
            ).normalize()

            // Применяем гравитационный импульс: отброс + подъём
            entity.velocity = Vec3d(
                dir.x * KNOCKBACK_FORCE,
                LIFT_FORCE,
                dir.z * KNOCKBACK_FORCE
            )
            entity.velocityModified = true
        }

        // Взрывная волна из частиц при выстреле импульса
        (world as ServerWorld).spawnParticles(
            ParticleTypes.EXPLOSION,
            user.x, user.y + 1.0, user.z,
            5, RADIUS / 4, RADIUS / 4, RADIUS / 4, 0.1
        )

        // Арканное облако разряда энергии
        world.spawnParticles(
            ParticleTypes.ENCHANT,
            user.x, user.y + 1.0, user.z,
            60, RADIUS / 2, RADIUS / 2, RADIUS / 2, 0.2
        )

        // Уменьшаем прочность устройства
        val slot = if (hand == Hand.MAIN_HAND) EquipmentSlot.MAINHAND else EquipmentSlot.OFFHAND
        stack.damage(1, user, slot)

        user.itemCooldownManager.set(this, COOLDOWN_TICKS)
        user.sendMessage(
            Text.literal("Гравитонный импульс! ${targets.size} целей отброшено").formatted(Formatting.DARK_AQUA),
            true
        )

        return TypedActionResult.success(stack)
    }
}
