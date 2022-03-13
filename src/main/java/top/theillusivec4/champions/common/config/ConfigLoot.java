package top.theillusivec4.champions.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.champions.Champions;

public class ConfigLoot {

  private static final Random RAND = new Random();
  private static final Map<Integer, List<Data>> DROPS = new HashMap<>();

  public static List<ItemStack> getLootDrops(int tier) {
    double totalWeight;
    List<Data> data = new ArrayList<>(DROPS.getOrDefault(tier, new ArrayList<>()));
    List<ItemStack> drops = new ArrayList<>();

    if (data.isEmpty()) {
      return drops;
    }
    int amount = ChampionsConfig.lootScaling ? tier : 1;

    for (int i = 0; i < amount; i++) {
      totalWeight = 0;

      for (Data loot : data) {
        totalWeight += loot.weight;
      }
      double random = RAND.nextDouble() * totalWeight;
      double countWeight = 0;

      for (Data loot : data) {
        countWeight += loot.weight;

        if (countWeight >= random) {
          drops.add(loot.getStack());
          break;
        }
      }
    }
    return drops;
  }

  public static void parse(List<? extends String> lootDrops) {
    Map<Integer, List<Data>> result = new HashMap<>();

    for (String s : lootDrops) {
      String[] parsed = s.split(";");

      if (parsed.length > 0) {
        int tier;
        ItemStack stack;
        int amount = 1;
        boolean enchant = false;
        int weight = 1;

        if (parsed.length < 2) {
          Champions.LOGGER.error(s + " needs at least a tier and an item name");
          continue;
        }

        try {
          tier = Integer.parseInt(parsed[0]);
        } catch (NumberFormatException e) {
          Champions.LOGGER.error(parsed[0] + " is not a valid tier");
          continue;
        }

        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parsed[1]));

        if (item == null) {
          Champions.LOGGER.error("Item not found! " + parsed[1]);
          continue;
        }

        if (parsed.length > 2) {

          try {
            amount = Integer.parseInt(parsed[2]);
          } catch (NumberFormatException e) {
            Champions.LOGGER.error(parsed[2] + " is not a valid stack amount");
          }

          if (parsed.length > 3) {

            if (parsed[3].equalsIgnoreCase("true")) {
              enchant = true;
            }

            if (parsed.length > 4) {
              try {
                weight = Integer.parseInt(parsed[4]);
              } catch (NumberFormatException e) {
                Champions.LOGGER.error(parsed[4] + " is not a valid weight");
              }
            }
          }
        }
        stack = new ItemStack(item, amount);
        result.computeIfAbsent(tier, list -> new ArrayList<>())
            .add(new Data(stack, enchant, weight));
      }
    }
    DROPS.clear();
    DROPS.putAll(result);
  }

  private record Data(ItemStack stack, boolean enchant, int weight) {

    public ItemStack getStack() {
      ItemStack loot = stack.copy();

      if (enchant) {
        EnchantmentHelper.enchantItem(RAND, loot, 30, true);
      }
      return loot;
    }
  }
}
