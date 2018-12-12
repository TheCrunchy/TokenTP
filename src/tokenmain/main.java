package tokenmain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	Player senderinv, player, remove, playsender, playreciever;
	String psender, preciever;
	ItemStack token = new ItemStack(Material.PAPER);
	ItemMeta meta = token.getItemMeta();
	String type;
	Player message;
	int amount = 1;
    int removeamount = 1;
	Boolean hasperms = false;
	Map <String, String> tptype = new HashMap<String, String>();
	Map <String, Long> cooldown = new HashMap<String, Long>();
	Map <String, Long> sendercooldown = new HashMap<String, Long>();
	Map <String, String> currentRequest = new HashMap<String, String>();
	Map <String, String> currentTpaccept = new HashMap<String, String>();
	Map <String, Location> playermove = new HashMap<String, Location>();
    Plugin plugin = this;
	//Map <Player, Player> plsender = new HashMap<Player, Player>();
    public void onEnable() {
    	token = new ItemStack(Material.PAPER);
    	meta = token.getItemMeta();
    	tokenlore();
    	getCommand("tokenget").setExecutor(new tokengive());
    	getCommand("tpa").setExecutor(new tp());
    	getCommand("tpahere").setExecutor(new tp());
    	getCommand("tpaccept").setExecutor(new tpaccept());
    	getCommand("tpdeny").setExecutor(new tpdeny());
    	getCommand("tpa").setTabCompleter(new tabcomplete());
    	getCommand("tpahere").setTabCompleter(new tabcomplete());
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("Removing expired TP requests");
				//System.out.println(currentRequest);
				for (Map.Entry<String, String> entry : currentRequest.entrySet()) {
					//System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());

						String oldrequest = entry.getKey();
			    		if (cooldown.get(oldrequest) !=  null){
			    	        if((cooldown.get(oldrequest) + 60) <= (System.currentTimeMillis() / 1000)){
			    	        	//System.out.println(cooldown.get(preciever).toString());
			    	        	//System.out.println(System.currentTimeMillis() / 1000);
			    	        	String expiredpreciever = currentRequest.get(oldrequest);
			    	        	cooldown.remove(expiredpreciever);
			    	        	currentRequest.remove(oldrequest);
			    	        	currentTpaccept.remove(oldrequest);
			    	        	return;
			    	        }
			    	        else {
			    	        	System.out.println(currentRequest + " these havent expired");
			    	        	return;
			    	        }
			        		}  
						currentRequest.remove(oldrequest);
					}
					
				
			}
    		//Code
    		}, 1, 20 * 120); 
    }
    // Fired when plugin is disable
    
   
    @Override
    public void onDisable() {
    }
    
    
    public class tokengive implements CommandExecutor {
    @Override
    @EventHandler
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (sender instanceof Player) {
    		sender = (Player) sender;
    		if (sender.hasPermission("tokentp.get")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++){
            sb.append(args[i]).append(" ");
            }
            String newarg = sb.toString().trim();
            if (newarg.equals("")) {
            	newarg = "1";
            }
    	    amount = Integer.parseInt(newarg);
    		tokenlore();
    		token.setAmount(amount);
    		((Player) sender).getInventory().addItem(token);
    		sender.sendMessage(ChatColor.GOLD + "Tokens given");
    		}
    		else {
    			sender.sendMessage("Insufficient Permissions");
    		}
    	}
        return true;
    }

}
    public class tabcomplete implements TabExecutor {
        @EventHandler (priority = EventPriority.LOWEST)
        public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        	System.out.println("Hi, essentials can go fuck itself");
    		return null;
        }

    	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
    		// TODO Auto-generated method stub
        	System.out.println("Hi Zel, essentials can go fuck itself");
    		return false;
    	}
  }
    public class tpdeny implements CommandExecutor {
    @Override
    @EventHandler
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (sender instanceof Player) {
    		String checksender = sender.getName().toString(), checkplayer;
    		Player newplayer, oldplayer;
    		psender = sender.getName().toString();
    		oldplayer = (Player) sender;
    		if (currentRequest.containsKey(checksender)) {	
    			preciever = currentRequest.get(psender);
    			checkplayer = currentRequest.get(checksender);
    			newplayer = getServer().getPlayerExact(checkplayer);
    			newplayer.sendMessage(newplayer.getDisplayName() + ChatColor.RED + "Has denied the request.");
    			sender.sendMessage(ChatColor.RED + "Request denied");
	        	cooldown.remove(preciever);
	        	currentRequest.remove(psender);
	        	currentTpaccept.remove(psender);
    			//currentRequest.remove(key, checksender);
    		}
    		else 
    		{
    			oldplayer.sendMessage(ChatColor.RED + "No current teleport requests");
    			
    		}
    		
    	}
    	
        return true;
    }
    }
    
    
	private void tokenlore() {
		System.out.println("Creating token");
		
		meta.setDisplayName(ChatColor.GOLD + "☸ " + ChatColor.AQUA + "Teleport Token" + ChatColor.GOLD + " ☸");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.DARK_AQUA + "Every token allows for one");
		lore.add(ChatColor.DARK_AQUA + "use of /tpa or /tpahere.");
		lore.add("");
		lore.add(ChatColor.DARK_GREEN + "Tokens are obtained by mob drops,");
		lore.add(ChatColor.DARK_GREEN + "loot chests and voting.");
		meta.setLore(lore);	
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		token.setItemMeta(meta);
		//token.setAmount(amount);
		token.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		
	}
	private void removetoken() {
	//	System.out.println("Trying schematic");
		//playsender.sendMessage("Attempting to remove item");
		//System.out.print(playsender.toString());
		removeamount = 1;
		token.setAmount(removeamount);
	    playreciever.getInventory().removeItem(token);
		playreciever.updateInventory();
	}
    public class tp implements CommandExecutor {
    @SuppressWarnings("unlikely-arg-type")
	@Override
    @EventHandler
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (sender instanceof Player) {
    	psender = sender.getName().toString();
    	playsender = (Player) sender;
    	hasperms = false;
    		String commands = cmd.toString();
    		if (sendercooldown.get(psender) !=  null){
    			if (sender.isOp()) {
    				
    			}
    			else if((sendercooldown.get(psender) + 5) >= (System.currentTimeMillis() / 1000)){
    	        	sender.sendMessage(ChatColor.RED + " " + " Wait 5 seconds before sending another request.");
    	        	return true;
    	        }
    		}
    		try {
    			if (getServer().getPlayerExact(args[0]) != null) {
    				playreciever = getServer().getPlayerExact(args[0]);
    				preciever = playreciever.getName().toString();
    			}
    			else {
    				sender.sendMessage(ChatColor.RED + " Player not online.");
    				return true;
    			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
    			sender.sendMessage("/tpa <player>");	
				return true;
			}
			if (currentRequest.containsValue(psender)){
				if (currentRequest.get(preciever) == psender) {
					sender.sendMessage(ChatColor.RED + "You have already sent that player a request.");
					return true;
				}
				for (Map.Entry<String, String> entry : currentRequest.entrySet()) {
					System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
					if (entry.getValue().equals(psender)) {
						String oldrequest = entry.getKey();
						Player oldplayer = getServer().getPlayerExact(oldrequest);
						if (oldplayer == null)
						{
							currentRequest.remove(oldrequest);
							return true;
						}
						oldplayer.sendMessage(playsender.getDisplayName() + ChatColor.RED + " Cancelled their teleport request" );
						currentRequest.remove(oldrequest);
					}
					
				}
	        	cooldown.remove(psender);
	        	currentTpaccept.remove(preciever);
	        	currentRequest.remove(preciever, psender);
	        	//plsender.remove(preciever);
			}
			else {
			}
    		if (commands.contains("tpahere")) {
    			type = "tpahere";
    		}
    		else if (commands.contains("tpa")) {
    			type = "tpa";	
    		}	
    		String sendertest = playsender.getUniqueId().toString();
    		String recievertest = playreciever.getUniqueId().toString();
    		if (preciever == null) {
    			sender.sendMessage("/tpa <player>");
    			return true;
    		}
    		if (sender.hasPermission("tokentp.exempt")){
    			hasperms = true;
    		}
    		else {
    			hasperms = false;
    		}
    		if (hasperms == false) {
    				if (getServer().getPlayerExact(psender).getInventory().containsAtLeast(token, 1)) {
    					}
    			else 
    			{
    				sender.sendMessage(ChatColor.RED + " You require a teleport token.");
    				return true;
    			}
    		}
    		try {
				if (!recievertest.equals(sendertest)) {
		    		if (currentRequest.containsKey(preciever)) {
			        	Player newsender = (Player) getServer().getPlayerExact(currentRequest.get(preciever));
			        	if (newsender != playsender)
			        	{
			        	newsender.sendMessage(ChatColor.DARK_RED + "Another player requested to teleport to " + playreciever.getDisplayName() + ChatColor.DARK_RED + ", cancelling your request.");
		    		}}
					currentRequest.put(preciever, psender);
					//plsender.put(preciever, psender);
					cooldown.put(preciever, System.currentTimeMillis() / 1000);
					sendercooldown.put(psender, System.currentTimeMillis() / 1000);
					String sendername = playsender.getDisplayName();
					String recievername = playreciever.getDisplayName();
					if (type.equals("tpa")) {
					playreciever.sendMessage(sendername+ ChatColor.GOLD + " has requested to teleport to you.");
					}
					if (type.equals("tpahere")) {
					playreciever.sendMessage(sendername + ChatColor.GOLD + " has requested that you teleport to them.");
					}
					playreciever.sendMessage(ChatColor.GOLD + "To accept type" + ChatColor.RED + " /tpaccept");
					playreciever.sendMessage( ChatColor.GOLD +"To deny type" + ChatColor.RED +" /tpdeny"); 
					playreciever.sendMessage( ChatColor.GOLD +"This request will expire in" + ChatColor.RED +" 60 seconds."); 
					playsender.sendMessage(ChatColor.GOLD + "Request sent to " + recievername); 
					playsender.sendMessage(ChatColor.GOLD + "To cancel this request type" + ChatColor.RED + " /tpcancel"); 
					playsender.sendMessage( ChatColor.GOLD +"This request will expire in" + ChatColor.RED +" 60 seconds."); 
					tptype.put(preciever, type);
				}
				else {
					playsender.sendMessage(ChatColor.RED + "You cannot teleport to yourself.");
					return true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				playsender.sendMessage("Error: You cannot teleport to an offline player.");
				e.printStackTrace();
			}	
    		
    	}
    	
    
        return true;
    }


}

    public class tpaccept implements CommandExecutor {
    @SuppressWarnings({ "unlikely-arg-type", "deprecation" })
	@Override
    @EventHandler
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (sender instanceof Player) {
    		playsender = (Player) sender;
    		psender = sender.getName().toString();
    		
    		if (currentTpaccept.containsKey(psender)) {
	        	sender.sendMessage(ChatColor.GOLD + "You have already accepted that request!");	
	        	return true;
    		}
    		currentTpaccept.put(psender, "true");
    	//	System.out.println("Do they have a request");
    		if (!currentRequest.containsKey(psender)) {
	        	sender.sendMessage(ChatColor.GOLD + "No active teleport request.");
    			return true;
    		}
    		//System.out.println("get preciever");
    		preciever = currentRequest.get(psender);
    		if (preciever == null)
    		{
    			System.out.println("If you can see this TPA fucked up and got a null error");
    			return true;
    		}
    		//System.out.println("get player reciever");
    		playreciever = getServer().getPlayerExact(preciever);
    		//System.out.println("get type of tp");
    		type = tptype.get(psender);
    		if (playreciever == null)
    		{
    			//System.out.print(preciever);
    			//Bukkit.broadcastMessage("Fuck my life its null");
    			return true;
    		}
    		//System.out.print(preciever);
    		//System.out.println("is player reciever null");
    		if (playreciever == null)
    		{
	        	sender.sendMessage(ChatColor.GOLD + "No active teleport request.");
    			return true;
    		}
    		//System.out.println("is player sender null");
    		if (playsender == null)
    		{
	        	sender.sendMessage(ChatColor.GOLD + "No active teleport request.");
    			return true;
    		}
    		//System.out.println("is the cooldown null");
    		if (cooldown.get(psender) !=  null){
	        if((cooldown.get(psender) + 60) <= (System.currentTimeMillis() / 1000)){
	        	//System.out.println(cooldown.get(preciever).toString());
	        	//System.out.println(System.currentTimeMillis() / 1000);
	        	sender.sendMessage(ChatColor.RED + "Request expired.");
	        	cooldown.remove(preciever);
	        	currentRequest.remove(psender);
	        	currentTpaccept.remove(psender);
	        	return true;
	        }
    		}        
	        //System.out.println("get player reciever");
	        
	        	if (playreciever.hasPermission("tokentp.exempt"))
	        	{
					    if (type.equals("tpa")) {
						    playermove.put(preciever, playreciever.getLocation());
						    playreciever.sendMessage(ChatColor.GOLD + "Request accepted, teleporting in 5 seconds, dont move.");
						    sender.sendMessage(ChatColor.GOLD + "Request accepted, teleporting in 5 seconds.");
					    }
					    if (type.equals("tpahere")) {
						    playermove.put(psender, playsender.getLocation());
						    sender.sendMessage(ChatColor.GOLD + "Request accepted, teleporting in 5 seconds, dont move.");
						    playreciever.sendMessage(ChatColor.GOLD + "Request accepted, teleporting in 5 seconds.");
					    }
					    getServer().getScheduler().scheduleAsyncDelayedTask((Plugin) plugin, new Runnable() {		
					    	public void run() {
						    	//System.out.println("schedule worked")

					    		playsender = (Player) sender;
					    		psender = sender.getName().toString();
					    		preciever = currentRequest.get(psender);
					    		playreciever = getServer().getPlayerExact(preciever);
					    		if (playreciever == null)
					    		{
					    			playsender.sendMessage(ChatColor.DARK_RED + "Request cancelled");
					    			return;
					    		}
							    if (type.equals("tpa")) {
								    Location recieverloc = playermove.get(preciever);
								    if (recieverloc.getX() != playreciever.getLocation().getX())
								    {
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playreciever.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
							        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(preciever);
					    	        	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
								    if (recieverloc.getZ() != playreciever.getLocation().getZ())
								    {
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playreciever.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
							        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(preciever);
					    	        	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
								    if (recieverloc.getY() != playreciever.getLocation().getY())
								    {
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playreciever.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
							        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(preciever);
					    	        	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
							    }
							    if (type.equals("tpahere")) {
							    	Location senderloc = playermove.get(psender);
								    if (senderloc.getX() != playsender.getLocation().getX())
								    {
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playsender.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
					    	        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(psender);
					    	        	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
								    if (senderloc.getZ() != playsender.getLocation().getZ())
								    {
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playsender.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
					    	        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(psender);
					    	           	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
								    if (senderloc.getY() != playsender.getLocation().getY())
								    {
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playsender.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
					    	        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(psender);
					    	           	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
							    }

				        		if (type.equals("tpa")) {
				        			playreciever.teleport(playsender);
				    	        	cooldown.remove(preciever);
				    	        	currentRequest.remove(psender);
				    	        	currentTpaccept.remove(psender);			 
					  	        	playreciever.sendMessage(ChatColor.GOLD + "Teleporting to " + playsender.getDisplayName());
				    	        	playsender.sendMessage(ChatColor.GOLD + "Teleporting " + playreciever.getDisplayName() + ChatColor.GOLD + " to you");
					        		}		
					        		else if (type.equals("tpahere")){	
					        			playsender.teleport(playreciever);
					    	        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	   playsender.sendMessage(ChatColor.GOLD + "Teleporting to "+ playreciever.getDisplayName());
												    playreciever.sendMessage(ChatColor.GOLD + "Teleporting " + playsender.getDisplayName() + ChatColor.GOLD + " to you");
					        		}
					    	}
					    	}, 100L); 
	        	}
	        	  //System.out.println("Check players inventory for token");
	        	else if (playreciever.getInventory().containsAtLeast(token, 1)) {

					try {
						removetoken();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// System.out.println("Check players inventory for token");
						playsender.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " You require a teleport token.");
		     	    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled. " + ChatColor.RED + playsender.getDisplayName() + ChatColor.RED + " does not have a teleport token.");
						e.printStackTrace();
						return true;
					}
				    if (type.equals("tpa")) {
					    playermove.put(preciever, playreciever.getLocation());
					    playreciever.sendMessage(ChatColor.GOLD + "Request accepted, teleporting in 5 seconds, dont move.");
					    sender.sendMessage(ChatColor.GOLD + "Request accepted, teleporting in 5 seconds.");
				    }
				    if (type.equals("tpahere")) {
					    playermove.put(psender, playsender.getLocation());
					    sender.sendMessage(ChatColor.GOLD + "Request accepted, teleporting in 5 seconds, dont move.");
					    playreciever.sendMessage(ChatColor.GOLD + "Request accepted, teleporting in 5 seconds.");
				    }
					   // System.out.println("Scheduling task");
					    getServer().getScheduler().scheduleAsyncDelayedTask((Plugin) plugin, new Runnable() {		
					    	public void run() {
						    	//System.out.println("schedule worked");
					    		playsender = (Player) sender;
					    		psender = sender.getName().toString();
					    		preciever = currentRequest.get(psender);
					    		playreciever = getServer().getPlayerExact(preciever);
					    		if (playreciever == null)
					    		{
					    			playsender.sendMessage(ChatColor.DARK_RED + "Request cancelled");
					    			return;
					    		}
							    if (type.equals("tpa")) {
								    Location recieverloc = playermove.get(preciever);
								    if (recieverloc.getX() != playreciever.getLocation().getX())
								    {
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playreciever.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
							        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(preciever);
					    	        	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
								    if (recieverloc.getZ() != playreciever.getLocation().getZ())
								    {
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playreciever.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
							        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(preciever);
					    	           	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
								    if (recieverloc.getY() != playreciever.getLocation().getY())
								    {
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playreciever.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
							        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(preciever);
					    	        	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
							    }
							    if (type.equals("tpahere")) {
							    	Location senderloc = playermove.get(psender);
								    if (senderloc.getX() != playsender.getLocation().getX())
								    {
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playsender.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
					    	        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(psender);
					    	        	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
								    if (senderloc.getZ() != playsender.getLocation().getZ())
								    {
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playsender.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
					    	        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(psender);
					    	           	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
								    if (senderloc.getY() != playsender.getLocation().getY())
								    {
								    	playreciever.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, "+ playsender.getDisplayName() + ChatColor.DARK_RED + " moved");
								    	playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled, you moved");
					    	        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	playermove.remove(psender);
					    	           	amount = 1;
					    	        	tokenlore();
					    	        	if (!playreciever.hasPermission("tokentp.exempt"))
					    	        	{
					    	        	playreciever.getInventory().addItem(token);
					    	        	}
								    	return;
								    }
							    }
				        		if (type.equals("tpa")) {
				        			playreciever.teleport(playsender);
				    	        	cooldown.remove(preciever);
				    	        	currentRequest.remove(psender);
				    	        	currentTpaccept.remove(psender);
					  	        	playreciever.sendMessage(ChatColor.GOLD + "Teleporting to " + playsender.getDisplayName());
				    	        	playsender.sendMessage(ChatColor.GOLD + "Teleporting " + playreciever.getDisplayName() + ChatColor.GOLD + " to you");
					        		}		
					        		else if (type.equals("tpahere")){	
					        			playsender.teleport(playreciever);
					    	        	cooldown.remove(preciever);
					    	        	currentRequest.remove(psender);
					    	        	currentTpaccept.remove(psender);
					    	        	   playsender.sendMessage(ChatColor.GOLD + "Teleporting to "+ playreciever.getDisplayName());
										    playreciever.sendMessage(ChatColor.GOLD + "Teleporting " + playsender.getDisplayName() + ChatColor.GOLD + " to you");
					        		}
					    	}
					    	

	}, 100L); 
					    //Bukkit.broadcastMessage(psender.toString() +  " " + preciever.toString() + " " + type);
					   	
					}
	     	       else
	     	       {
	     	    	 
	     	    	   playreciever.sendMessage(ChatColor.DARK_RED + "Error:" + ChatColor.RED + " You require a teleport token.");
	     	    	   playsender.sendMessage(ChatColor.DARK_RED + "Teleporting cancelled. " + ChatColor.RED + playreciever.getDisplayName() + ChatColor.RED + " does not have a teleport token.");
	     	       }
			
	        }
    		
    	
        return true;
    }

}
    }