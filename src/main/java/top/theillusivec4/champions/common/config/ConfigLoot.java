package top.theillusivec4.champions.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.champions.Champions;

public class ConfigLoot {

  private static final Random RAND = new Random();

  private static Map<Integer, List<Data>> drops;

  public static List<ItemStack> getLootDrops(int tier) {
    double totalWeight;
    List<Data> data = new ArrayList<>(drops.getOrDefault(tier, new ArrayList<>()));
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
    drops = new HashMap<>();

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
        drops.computeIfAbsent(tier, list -> new ArrayList<>())
            .add(new Data(stack, enchant, weight));
      }
    }
  }

  private static class Data {

    private ItemStack stack;
    private boolean enchant;
    private int weight;

    Data(ItemStack stack, boolean enchant, int weight) {
      this.stack = stack;
      this.enchant = enchant;
      this.weight = weight;
    }

    public ItemStack getStack() {
      ItemStack loot = stack.copy();

      if (enchant) {
        EnchantmentHelper.addRandomEnchantment(RAND, loot, 30, true);
      }
      return loot;
    }
  }
}
