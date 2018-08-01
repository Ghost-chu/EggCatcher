/*
EggCatcher
Copyright (C) 2012, 2013  me@shansen.me

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package me.shansen.EggCatcher.listeners;

import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.EggCatcherLogger;
import me.shansen.EggCatcher.EggType;
import me.shansen.EggCatcher.events.EggCaptureEvent;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Locale;

public class EggCatcherEntityListener implements Listener {

    private boolean usePermissions;
    private boolean useCatchChance;
    private boolean useHealthPercentage;
    private boolean looseEggOnFail;
    private boolean useVaultCost;
    private boolean explosionEffect;
    private boolean smokeEffect;
    private boolean nonPlayerCatching;
    private boolean preventCatchingBabyAnimals;
    private boolean preventCatchingTamedAnimals;
    private boolean preventCatchingShearedSheeps;
    private String catchChanceSuccessMessage;
    private String catchChanceFailMessage;
    private String healthPercentageFailMessage;
    private String vaultTargetBankAccount;
    private boolean deleteVillagerInventoryOnCatch;
    private boolean logCaptures;
    private boolean debug;
    FileConfiguration config;
	private File captureLogFile;
	private EggCatcherLogger captureLogger;
	EggCatcher main;

	public void load() {
        this.config = main.getConfig();
        this.usePermissions = this.config.getBoolean("UsePermissions", true);
        this.useCatchChance = this.config.getBoolean("UseCatchChance", true);
        this.useHealthPercentage = this.config.getBoolean("UseHealthPercentage", false);
        this.looseEggOnFail = this.config.getBoolean("LooseEggOnFail", true);
        this.useVaultCost = this.config.getBoolean("UseVaultCost", false);
        //this.useItemCost = this.config.getBoolean("UseItemCost", false);
        this.explosionEffect = this.config.getBoolean("ExplosionEffect", true);
        this.smokeEffect = this.config.getBoolean("SmokeEffect", false);
        this.nonPlayerCatching = this.config.getBoolean("NonPlayerCatching", true);
        this.catchChanceSuccessMessage = this.config.getString("Messages.CatchChanceSuccess");
        this.catchChanceFailMessage = this.config.getString("Messages.CatchChanceFail");
        this.healthPercentageFailMessage = this.config.getString("Messages.HealthPercentageFail");
        this.preventCatchingBabyAnimals = this.config.getBoolean("PreventCatchingBabyAnimals", true);
        this.preventCatchingTamedAnimals = this.config.getBoolean("PreventCatchingTamedAnimals", true);
        this.preventCatchingShearedSheeps = this.config.getBoolean("PreventCatchingShearedSheeps", true);
        //this.spawnChickenOnFail = this.config.getBoolean("SpawnChickenOnFail", true);
        //this.spawnChickenOnSuccess = this.config.getBoolean("SpawnChickenOnSuccess", false);
        this.vaultTargetBankAccount = this.config.getString("VaultTargetBankAccount", "");
        this.deleteVillagerInventoryOnCatch = this.config.getBoolean("DeleteVillagerInventoryOnCatch", false);
        this.logCaptures = this.config.getBoolean("LogEggCaptures", false);
		this.captureLogFile = new File(main.getDataFolder(), "captures.txt");
		this.captureLogger = new EggCatcherLogger(captureLogFile);
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityHitByEgg(EntityDamageEvent event) {
        EntityDamageByEntityEvent damageEvent = null;
        Egg egg = null;
        EggType eggType = null;
        double vaultCost = 0.0;
        Entity entity = event.getEntity();

        if (!(event instanceof EntityDamageByEntityEvent)) {
        	if(debug) {main.getLogger().info("Not a EntityDamageByEntityEvent");}
            return;
        }

        damageEvent = (EntityDamageByEntityEvent) event;

        if (!(damageEvent.getDamager() instanceof Egg)) {
        	if(debug) {main.getLogger().info("Not a Egg damage");}
        	return;
        }

        egg = (Egg) damageEvent.getDamager();
        eggType = EggType.getEggType(entity);
        if (eggType == null) {
        	if(debug) {main.getLogger().info("eggType == null");}
        	return;
        }
        if (this.preventCatchingBabyAnimals) {
            if (entity instanceof Ageable) {
                if (!((Ageable) entity).isAdult()) {
                	if(debug) {main.getLogger().info("Not adult");}
                    return;
                }
            }
        }

        if (this.preventCatchingTamedAnimals) {
            if (entity instanceof Tameable) {
                if (((Tameable) entity).isTamed()) {
                	if(debug) {main.getLogger().info("Not tamed");}
                    return;
                }
            }
        }

        if (this.preventCatchingShearedSheeps) {
            if (entity instanceof Sheep) {
                if (((Sheep) entity).isSheared()) {
                	if(debug) {main.getLogger().info("Sheared sheep");}
                    return;
                }
            }
        }
        EggCaptureEvent eggCaptureEvent = new EggCaptureEvent(entity, egg);
        main.getServer().getPluginManager().callEvent(eggCaptureEvent);
        if (eggCaptureEvent.isCancelled()) {
        	if(debug) {main.getLogger().info("Canceled");}
            return;
        }

        if (egg.getShooter() instanceof Player) {
            Player player = (Player) egg.getShooter();
            if (this.usePermissions) {
                if (!player.hasPermission("eggcatcher.catch." + eggType.getFriendlyName().toLowerCase()) && !player.hasPermission("eggcatcher.catch." + eggType.name().toLowerCase())) {
                    player.sendMessage(config.getString("Messages.PermissionFail"));
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(Material.EGG, 1));  
                    }
                    if(debug) {main.getLogger().info("no permission");}
                    return;
                }
            }
            if (this.useHealthPercentage) {
                double healthPercentage = config.getDouble("HealthPercentage." + eggType.getFriendlyName());
	  	  		AttributeInstance MaxHealth = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH);
	  	  		Double getMaxhealth = MaxHealth.getBaseValue();
                double currentHealth = ((LivingEntity) entity).getHealth() * 100.0 / getMaxhealth;
                if (healthPercentage < currentHealth) {
                    if (this.healthPercentageFailMessage.length() > 0) {
                        player.sendMessage(String.format(this.healthPercentageFailMessage, healthPercentage));
                    }
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    }
                    if(debug) {main.getLogger().info("Not low at Health in config");}
                    return;
                }
            }

            if (this.useCatchChance) {
                double catchChance = config.getDouble("CatchChance." + eggType.getFriendlyName());
                if (Math.random() * 100 <= catchChance) {
                    if (this.catchChanceSuccessMessage.length() > 0) {
                        player.sendMessage(catchChanceSuccessMessage);
                    }
                } else {
                    if (this.catchChanceFailMessage.length() > 0) {
                        player.sendMessage(this.catchChanceFailMessage);
                    }
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    }
                    if(debug) {main.getLogger().info("Not have enough RP");}
                    return;
                }
            }
            
            boolean freeCatch = player.hasPermission("eggcatcher.free");

            if (this.useVaultCost && !freeCatch) {
                vaultCost = config.getDouble("VaultCost." + eggType.getFriendlyName());
                if (!EggCatcher.economy.has(player, vaultCost)) {
                    player.sendMessage(String.format(config.getString("Messages.VaultFail"), vaultCost));
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    }
                    if(debug) {main.getLogger().info("Not enough money");}
                    return;
                } else {
                    EggCatcher.economy.withdrawPlayer(player, vaultCost);
                    if (!this.vaultTargetBankAccount.isEmpty()) {
                        EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
                    }
                    player.sendMessage(String.format(config.getString("Messages.VaultSuccess"), vaultCost));
                }
            }
            // Dispenser
            if (!this.nonPlayerCatching) {
            	if(debug) {main.getLogger().info("Not a Player");}
                return;
            }
            if (this.useCatchChance) {
                double catchChance = config.getDouble("CatchChance." + eggType.getFriendlyName());
                if (Math.random() * 100 > catchChance) {
                	if(debug) {main.getLogger().info("Not enough chance");}
                    return;
                }
            }
        }
         Material spawnegg = null;
         try {
        	 spawnegg = Material.matchMaterial(entity.getName().trim().replaceAll(" ", "_").toUpperCase(Locale.ROOT)+"_SPAWN_EGG");
		} catch (Exception e) {
			// TODO: handle exception
			spawnegg=null;
		}
         if(spawnegg==null) {
        	 if(debug) {main.getLogger().info("Material == null");}
        	 return;
         }
        entity.remove();
        if (this.explosionEffect) {
            entity.getWorld().createExplosion(entity.getLocation(), 0);
        }
        if (this.smokeEffect) {
            entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0);
        }
        ItemStack eggStack = new ItemStack(spawnegg, 1);
        String customName = ((LivingEntity) entity).getCustomName();
        if (customName != null) {
            // Entity had custom name
            ItemMeta meta = eggStack.getItemMeta();
            meta.setDisplayName(customName);
            eggStack.setItemMeta(meta);
        }

        if(entity instanceof Pig) {
            if(((Pig)entity).hasSaddle()) {
                entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.SADDLE, 1));
            }
        }

        if(entity instanceof Horse) {
            if(((ChestedHorse) entity).isCarryingChest()){
                entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.CHEST));
            }
        }

        if((entity instanceof Villager && !this.deleteVillagerInventoryOnCatch) ||
                (!(entity instanceof Villager) && entity instanceof InventoryHolder)) {

            ItemStack[] items = ((InventoryHolder) entity).getInventory().getContents();

            for(ItemStack itemStack : items) {
                if(itemStack!=null){
                    entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
                }
            }
        }

        entity.getWorld().dropItem(entity.getLocation(), eggStack);
        
        if (this.logCaptures){
			captureLogger.logToFile("Player " + ((Player) egg.getShooter()).getName() + " caught " + entity.getType() + " at X" + Math.round(entity.getLocation().getX()) + ",Y" + Math.round(entity.getLocation().getY()) + ",Z" + Math.round(entity.getLocation().getZ()));
        }
    }
}
