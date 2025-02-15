package net.denanu.amazia.entities.village.server.goal;

import net.denanu.amazia.entities.village.server.AmaziaVillagerEntity;
import net.denanu.amazia.pathing.PathingPath;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class BaseAmaziaGoToBlockGoal<E extends AmaziaVillagerEntity> extends BaseAmaziaVillageGoal<E> {
	private BlockPos targetPos;
	private boolean isRunning;
	protected boolean reached;
	private PathingPath path;
	private EntityNavigation nav;
	private int ticksStanding;

	public BaseAmaziaGoToBlockGoal(E e, int priority) {
		super(e, priority);
		this.nav =  e.getNavigation();
	}
	
	@Override
	public void start() {
		super.start();
		this.isRunning = true;
		this.reached = false;
		this.ticksStanding = 0;
		this.recalcPath();
	}
	
	@Override
	public void stop() {
		super.stop();
		this.isRunning = false;
		this.targetPos = null;
	}
	
	@Override
	public boolean canStart() {
		if (super.canStart()) {
			this.getNewTargetPos();
			return targetPos != null && this.entity.getVillagePathFinder() != null;
		}
		return false;
	}
	
	@Override
	public boolean shouldContinue() {
		return super.shouldContinue() && path != null && targetPos != null && !this.reached && path.getLength() > 1;
	}
	
	@Override
    public void tick() {
		Vec3d targetPos = new Vec3d(this.targetPos.getX() + 0.5, this.targetPos.getY() + 1.0, this.targetPos.getZ() + 0.5);
		if (this.nav.isIdle()) {
			this.ticksStanding++;
		}
		else {
			this.ticksStanding = 0;
		}
		double distance = targetPos.squaredDistanceTo(this.entity.getPos());
        if (distance > this.getDesiredDistanceToTarget()) {
        	this.reached = false;
        	if (distance < 3) {
            	this.runBackupMotion();
            }
        	else {
	            if (this.shouldResetPath()) {
	                this.recalcPath();
	            }
        	}
        }         
        else {
            this.reached = true;
        }
    }
	
	private void runBackupMotion() {
		this.entity.getNavigation().stop();
    	this.entity.getMoveControl().moveTo( this.targetPos.getX() + 0.5,  this.targetPos.getY() + 1, this.targetPos.getZ() + 0.5, 1);
	}
	
	public boolean shouldResetPath() {
        return this.ticksStanding > 2;
    }
	
	public double getDesiredDistanceToTarget() {
        return 0.5; // note distance squared
    }
	
	protected void recalcPath() {
		path = this.entity.getVillagePathFinder().findPath(this.entity.world, this.targetPos);
		if (path != null && path.getLength() == 1) {
			this.entity.getMoveControl().moveTo( this.targetPos.getX() + 0.5,  this.targetPos.getY() + 1, this.targetPos.getZ() + 0.5, 1);
		}
		nav.startMovingAlong(path, 1);
		this.ticksStanding = 0;
	}
	
	private void getNewTargetPos() {
		if (this.targetPos == null) {
			this.targetPos = this.getTargetBlock();
		}
	}
	
	protected abstract BlockPos getTargetBlock();

	public boolean isRunning() {
		return isRunning;
	}
}
