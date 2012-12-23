package uk.co.jacekk.bukkit.bloodmoon.feature;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import uk.co.jacekk.bukkit.baseplugin.v6.config.PluginConfig;
import uk.co.jacekk.bukkit.baseplugin.v6.event.BaseListener;
import uk.co.jacekk.bukkit.baseplugin.v6.util.ListUtils;
import uk.co.jacekk.bukkit.bloodmoon.BloodMoon;
import uk.co.jacekk.bukkit.bloodmoon.Config;
import uk.co.jacekk.bukkit.bloodmoon.event.BloodMoonEndEvent;
import uk.co.jacekk.bukkit.bloodmoon.event.BloodMoonStartEvent;

public class ZombieArmorListener extends BaseListener<BloodMoon> {

	private Random random;
	
	public ZombieArmorListener(BloodMoon plugin){
		super(plugin);
		
		this.random = new Random();
	}
	
	private void giveArmor(LivingEntity entity, PluginConfig worldConfig){
		String name = ListUtils.getRandom(worldConfig.getStringList(Config.FEATURE_ZOMBIE_ARMOR_ARMOR));
		
		if (Material.getMaterial(name + "_BOOTS") == null){
			plugin.log.warn(name + " is not a valid armor name");
			return;
		}
		
		EntityEquipment equipment = entity.getEquipment();
		
		equipment.setBoots(new ItemStack(Material.getMaterial(name + "_BOOTS")));
		equipment.setLeggings(new ItemStack(Material.getMaterial(name + "_LEGGINGS")));
		equipment.setChestplate(new ItemStack(Material.getMaterial(name + "_CHESTPLATE")));
		equipment.setHelmet(new ItemStack(Material.getMaterial(name + "_HELMET")));
		
		float dropChance = worldConfig.getInt(Config.FEATURE_ZOMBIE_ARMOR_DROP_CHANCE) / 100.0f;
		
		equipment.setBootsDropChance(dropChance);
		equipment.setLeggingsDropChance(dropChance);
		equipment.setChestplateDropChance(dropChance);
		equipment.setHelmetDropChance(dropChance);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onStart(BloodMoonStartEvent event){
		String worldName = event.getWorld().getName();
		PluginConfig worldConfig = plugin.getConfig(worldName);
		
		if (worldConfig.getBoolean(Config.FEATURE_ZOMBIE_ARMOR_ENABLED)){
			for (LivingEntity entity : event.getWorld().getLivingEntities()){
				if (entity.getType() == EntityType.ZOMBIE && this.random.nextInt(100) < worldConfig.getInt(Config.FEATURE_ZOMBIE_WEAPON_CHANCE)){
					this.giveArmor(entity, worldConfig);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event){
		String worldName = event.getLocation().getWorld().getName();
		PluginConfig worldConfig = plugin.getConfig(worldName);
		
		if (plugin.isActive(worldName) && worldConfig.getBoolean(Config.FEATURE_ZOMBIE_ARMOR_ENABLED)){
			LivingEntity entity = event.getEntity();
			
			if (entity.getType() == EntityType.ZOMBIE && this.random.nextInt(100) < worldConfig.getInt(Config.FEATURE_ZOMBIE_WEAPON_CHANCE)){
				this.giveArmor(entity, worldConfig);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onStop(BloodMoonEndEvent event){
		String worldName = event.getWorld().getName();
		PluginConfig worldConfig = plugin.getConfig(worldName);
		
		if (worldConfig.getBoolean(Config.FEATURE_ZOMBIE_ARMOR_ENABLED)){
			for (LivingEntity entity : event.getWorld().getLivingEntities()){
				if (entity.getType() == EntityType.ZOMBIE){
					EntityEquipment equipment = entity.getEquipment();
					
					equipment.setBoots(null);
					equipment.setLeggings(null);
					equipment.setChestplate(null);
					equipment.setHelmet(null);
					
					equipment.setBootsDropChance(0.0f);
					equipment.setLeggingsDropChance(0.0f);
					equipment.setChestplateDropChance(0.0f);
					equipment.setHelmetDropChance(0.0f);
				}
			}
		}
	}
	
}
