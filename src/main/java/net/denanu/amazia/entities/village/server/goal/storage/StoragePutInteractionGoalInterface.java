package net.denanu.amazia.entities.village.server.goal.storage;

import net.denanu.amazia.village.sceduling.utils.StoragePathingData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface StoragePutInteractionGoalInterface {
	public void StorageInteractionDone();
	public StoragePathingData getTarget();
	public ItemStack getItem();
	public int getItemIdx();
	public int getMaxDepositable();

}
