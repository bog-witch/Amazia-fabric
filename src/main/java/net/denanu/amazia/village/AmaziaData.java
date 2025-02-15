package net.denanu.amazia.village;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class AmaziaData {
	public static final Set<Item> CLASSIC_PLANTABLES = ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS);
	public static final Set<Item> SAPLINGS = ImmutableSet.of(Items.ACACIA_SAPLING, Items.BIRCH_SAPLING, Items.DARK_OAK_SAPLING, Items.JUNGLE_SAPLING, Items.OAK_SAPLING, Items.SPRUCE_SAPLING);
}
