package top.theillusivec4.champions.common.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants.NBT;
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
    super(new Item.Properties().group(ItemGroup.MISC));
    this.setRegistryName(RegistryReference.EGG);
  }

  @Override
  public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
    // NO-OP
  }

  @Nonnull
  @Override
  public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
    int tier = 0;
    Optional<EntityType<?>> type = getType(stack);

    if (stack.hasTag()) {
      CompoundNBT tag = stack.getChildTag(CHAMPION_TAG);

      if (tag != null) {
        tier = tag.getInt(TIER_TAG);
      }
    }
    IFormattableTextComponent root = new TranslationTextComponent("rank.champions.title." + tier);
    root.appendString(" ");
    root.append(type.map(EntityType::getName).orElse(EntityType.ZOMBIE.getName()));
    root.appendString(" ");
    root.append(new TranslationTextComponent(this.getTranslationKey(stack)));
    return root;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
                             ITooltipFlag flagIn) {
    boolean hasAffix = false;

    if (stack.hasTag()) {
      CompoundNBT tag = stack.getChildTag(CHAMPION_TAG);

      if (tag != null) {
        ListNBT listNBT = tag.getList(AFFIX_TAG, NBT.TAG_STRING);

        if (!listNBT.isEmpty()) {
          hasAffix = true;
        }
        listNBT.forEach(affix -> Champions.API.getAffix(affix.getString()).ifPresent(
            affix1 -> tooltip.add(
                new TranslationTextComponent("affix.champions." + affix1.getIdentifier())
                    .mergeStyle(TextFormatting.GRAY))));
      }
    }

    if (!hasAffix) {
      tooltip.add(new TranslationTextComponent("item.champions.egg.tooltip")
          .mergeStyle(TextFormatting.AQUA));
    }
  }

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    World world = context.getWorld();

    if (!world.isRemote() && world instanceof ServerWorld) {
      ItemStack itemstack = context.getItem();
      BlockPos blockpos = context.getPos();
      Direction direction = context.getFace();
      BlockState blockstate = world.getBlockState(blockpos);
      BlockPos blockpos1;

      if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
        blockpos1 = blockpos;
      } else {
        blockpos1 = blockpos.offset(direction);
      }
      Optional<EntityType<?>> entitytype = getType(itemstack);
      entitytype.ifPresent(type -> {
        Entity entity = type
            .create((ServerWorld) world, itemstack.getTag(), null, context.getPlayer(), blockpos1,
                SpawnReason.SPAWN_EGG, true,
                !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);

        if (entity instanceof LivingEntity) {
          ChampionCapability.getCapability(entity).ifPresent(champion -> read(champion, itemstack));
          world.addEntity(entity);
          itemstack.shrink(1);
        }
      });
    }
    return ActionResultType.SUCCESS;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn,
                                                  @Nonnull Hand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);

    if (worldIn.isRemote()) {
      return new ActionResult<>(ActionResultType.PASS, itemstack);
    } else if (worldIn instanceof ServerWorld) {
      BlockRayTraceResult raytraceresult = rayTrace(worldIn, playerIn,
          RayTraceContext.FluidMode.SOURCE_ONLY);

      if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
        return new ActionResult<>(ActionResultType.PASS, itemstack);
      } else {
        BlockPos blockpos = raytraceresult.getPos();

        if (!(worldIn.getBlockState(blockpos).getBlock() instanceof FlowingFluidBlock)) {
          return new ActionResult<>(ActionResultType.PASS, itemstack);
        } else if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn
            .canPlayerEdit(blockpos, raytraceresult.getFace(), itemstack)) {
          Optional<EntityType<?>> entityType = getType(itemstack);
          return entityType.map(type -> {
            Entity entity = type
                .create((ServerWorld) worldIn, itemstack.getTag(), null, playerIn, blockpos,
                    SpawnReason.SPAWN_EGG, false, false);

            if (entity instanceof LivingEntity) {
              ChampionCapability.getCapability(entity)
                  .ifPresent(champion -> read(champion, itemstack));
              worldIn.addEntity(entity);

              if (!playerIn.abilities.isCreativeMode) {
                itemstack.shrink(1);
              }
              playerIn.addStat(Stats.ITEM_USED.get(this));
              return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
            } else {
              return new ActionResult<>(ActionResultType.PASS, itemstack);
            }
          }).orElse(new ActionResult<>(ActionResultType.PASS, itemstack));
        } else {
          return new ActionResult<>(ActionResultType.FAIL, itemstack);
        }
      }
    }
    return new ActionResult<>(ActionResultType.FAIL, itemstack);
  }

  public static int getColor(ItemStack stack, int tintIndex) {
    SpawnEggItem eggItem = SpawnEggItem.getEgg(getType(stack).orElse(EntityType.ZOMBIE));
    return eggItem != null ? eggItem.getColor(tintIndex) : 0;
  }

  public static Optional<EntityType<?>> getType(ItemStack stack) {

    if (stack.hasTag()) {
      CompoundNBT entityTag = stack.getChildTag(ENTITY_TAG);

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
      CompoundNBT tag = stack.getChildTag(CHAMPION_TAG);

      if (tag != null) {
        int tier = tag.getInt(TIER_TAG);
        ListNBT listNBT = tag.getList(AFFIX_TAG, NBT.TAG_STRING);
        List<IAffix> affixes = new ArrayList<>();
        listNBT.forEach(affix -> Champions.API.getAffix(affix.getString()).ifPresent(affixes::add));
        ChampionBuilder.spawnPreset(champion, tier, affixes);
      }
    }
  }

  public static void write(ItemStack stack, ResourceLocation entityId, int tier,
                           Collection<IAffix> affixes) {
    CompoundNBT tag = stack.hasTag() ? stack.getTag() : new CompoundNBT();
    assert tag != null;

    CompoundNBT compoundNBT = new CompoundNBT();
    compoundNBT.putString(ID_TAG, entityId.toString());
    tag.put(ENTITY_TAG, compoundNBT);

    CompoundNBT compoundNBT1 = new CompoundNBT();
    compoundNBT1.putInt(TIER_TAG, tier);
    ListNBT listNBT = new ListNBT();
    affixes.forEach(affix -> listNBT.add(StringNBT.valueOf(affix.getIdentifier())));
    compoundNBT1.put(AFFIX_TAG, listNBT);
    tag.put(CHAMPION_TAG, compoundNBT1);
    stack.setTag(tag);
  }
}
