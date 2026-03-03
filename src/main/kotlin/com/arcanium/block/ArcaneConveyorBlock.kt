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
 * Арканный конвейер — транспортный блок для ускоренного перемещения по базе.
 * Даёт Speed III + Jump Boost I при ходьбе по нему (эффекты обновляются каждые 5с).
 * Светится слабым голубым светом (luminance 6).
 */
class ArcaneConveyorBlock(settings: Settings) : Block(settings) {

    companion object {
        // Длительность эффектов конвейера: 5 секунд (постоянно обновляется при ходьбе)
        private const val EFFECT_TICKS = 100
    }

    override fun onSteppedOn(world: World, pos: BlockPos, state: BlockState, entity: Entity) {
        if (!world.isClient && entity is LivingEntity) {

            // Ускоренное перемещение по конвейерной дорожке
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.SPEED, EFFECT_TICKS, 2))
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, EFFECT_TICKS, 0))

            // Синие электрические искры под ногами при движении
            (world as ServerWorld).spawnParticles(
                ParticleTypes.ELECTRIC_SPARK,
                entity.x, entity.y + 0.05, entity.z,
                5, 0.2, 0.0, 0.2, 0.02
            )
        }
        super.onSteppedOn(world, pos, state, entity)
    }
}
