package net.denanu.amazia.entities.village.server.goal.mineing;

import net.denanu.amazia.entities.village.server.MinerEntity;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;

public class ExtendMineGoal extends TimedMineGoal {
	public ExtendMineGoal(MinerEntity e, int priority) {
		super(e, priority);
	}

	@Override
	protected int getRequiredTime() {
		return this.entity.getMineTime();
	}

	@Override
	protected void takeAction() {
		this.entity.getWorld().breakBlock(this.pos, true);
		this.entity.addSeroundingOreBlock(pos);
	}

	@Override
	protected boolean shouldStart() {
		if (this.pos == null) {
			this.pos = this.entity.getNextOreBlock();
		}
		if (this.pos == null) {
			this.pos = this.entity.getMine().getBlockToExtend(this.entity);
		}
		return pos != null;
	}
}
