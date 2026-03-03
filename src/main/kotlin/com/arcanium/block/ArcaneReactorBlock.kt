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
 * Арканный реактор — энергетическое ядро технологической базы.
 * Стоя на нём, существо получает Регенерацию II + Haste I + Ночное зрение на 30с.
 * Светится максимальной яркостью (luminance 15).
 */
class ArcaneReactorBlock(settings: Settings) : Block(settings) {

    companion object {
        // Длительность эффектов реактора: 30 секунд
        private const val EFFECT_TICKS = 600
    }

    override fun onSteppedOn(world: World, pos: BlockPos, state: BlockState, entity: Entity) {
        if (!world.isClient && entity is LivingEntity) {

            // Реактор передаёт энергию стоящему на нём существу
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION, EFFECT_TICKS, 1))
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE, EFFECT_TICKS, 0))
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION, EFFECT_TICKS, 0))

            // Плазменные частицы от активного реактора
            (world as ServerWorld).spawnParticles(
                ParticleTypes.FLAME,
                entity.x, entity.y + 0.3, entity.z,
                8, 0.2, 0.2, 0.2, 0.04
            )

            // Душевое пламя — признак высокой энергетической плотности
            world.spawnParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                pos.x + 0.5, pos.y + 1.0, pos.z + 0.5,
                6, 0.3, 0.1, 0.3, 0.05
            )
        }
        super.onSteppedOn(world, pos, state, entity)
    }
}
