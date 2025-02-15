package net.denanu.amazia.entities.village.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.denanu.amazia.entities.village.server.goal.farming.AmaziaGoToFarmGoal;
import net.denanu.amazia.entities.village.server.goal.farming.HarvestCropsGoal;
import net.denanu.amazia.entities.village.server.goal.farming.HoeFarmLandGoal;
import net.denanu.amazia.entities.village.server.goal.farming.PlantCropsGoal;
import net.denanu.amazia.village.AmaziaData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import oshi.util.tuples.Triplet;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class FarmerEntity extends AmaziaVillagerEntity implements IAnimatable  {
	public static final ImmutableSet<Item> USABLE_ITEMS = ImmutableSet.of(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE, Items.AIR);
	public static final ImmutableMap<Item, Integer> REQUIRED_ITEMS = ImmutableMap.of(Items.CARROT, 16, Items.POTATO, 16, Items.WHEAT_SEEDS, 16, Items.BEETROOT_SEEDS, 16);
	
	private AnimationFactory factory = new AnimationFactory(this);

	public FarmerEntity(EntityType<? extends PassiveEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@Override
    protected void initGoals() {                
        this.goalSelector.add(47, new HarvestCropsGoal  (this, 47));
        this.goalSelector.add(48, new PlantCropsGoal    (this, 48));
        this.goalSelector.add(49, new HoeFarmLandGoal   (this, 49));
        this.goalSelector.add(50, new AmaziaGoToFarmGoal(this, 50));
        
        super.registerBaseGoals();
    }
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.farmer.walk", true));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.farmer.idle", true));
        return PlayState.CONTINUE;
    }

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<FarmerEntity>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
	
	// FARMER SPECIFIC
	
	@Override
	public boolean canHoe() {
		return true;
	}
	
	@Override
	public boolean canPlant() {
		return this.getInventory().containsAny(AmaziaData.CLASSIC_PLANTABLES);
	}
	
	@Override
	public boolean canHarvest() {
		return this.hasFreeSlot();
	}
	
	@Override
	public boolean canDepositItems() {
		return this.getEmptyInventorySlots() == 0 && this.getDepositableItems() != null;
	}
	
	@Override
	public Triplet<ItemStack, Integer, Integer> getDepositableItems() {
		return this.getDepositableItems(USABLE_ITEMS, REQUIRED_ITEMS);
	}
	
	private void plant(ServerWorld world, BlockPos pos) {
		BlockState blockState2;
		SimpleInventory simpleInventory = this.getInventory();
        for (int i = 0; i < simpleInventory.size(); ++i) {
            ItemStack itemStack = simpleInventory.getStack(i);
            boolean bl = false;
            if (!itemStack.isEmpty()) {
                if (itemStack.isOf(Items.WHEAT_SEEDS)) {
                	blockState2 = Blocks.WHEAT.getDefaultState();
                    world.setBlockState(pos, blockState2);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(this, blockState2));
                    bl = true;
                } else if (itemStack.isOf(Items.POTATO)) {
                    blockState2 = Blocks.POTATOES.getDefaultState();
                    world.setBlockState(pos, blockState2);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(this, blockState2));
                    bl = true;
                } else if (itemStack.isOf(Items.CARROT)) {
                    blockState2 = Blocks.CARROTS.getDefaultState();
                    world.setBlockState(pos, blockState2);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(this, blockState2));
                    bl = true;
                } else if (itemStack.isOf(Items.BEETROOT_SEEDS)) {
                    blockState2 = Blocks.BEETROOTS.getDefaultState();
                    world.setBlockState(pos, blockState2);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(this, blockState2));
                    bl = true;
                }
            }
            if (!bl) continue;
            world.playSound(null, (double)pos.getX(), (double)pos.getY(), pos.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            itemStack.decrement(1);
            if (!itemStack.isEmpty()) break;
            simpleInventory.setStack(i, ItemStack.EMPTY);
            break;
        }
	}
	private void harvest(ServerWorld world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof CropBlock crop) {
			if (crop.isMature(state)) {
				world.breakBlock(pos, true);
			}
		}
		if (state.getBlock() instanceof SugarCaneBlock crop) {
			world.breakBlock(pos, true);
		}
	}

	public void plant() {
		this.plant((ServerWorld)world, new BlockPos(this.getPos()).up());
	}

	public void harvest() {
		this.harvest((ServerWorld)world, new BlockPos(this.getPos()).up());
	}
}
