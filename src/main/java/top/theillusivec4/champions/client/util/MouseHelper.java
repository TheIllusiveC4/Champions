package top.theillusivec4.champions.client.util;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import top.theillusivec4.champions.client.config.ClientChampionsConfig;
import top.theillusivec4.champions.common.capability.ChampionCapability;

public class MouseHelper {

  public static Optional<LivingEntity> getMouseOverChampion(Minecraft mc, float partialTicks) {
    Entity entity = mc.getRenderViewEntity();

    if (entity != null) {

      if (mc.world != null) {
        mc.getProfiler().startSection("mouse_champion");
        double d0 = ClientChampionsConfig.hudRange;
        RayTraceResult rayTraceResult = entity.pick(d0, partialTicks, false);
        Vec3d vec3d = entity.getEyePosition(partialTicks);
        double d1 = rayTraceResult.getHitVec().squareDistanceTo(vec3d);
        Vec3d vec3d1 = entity.getLook(1.0F);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
        AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.scale(d0))
            .grow(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult entityraytraceresult = ProjectileHelper
            .rayTraceEntities(entity, vec3d, vec3d2, axisalignedbb,
                (p_215312_0_) -> !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith(), d1);

        if (entityraytraceresult != null) {
          Entity entity1 = entityraytraceresult.getEntity();

          if (entity1 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity1;
            return ChampionCapability.getCapability(livingEntity).isPresent() ? Optional.of(livingEntity) : Optional.empty();
          }
        }
        mc.getProfiler().endSection();
      }
    }
    return Optional.empty();
  }
}
