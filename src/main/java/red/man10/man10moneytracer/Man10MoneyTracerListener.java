package red.man10.man10moneytracer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import red.man10.man10mysqlapi.ExecuteMySQLEvent;
import red.man10.man10mysqlapi.QueryExecutedEvent;

import java.util.Base64;

/**
 * Created by sho on 2017/08/12.
 */
public class Man10MoneyTracerListener implements Listener {
    private Man10MoneyTracer plugin;

    public Man10MoneyTracerListener(Man10MoneyTracer pluginn){
        plugin = pluginn;
    }



    @EventHandler
    public void onBreak(BlockBreakEvent  e){
        Runnable r = () -> {
            Block b = e.getBlock();
            if(b.getType() == Material.DISPENSER){
                Location l = b.getLocation();
                plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" +  l.getWorld().getName() + "' and x ='" + (int)l.getX() + "' and y ='" + (int)l.getY() + "' and z ='" + (int)l.getZ() + "'");
            }else if (b.getType() == Material.DROPPER) {
                Location l = b.getLocation();
                plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" + l.getWorld().getName() + "' and x ='" + (int)l.getX() + "' and y ='" + (int)l.getY() + "' and z ='" + (int)l.getZ() + "'");
            }else if(b.getType().name().contains("SHULKER_BOX")){
                Location l = b.getLocation();
                plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" + l.getWorld().getName() + "' and x ='" + (int)l.getX() + "' and y ='" +(int) l.getY() + "' and z ='" + (int)l.getZ() + "'");
            }else if(b.getType() == Material.CHEST || b.getType() == Material.TRAPPED_CHEST){
                if(((Chest)b.getState()).getInventory().getName().contains("§8Chest§m§a§n§1§0§h§o§n§e§y§c§h§e§s§t")){
                    return;
                }
                Location l = b.getLocation();
                plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" + l.getWorld().getName() + "' and x ='" + (int)l.getX() + "' and y ='" + (int)l.getY() + "' and z ='" + (int)l.getZ() + "'");
                if(plugin.manager.isLargeChest(b.getLocation())){
                    Location ll = b.getLocation();
                    plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" + ll.getWorld().getName() + "' and x ='" + (int)ll.getX() + "' and y ='" + (int)ll.getY() + "' and z ='" + (int)ll.getZ() + "'");
                    Location lll = plugin.manager.getLargeChestLocation(e.getBlock().getLocation());
                    plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" + lll.getWorld().getName() + "' and x ='" +(int) lll.getX() + "' and y ='" + (int)lll.getY() + "' and z ='" + (int)lll.getZ() + "'");
                    Block chest = lll.getBlock();
                    Chest cc = ((Chest) chest.getState());
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            plugin.manager.createLog(cc.getInventory(),lll,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Chest",plugin.manager.containerIdGenerator("C",lll));
                        }
                    }.runTaskLater(plugin,2);
                }
            }
        };
        plugin.executorService.submit(r);
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Runnable r = () -> {
            Block b = e.getBlock();
            if(b.getType() == Material.CHEST  || b.getType() == Material.TRAPPED_CHEST) {
                if(plugin.manager.isLargeChest(b.getLocation())) {
                    Location l = plugin.manager.getLargeChestLocation(b.getLocation());
                    plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" + l.getWorld().getName() + "' and x ='" + (int) l.getX() + "' and y ='" + (int) l.getY() + "' and z ='" + (int) l.getZ() + "'");
                    Location ll = e.getBlock().getLocation();
                    plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" + ll.getWorld().getName() + "' and x ='" + (int) ll.getX() + "' and y ='" + (int) ll.getY() + "' and z ='" + (int) ll.getZ() + "'");
                }
            }
        };
        plugin.executorService.submit(r);
    }

    void broadCastLocation(Location l){
        Bukkit.broadcastMessage("x:" + l.getX() + " y:" + l.getY() + " z:" + l.getZ());
    }


    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Runnable r = () -> {
            InventoryHolder inv = e.getInventory().getHolder();
            if(e.getInventory().getName().contains("§8Chest§m§a§n§1§0§h§o§n§e§y§c§h§e§s§t")){
                return;
            }
            if(inv instanceof DoubleChest){
                Location l = ((DoubleChest)inv).getLocation();
                plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"DoubleChest",plugin.manager.containerIdGenerator("DC",l));
                plugin.manager.createLog(e.getPlayer().getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Player",plugin.manager.containerIdGenerator("P",l));
            }else if(inv instanceof Chest){
                Location l = ((Chest)inv).getLocation();
                plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Chest",plugin.manager.containerIdGenerator("C",l));
                plugin.manager.createLog(e.getPlayer().getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Player",plugin.manager.containerIdGenerator("P",l));
            }else if(e.getInventory().getType().name().equals("ENDER_CHEST")){
                Location l = e.getPlayer().getLocation();
                plugin.manager.createLog(e.getPlayer().getEnderChest(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Ender",plugin.manager.containerIdGenerator("E",l));
                plugin.manager.createLog(e.getPlayer().getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Player",plugin.manager.containerIdGenerator("P",l));
            }else if (e.getInventory().getType() == InventoryType.CRAFTING){
                Location l = e.getPlayer().getLocation();
                plugin.manager.createLog(e.getPlayer().getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Player",plugin.manager.containerIdGenerator("P",l));
            }else if(inv instanceof Dispenser){
                Location l = ((Dispenser) inv).getLocation();
                plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Dispenser",plugin.manager.containerIdGenerator("D",l));
                plugin.manager.createLog(e.getPlayer().getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Player",plugin.manager.containerIdGenerator("P",l));
            }else if(inv instanceof Dropper){
                Location l = ((Dropper) inv).getLocation();
                plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Dropper",plugin.manager.containerIdGenerator("DR",l));
                plugin.manager.createLog(e.getPlayer().getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Player",plugin.manager.containerIdGenerator("P",l));
            }else if(inv instanceof ShulkerBox){
                Location l = ((ShulkerBox) inv).getLocation();
                plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Shulker",plugin.manager.containerIdGenerator("S",l));
                plugin.manager.createLog(e.getPlayer().getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Player",plugin.manager.containerIdGenerator("P",l));
            }else if(inv instanceof  Furnace){
                Location l = ((Furnace) inv).getLocation();
                plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Furnace",plugin.manager.containerIdGenerator("F",l));
                plugin.manager.createLog(e.getPlayer().getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Player",plugin.manager.containerIdGenerator("P",l));
            }
        };
        plugin.executorService.submit(r);
    }

}
