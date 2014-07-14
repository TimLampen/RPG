package me.timlampen.rpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;





import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class DropChance implements Listener{
	
	public Map<String, String> mob = new HashMap<String, String>();
	Random rad = new Random();
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			if(!mob.containsKey(event.getEntity().toString())){
			Player player = (Player) event.getDamager();
				mob.put(event.getEntity().toString(), player.getName());
		}
		}
	}
	public enum Items {
		Sword,
	}
	public ItemStack getCustomItem(Items item) {
		final int numluck = rad.nextInt(101);
		ItemStack is = null;
		ItemMeta im;
		ArrayList<String> Sword;
		switch (item) {
		case Sword:
			is = new ItemStack(Material.IRON_SWORD);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.RESET + " Iron Sword");
			Sword = new ArrayList<String>();
			Sword.add(ChatColor.GRAY + "You must be level: " + ChatColor.GOLD + numluck);
			im.setLore(Sword);
			is.setItemMeta(im);
		}
		return is;
	}
	@EventHandler
	public void onEDeath(EntityDeathEvent event){
		int first = rad.nextInt(11);
		if(event.getEntity().getKiller() instanceof Player){
			Player player = event.getEntity().getKiller();
			if(first==0){
				if(mob.containsKey(event.getEntity().toString())){
					mob.remove(event.getEntity().toString());
					if(event.getEntityType()==EntityType.CHICKEN || event.getEntityType()==EntityType.COW || event.getEntityType()==EntityType.PIG || event.getEntityType()==EntityType.SHEEP){
						event.setDroppedExp(0);
						player.setExp(player.getExp()+2f);
					}
					if(event.getEntityType()==EntityType.ZOMBIE){
						event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), getCustomItem(Items.Sword));
						event.setDroppedExp(0);
						player.setExp(player.getExp()+2f);
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		int exp = event.getPlayer().getLevel();
		if(player.getItemInHand()!=null && player.getItemInHand().getType()!=null && player.getItemInHand().getType()!=Material.AIR){
			if(player.getItemInHand().getType()==Material.IRON_SWORD){
				if(event.getAction()==Action.RIGHT_CLICK_AIR || event.getAction()==Action.RIGHT_CLICK_BLOCK){
					if(player.getItemInHand().getItemMeta()!=null && player.getItemInHand().getItemMeta().getLore()!=null){
						if(exp>=getMinValueFromLore(player.getItemInHand(), "level")){
							if(enchants(player.getItemInHand().getItemMeta().getDisplayName())!=null && enchantnum()!=0){
								player.getInventory().remove(player.getItemInHand());
								player.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
								player.getItemInHand().addEnchantment(enchants(player.getItemInHand().getItemMeta().getDisplayName()), enchantnum());
					}
				}
						else if(!(exp>=getMinValueFromLore(player.getItemInHand(), "level"))){
							player.sendMessage(ChatColor.YELLOW + "You do not have the required amount of levels!");
						}
	}
			}
			}
		}
	}
	public int list(Player player){
		int exp = player.getExpToLevel();
		int output = 0;
		for(int i = 0; i<exp; i++){
			output++;
		}
		return output;
	}
	public Enchantment enchants(String s){
		Enchantment output = null;
		int first = rad.nextInt(10) + 1;
		if(first>=9){//20% chance of enchantment
			if(s.contains("sword") || s.contains("Sword")){
				int second = rad.nextInt(10) + 1;
				if(second==1){//10% chance of sharpness
				output = Enchantment.DAMAGE_ALL;
				}
				if(second==2 || second==3 || second==4){//30% chance of bane
					output = Enchantment.DAMAGE_ARTHROPODS;
				}
				if(second==5 || second==6 || second==7){//30% chance of smite
					output = Enchantment.DAMAGE_UNDEAD;
				}
				if(second==8){//10% chance of fire
					output = Enchantment.FIRE_ASPECT;
				}
				if(second==9 || second==10){//20% chance of knockback
					output = Enchantment.KNOCKBACK;
				}
			}
		}
		else{//cancels task
			output = Enchantment.DURABILITY;
		}
		return output;
		
}
	public int enchantnum(){
		int output = 1;
		int first = rad.nextInt(10) + 1;
		if(first<=5){//50% chance of being lvl 1
			output = 1;
		}
		if(first>=8){//30% chance of being lvl 2
			output = 2;
		}
		if(first==6){//10% chance of being lvl 3
			output = 3;
		}
		if(first==7){//10% chance of being lvl 4
			output = 4;
		}
		return output;
	}
	public static int getMinValueFromLore(ItemStack item, String value) {
		 
		int returnVal = 0;
		ItemMeta meta = item.getItemMeta();
		try {
		List<String> lore = meta.getLore();
		if (lore != null) {
		for (int i = 0; i < lore.size(); i++) {
		if (lore.get(i).contains(value)) {
		String vals = lore.get(i).split(":")[1];
		vals = ChatColor.stripColor(vals);
		vals = vals.split("-")[0];
		returnVal = Integer.parseInt(vals.trim());
		}
		}
		}
		} catch (Exception e) {
			System.out.println(e);
		}
		return returnVal;
		}
	public static String getValueFromLore(List<String> par1, String par2) {
		 
		String retString = "";
		try {
		if (par1 != null) {
		for (int i = 0; i < par1.size(); i++) {
		if (par1.get(i).contains(par2)) {
		retString = cleanUpLore(par1.get(i));
		return retString;
		}
		}
		} else
		return retString;
		} catch (Exception e) {
		System.out.println(e);
		}
		return retString;
		}
		 
		private static String cleanUpLore(String par1) {
		 
		String[] arg = par1.split(":");
		arg[1] = ChatColor.stripColor(arg[1]);
		String str = arg[1].replace("%", "").trim().toString();
		return str;
		}
}
	
