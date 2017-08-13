package red.man10.man10moneytracer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        Block b = e.getBlock();
        if(b.getType() == Material.DISPENSER){
            Location l = b.getLocation();
            plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" +  l.getWorld().getName() + "' and x ='" + l.getX() + "' and y ='" + l.getY() + "' and z ='" + l.getZ() + "'");
        }else if (b.getType() == Material.DROPPER) {
            Location l = b.getLocation();
            plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" + l.getWorld().getName() + "' and x ='" + l.getX() + "' and y ='" + l.getY() + "' and z ='" + l.getZ() + "'");
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Bukkit.broadcastMessage(e.getInventory().getType().name());
        InventoryHolder inv = e.getInventory().getHolder();
        if(inv instanceof DoubleChest){
            Location l = ((DoubleChest)inv).getLocation();
            plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"DoubleChest",plugin.manager.containerIdGenerator("DC",l));
        }else if(inv instanceof Chest){
            Location l = ((Chest)inv).getLocation();
            plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Chest",plugin.manager.containerIdGenerator("C",l));
        }else if(e.getInventory().getType() == InventoryType.ENDER_CHEST){
            Location l = e.getPlayer().getLocation();
            plugin.manager.createLog(e.getPlayer().getEnderChest(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Ender",plugin.manager.containerIdGenerator("E",l));
        }else if (e.getInventory().getType() == InventoryType.CRAFTING){
            Location l = e.getPlayer().getLocation();
            plugin.manager.createLog(e.getPlayer().getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Player",plugin.manager.containerIdGenerator("P",l));
        }else if(inv instanceof Dispenser){
            Location l = ((Dispenser) inv).getLocation();
            plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Dispenser",plugin.manager.containerIdGenerator("D",l));
        }else if(inv instanceof Dropper){
            Location l = ((Dropper) inv).getLocation();
            plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Dropper",plugin.manager.containerIdGenerator("DR",l));
        }else if(inv instanceof ShulkerBox){
            Location l = ((ShulkerBox) inv).getLocation();
            plugin.manager.createLog(e.getInventory(),l,e.getPlayer().getName(),e.getPlayer().getUniqueId(),"Shulker",plugin.manager.containerIdGenerator("S",l));
        }
    }

}
