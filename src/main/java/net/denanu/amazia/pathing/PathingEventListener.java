package net.denanu.amazia.pathing;

import net.minecraft.util.math.BlockPos;

public interface PathingEventListener {

	void onCreate(BlockPos pos);

	void onDestroy(BlockPos pos);
	
}
