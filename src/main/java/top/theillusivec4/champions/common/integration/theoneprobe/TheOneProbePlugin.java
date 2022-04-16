package top.theillusivec4.champions.common.integration.theoneprobe;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.ElementHorizontal;
import mcjty.theoneprobe.apiimpl.elements.ElementText;
import mcjty.theoneprobe.apiimpl.elements.ElementVertical;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampion;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static top.theillusivec4.champions.common.capability.ChampionCapability.CHAMPION_CAP;

public class TheOneProbePlugin implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return Champions.MODID + ":entity.champion";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        entity.getCapability(CHAMPION_CAP).ifPresent(champion -> {
            IChampion.Server serverChampion = champion.getServer();
            serverChampion.getRank().ifPresent(rank -> {
                if (rank.getTier() == 0) {
                    return;
                }
                List<IElement> elements = iProbeInfo.getElements();
                ElementHorizontal test = (ElementHorizontal) iProbeInfo.getElements().get(0);
                ElementVertical iElement = (ElementVertical) test.getElements().get(1);
                iElement.getElements().set(0, new ElementText(new TranslatableComponent("rank.champions.title." + rank.getTier()).getString() + " " + entity.getName().getString()));
                serverChampion.getAffixes().stream().map(IAffix::getIdentifier)
                        .collect(Collectors.toSet()).forEach(affix -> {
                            iProbeInfo.mcText(new TranslatableComponent("affix." + Champions.MODID + "." + affix));
                        });
            });
        });
    }

    public static final class GetTheOneProbe implements Function<ITheOneProbe, Void> {
        @Override
        public Void apply(final ITheOneProbe input) {
            final IProbeInfoEntityProvider instance = new TheOneProbePlugin();
            input.registerEntityProvider(instance);
            return null;
        }
    }
}

