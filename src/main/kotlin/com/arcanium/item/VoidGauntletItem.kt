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
 * Перчатка пустоты — покрыта рунами пустоты.
 * При ударе: поднимает цель в воздух (Levitation I, 3с) и ослепляет (Blindness, 2с).
 * 128 зарядов прочности.
 */
class VoidGauntletItem(settings: Settings) : Item(settings) {

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        if (!attacker.world.isClient) {
            // Руна левитации — поднимает цель в воздух на 3 секунды
            target.addStatusEffect(StatusEffectInstance(StatusEffects.LEVITATION, 60, 0))
            // Руна пустоты — ослепляет цель на 2 секунды
            target.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, 40, 0))
        }
        // Расходуем заряд перчатки при каждом ударе
        stack.damage(1, attacker, EquipmentSlot.MAINHAND)
        return true
    }

    override fun canMine(state: BlockState, world: World, pos: BlockPos, miner: PlayerEntity): Boolean = true
}
