package com.arcanium.item

import net.minecraft.block.BlockState
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Рунный клинок — клинок, покрытый рунами огня и иссушения.
 * При ударе: поджигает цель на 5 секунд и накладывает Wither I на 4 секунды.
 * 256 зарядов прочности.
 */
class RuneBladeItem(settings: Settings) : Item(settings) {

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        if (!attacker.world.isClient) {
            // Поджигаем цель на 5 секунд (руна огня)
            target.setOnFireFor(5)
            // Накладываем иссушение I на 4 секунды (руна увядания)
            target.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER, 80, 0))
        }
        // Расходуем заряд клинка при каждом ударе
        stack.damage(1, attacker, EquipmentSlot.MAINHAND)
        return true
    }

    override fun canMine(state: BlockState, world: World, pos: BlockPos, miner: PlayerEntity): Boolean = true
}
