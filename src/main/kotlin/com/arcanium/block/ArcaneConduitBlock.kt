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
 * Арканный проводник — блок, пропитанный арканной энергией.
 * Стоя на нём, существо получает Speed II и Strength I на 20 секунд.
 */
class ArcaneConduitBlock(settings: Settings) : Block(settings) {

    companion object {
        // Длительность эффектов проводника: 20 секунд
        private const val EFFECT_TICKS = 400
    }

    override fun onSteppedOn(world: World, pos: BlockPos, state: BlockState, entity: Entity) {
        if (!world.isClient && entity is LivingEntity) {
            // Передаём арканную энергию через подошву: скорость и силу
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED, EFFECT_TICKS, 1))
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.STRENGTH, EFFECT_TICKS, 0))

            // Частицы зачарования вокруг сущности при контакте с проводником
            (world as ServerWorld).spawnParticles(
                ParticleTypes.ENCHANT,
                entity.x, entity.y + 0.5, entity.z,
                15, 0.3, 0.3, 0.3, 0.05
            )
        }
        super.onSteppedOn(world, pos, state, entity)
    }
}
