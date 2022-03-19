package top.theillusivec4.champions.common.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.champions.Champions;
import top.theillusivec4.champions.api.IAffix;
import top.theillusivec4.champions.api.IChampion;
import top.theillusivec4.champions.common.capability.ChampionCapability;
import top.theillusivec4.champions.common.registry.RegistryReference;
import top.theillusivec4.champions.common.util.ChampionBuilder;

public class ChampionEggItem extends Item {

  private static final String ID_TAG = "Id";
  private static final String ENTITY_TAG = "EntityTag";
  private static final String TIER_TAG = "Tier";
  private static final String AFFIX_TAG = "Affix";
  private static final String CHAMPION_TAG = "Champion";

  public ChampionEggItem() {
    super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    this.setRegistryName(RegistryReference.EGG);
  }

  @Override
  public void fillItemCategory(@Nonnull CreativeModeTab group,
                               @Nonnull NonNullList<ItemStack> items) {
    // NO-OP
  }

  @Nonnull
  @Override
  public Component getName(@Nonnull ItemStack stack) {
    int tier = 0;
    Optional<EntityType<?>> type = getType(stack);

    if (stack.hasTag()) {
      CompoundTag tag = stack.getOrCreateTag().getCompound(CHAMPION_TAG);

      if (tag != null) {
        tier = tag.getInt(TIER_TAG);
      }
    }
    BaseComponent root = new TranslatableComponent("rank.champions.title." + tier);
    root.append(" ");
    root.append(type.map(EntityType::getDescription).orElse(EntityType.ZOMBIE.getDescription()));
    root.append(" ");
    root.append(this.getDescription());
    return root;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn,
                              @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
    boolean hasAffix = false;

    if (stack.hasTag()) {
      CompoundTag tag = stack.getOrCreateTag().getCompound(CHAMPION_TAG);

      if (tag != null) {
        ListTag listNBT = tag.getList(AFFIX_TAG, CompoundTag.TAG_STRING);

        if (!listNBT.isEmpty()) {
          hasAffix = true;
        }

        listNBT.forEach(affix -> Champions.API.getAffix(affix.getAsString()).ifPresent(
            affix1 -> {
              final MutableComponent component =
                  new TranslatableComponent("affix.champions." + affix1.getIdentifier());
              component.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
              tooltip.add(component);
            }));
      }
    }

    if (!hasAffix) {
      final MutableComponent component = new TranslatableComponent("item.champions.egg.tooltip");
      component.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
      tooltip.add(component);
    }
  }

  @Nonnull
  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level world = context.getLevel();

    if (!world.isClientSide() && world instanceof ServerLevel) {
      ItemStack itemstack = context.getItemInHand();
      BlockPos blockpos = context.getClickedPos();
      Direction direction = context.getClickedFace();
      BlockState blockstate = world.getBlockState(blockpos);
      BlockPos blockpos1;

      if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
        blockpos1 = blockpos;
      } else {
        blockpos1 = blockpos.relative(direction);
      }
      Optional<EntityType<?>> entitytype = getType(itemstack);
      entitytype.ifPresent(type -> {
        Entity entity = type
            .create((ServerLevel) world, itemstack.getTag(), null, context.getPlayer(), blockpos1,
                MobSpawnType.SPAWN_EGG, true,
                !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);

        if (entity instanceof LivingEntity) {
          ChampionCapability.getCapability(entity)
              .ifPresent(champion -> read(champion, itemstack));
          world.addFreshEntity(entity);
          itemstack.shrink(1);
        }
      });
    }
    return InteractionResult.SUCCESS;
  }

  @Nonnull
  @Override
  public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn,
                                                @Nonnull InteractionHand handIn) {
    ItemStack itemstack = playerIn.getItemInHand(handIn);

    if (worldIn.isClientSide()) {
      return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
    } else if (worldIn instanceof ServerLevel) {
      BlockHitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn,
          ClipContext.Fluid.SOURCE_ONLY);

      if (raytraceresult.getType() != HitResult.Type.BLOCK) {
        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
      } else {
        BlockPos blockpos = raytraceresult.getBlockPos();

        if (!(worldIn.getFluidState(blockpos).getType() instanceof FlowingFluid)) {
          return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        } else if (worldIn.mayInteract(playerIn, blockpos) && playerIn
            .mayUseItemAt(blockpos, raytraceresult.getDirection(), itemstack)) {
          Optional<EntityType<?>> entityType = getType(itemstack);
          return entityType.map(type -> {
            Entity entity = type
                .create((ServerLevel) worldIn, itemstack.getTag(), null, playerIn, blockpos,
                    MobSpawnType.SPAWN_EGG, false, false);

            if (entity instanceof LivingEntity) {
              ChampionCapability.getCapability(entity)
                  .ifPresent(champion -> read(champion, itemstack));
              worldIn.addFreshEntity(entity);

              if (!playerIn.getAbilities().invulnerable) {
                itemstack.shrink(1);
              }
              playerIn.awardStat(Stats.ITEM_USED.get(this));
              return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
            } else {
              return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
            }
          }).orElse(new InteractionResultHolder<>(InteractionResult.PASS, itemstack));
        } else {
          return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
        }
      }
    }
    return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
  }

  public static int getColor(ItemStack stack, int tintIndex) {
    SpawnEggItem eggItem =
        ForgeSpawnEggItem.fromEntityType(getType(stack).orElse(EntityType.ZOMBIE));
    return eggItem != null ? eggItem.getColor(tintIndex) : 0;
  }

  public static Optional<EntityType<?>> getType(ItemStack stack) {

    if (stack.hasTag()) {
      CompoundTag entityTag = stack.getTagElement(ENTITY_TAG);

      if (entityTag != null) {
        String id = entityTag.getString(ID_TAG);

        if (!id.isEmpty()) {
          EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));

          if (type != null) {
            return Optional.of(type);
          }
        }
      }
    }
    return Optional.empty();
  }

  public static void read(IChampion champion, ItemStack stack) {

    if (stack.hasTag()) {
      CompoundTag tag = stack.getTagElement(CHAMPION_TAG);

      if (tag != null) {
        int tier = tag.getInt(TIER_TAG);
        ListTag listNBT = tag.getList(AFFIX_TAG, CompoundTag.TAG_STRING);
        List<IAffix> affixes = new ArrayList<>();
        listNBT.forEach(
            affix -> Champions.API.getAffix(affix.getAsString()).ifPresent(affixes::add));
        ChampionBuilder.spawnPreset(champion, tier, affixes);
      }
    }
  }

  public static void write(
      ItemStack stack, ResourceLocation entityId, int tier,
      Collection<IAffix> affixes) {
    CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
    assert tag != null;

    CompoundTag compoundNBT = new CompoundTag();
    compoundNBT.putString(ID_TAG, entityId.toString());
    tag.put(ENTITY_TAG, compoundNBT);

    CompoundTag compoundNBT1 = new CompoundTag();
    compoundNBT1.putInt(TIER_TAG, tier);
    ListTag listNBT = new ListTag();
    affixes.forEach(affix -> listNBT.add(StringTag.valueOf(affix.getIdentifier())));
    compoundNBT1.put(AFFIX_TAG, listNBT);
    tag.put(CHAMPION_TAG, compoundNBT1);
    stack.setTag(tag);
  }
}
