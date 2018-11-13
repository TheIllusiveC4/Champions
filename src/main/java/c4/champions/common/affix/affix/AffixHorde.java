package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class AffixHorde extends AffixBase {

    public AffixHorde() {
        super("horde", AffixCategory.DEFENSE);
    }

    @Override
    public void onSpawn(EntityLiving entity, IChampionship cap) {

        AffixNBT.Boolean horde = AffixNBT.getData(cap, getIdentifier(), AffixNBT.Boolean.class);

        if (!horde.mode) {
            int size = 2 + cap.getRank().getTier() * 2;

            for (int i = 0; i < size; i++) {
                ResourceLocation rl = EntityList.getKey(entity);
                if (rl != null) {
                    Entity hordeEntity = EntityList.createEntityByIDFromName(rl, entity.world);

                    if (hordeEntity != null) {

                        if (ChampionHelper.isValidChampion(hordeEntity)) {
                            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving) hordeEntity);

                            if (chp != null) {
                                chp.setRank(RankManager.getEmptyRank());
                            }
                        }
                        hordeEntity.setPosition(entity.posX + entity.getRNG().nextInt(4) - 2, entity.posY,
                                entity.posZ + entity.getRNG().nextInt(4) - 2);
                        entity.world.spawnEntity(hordeEntity);
                    }
                }
            }
            horde.mode = true;
            horde.saveData(entity);
        }
    }
}
