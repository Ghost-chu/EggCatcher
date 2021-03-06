/*
EggCatcher
Copyright (C) 2012, 2013  me@shansen.me

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package me.shansen.EggCatcher;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public enum EggType {
    EVOKER(EntityType.EVOKER, 34, "Evoker"),
    VEX(EntityType.VEX, 35, "Vex"),
    VINDICATOR(EntityType.VINDICATOR, 36, "Vindicator"),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE, 57, "PigZombie"),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, 62, "MagmaCube"),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, 59, "CaveSpider"),
    MUSHROOM_COW(EntityType.MUSHROOM_COW, 96, "MushroomCow"),
    CREEPER(EntityType.CREEPER, 50, "Creeper"),
    WITHER_SKELETON(EntityType.WITHER_SKELETON, 5, "WitherSkeleton"),
    STRAY(EntityType.STRAY, 6, "Stray"),
    SKELETON(EntityType.SKELETON, 51, "Skeleton"),
    SPIDER(EntityType.SPIDER, 52, "Spider"),
    HUSK(EntityType.HUSK, 23, "Husk"),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, 27, "ZombieVillager"),
    ZOMBIE(EntityType.ZOMBIE, 54, "Zombie"),
    SLIME(EntityType.SLIME, 55, "Slime"),
    GHAST(EntityType.GHAST, 56, "Ghast"),
    ENDERMAN(EntityType.ENDERMAN, 58, "Enderman"),
    SILVERFISH(EntityType.SILVERFISH, 60, "Silverfish"),
    BLAZE(EntityType.BLAZE, 61, "Blaze"),
    PIG(EntityType.PIG, 90, "Pig"),
    SHEEP(EntityType.SHEEP, 91, "Sheep"),
    COW(EntityType.COW, 92, "Cow"),
    CHICKEN(EntityType.CHICKEN, 93, "Chicken"),
    SQUID(EntityType.SQUID, 94, "Squid"),
    WOLF(EntityType.WOLF, 95, "Wolf"),
    VILLAGER(EntityType.VILLAGER, 120, "Villager"),
    OCELOT(EntityType.OCELOT, 98, "Ocelot"),
    BAT(EntityType.BAT, 65, "Bat"),
    WITCH(EntityType.WITCH, 66, "Witch"),
    ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE, 29, "ZombieHorse"),
    SKELETON_HORSE(EntityType.SKELETON_HORSE, 28, "SkeletonHorse"),
    LLAMA(EntityType.LLAMA, 103, "Llama"),
    DONKEY(EntityType.DONKEY, 31, "Donkey"),
    MULE(EntityType.MULE, 32, "Mule"),
    HORSE(EntityType.HORSE, 100, "Horse"),
    ENDERMITE(EntityType.ENDERMITE, 67, "Endermite"),
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, 4, "ElderGuardian"),
    GUARDIAN(EntityType.GUARDIAN, 68, "Guardian"),
    RABBIT(EntityType.RABBIT, 101, "Rabbit"),
    POLAR_BEAR(EntityType.POLAR_BEAR, 102, "PolarBear"),
    SHULKER(EntityType.SHULKER, 69, "Shulker"),
    PARROT(EntityType.PARROT, 105, "Parrot"),
    DOLPHIN(EntityType.DOLPHIN, 105, "Dolphin"),
    DROWNED(EntityType.DROWNED, 105, "Drowned"),
    PHAMTOM(EntityType.PHANTOM, 105, "Phontom"),
    PUFFERFISH(EntityType.PUFFERFISH, 105, "PufferFish"),
    SALMON(EntityType.SALMON, 105, "Salmon"),
    TURTLE(EntityType.TURTLE, 105, "Turtle"),
    TROPICAL_FISH(EntityType.TROPICAL_FISH, 105, "TropicalFish");
	

    private final EntityType entityType;
    private final Integer creatureId;
    private final String friendlyName;

    EggType(EntityType entityType, Integer creatureId, String friendlyName) {
        this.entityType = entityType;
        this.creatureId = creatureId;
        this.friendlyName = friendlyName;
    }

    public short getCreatureId() {
        return this.creatureId.shortValue();
    }

    public EntityType getCreatureType() {
        return this.entityType;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public static EggType getEggType(Entity entity) {
        for (EggType eggType : EggType.values()) {
            if (!eggType.getCreatureType().getEntityClass().isInstance(entity)) {
                continue;
            }
            return eggType;
        }
        return null;
    }
}
