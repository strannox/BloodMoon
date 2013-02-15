package uk.co.jacekk.bukkit.bloodmoon.entity;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftLivingEntity;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

import uk.co.jacekk.bukkit.baseplugin.v9_1.config.PluginConfig;
import uk.co.jacekk.bukkit.bloodmoon.BloodMoon;
import uk.co.jacekk.bukkit.bloodmoon.Config;

import net.minecraft.server.v1_4_R1.EntityMonster;

public abstract class BloodMoonEntityMonster {
	
	protected BloodMoon plugin;
	protected EntityMonster nmsEntity;
	protected CraftLivingEntity bukkitEntity;
	protected BloodMoonEntityType type;
	
	protected Random rand;
	
	public BloodMoonEntityMonster(BloodMoon plugin, EntityMonster nmsEntity, CraftLivingEntity bukkitEntity, BloodMoonEntityType type){
		this.plugin = plugin;
		this.nmsEntity = nmsEntity;
		this.bukkitEntity = bukkitEntity;
		this.type = type;
		
		this.rand = new Random();
	}
	
	protected Block getBreakableTargetBlock(){
		Location direction = nmsEntity.target.getBukkitEntity().getLocation().subtract(bukkitEntity.getLocation());
		
		double dx = direction.getX();
		double dz = direction.getY();
		
		int bdx = 0;
		int bdz = 0;
		
		if (Math.abs(dx) > Math.abs(dz)){
			bdx = (dx > 0) ? 1 : -1;
		}else{
			bdz = (dx > 0) ? 1 : -1;
		}
		
		return nmsEntity.world.getWorld().getBlockAt((int) Math.floor(nmsEntity.locX + bdx), (int) Math.floor(nmsEntity.locY), (int) Math.floor(nmsEntity.locZ + bdz));
	}
	
	protected void attemptBreakBlock(PluginConfig worldConfig, Block block){
		Material type = block.getType();
		
		if (type != Material.AIR && worldConfig.getStringList(Config.FEATURE_BREAK_BLOCKS_BLOCKS).contains(type.name())){
			Location location = block.getLocation();
			
			if (this.rand.nextInt(100) < 80){
				nmsEntity.world.getWorld().playEffect(location, Effect.ZOMBIE_CHEW_WOODEN_DOOR, 0);
			}else{
				EntityChangeBlockEvent event = new EntityChangeBlockEvent(bukkitEntity, block, Material.AIR, (byte) 0);
				plugin.pluginManager.callEvent(event);
				
				if (!event.isCancelled()){
					nmsEntity.world.getWorld().playEffect(location, Effect.ZOMBIE_DESTROY_DOOR, 0);
					
					if (worldConfig.getBoolean(Config.FEATURE_BREAK_BLOCKS_REALISTIC_DROP)){
						block.breakNaturally();
					}else{
						block.setType(Material.AIR);
						
						if (worldConfig.getBoolean(Config.FEATURE_BREAK_BLOCKS_DROP_ITEMS)){
							nmsEntity.world.getWorld().dropItemNaturally(location, new ItemStack(type, 1, block.getData()));
						}
					}
				}
			}
		}
	}
	
	public abstract void onTick();
	
}