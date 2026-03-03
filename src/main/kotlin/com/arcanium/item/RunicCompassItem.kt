package com.arcanium.item

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

/**
 * Рунный компас — покрыт рунами точки привязки.
 * Первое использование: сохраняет текущую позицию как рунный якорь.
 * Второе использование: телепортирует обратно к якорю.
 */
class RunicCompassItem(settings: Settings) : Item(settings) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        // На клиентской стороне не обрабатываем
        if (world.isClient) return TypedActionResult.success(stack)

        // Читаем NBT данные из компонента предмета
        val nbt = stack.get(DataComponentTypes.CUSTOM_DATA)?.copyNbt() ?: NbtCompound()

        // Если якорь уже установлен — телепортируемся, иначе — сохраняем якорь
        if (nbt.contains("anchor_x")) {
            teleportToAnchor(world, user, stack, nbt)
        } else {
            saveAnchor(world, user, stack, nbt)
        }

        return TypedActionResult.success(stack)
    }

    /** Сохраняет текущие координаты игрока как рунный якорь */
    private fun saveAnchor(world: World, user: PlayerEntity, stack: ItemStack, nbt: NbtCompound) {
        nbt.putDouble("anchor_x", user.x)
        nbt.putDouble("anchor_y", user.y)
        nbt.putDouble("anchor_z", user.z)
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt))

        user.sendMessage(
            Text.literal("Рунный якорь установлен!").formatted(Formatting.GOLD),
            true
        )

        // Частицы зачарования при установке якоря
        (world as ServerWorld).spawnParticles(
            ParticleTypes.ENCHANT,
            user.x, user.y + 1.0, user.z,
            25, 0.4, 0.4, 0.4, 0.1
        )
    }

    /** Телепортирует игрока к сохранённому якорю и сбрасывает его */
    private fun teleportToAnchor(world: World, user: PlayerEntity, stack: ItemStack, nbt: NbtCompound) {
        val x = nbt.getDouble("anchor_x")
        val y = nbt.getDouble("anchor_y")
        val z = nbt.getDouble("anchor_z")

        // Портальные частицы в точке отправления
        (world as ServerWorld).spawnParticles(
            ParticleTypes.PORTAL,
            user.x, user.y + 1.0, user.z,
            40, 0.5, 0.5, 0.5, 0.1
        )

        // Выполняем телепортацию через сетевой обработчик сервера
        (user as ServerPlayerEntity).networkHandler.requestTeleport(x, y, z, user.yaw, user.pitch)

        // Удаляем якорь из NBT после использования
        nbt.remove("anchor_x")
        nbt.remove("anchor_y")
        nbt.remove("anchor_z")
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt))

        user.sendMessage(
            Text.literal("Руническое возвращение активировано!").formatted(Formatting.GOLD),
            true
        )

        user.itemCooldownManager.set(this, 100)
    }
}
