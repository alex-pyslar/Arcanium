package com.arcanium.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Руническая ловушка — невидимая руническая ловушка, врезанная в пол.
 * При наступлении накладывает Wither II и Blindness на 4 секунды.
 */
class RunicTrapBlock(settings: Settings) : Block(settings) {

    companion object {
        // Длительность эффектов ловушки: 4 секунды
        private const val EFFECT_TICKS = 80
    }

    override fun onSteppedOn(world: World, pos: BlockPos, state: BlockState, entity: Entity) {
        if (!world.isClient && entity is LivingEntity) {
            // Активируем ловушку: иссушение II и слепота
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER, EFFECT_TICKS, 1))
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.BLINDNESS, EFFECT_TICKS, 0))

            // Тёмные частицы ведьмы при активации ловушки
            (world as ServerWorld).spawnParticles(
                ParticleTypes.WITCH,
                entity.x, entity.y + 0.1, entity.z,
                15, 0.3, 0.1, 0.3, 0.03
            )
        }
        super.onSteppedOn(world, pos, state, entity)
    }
}
