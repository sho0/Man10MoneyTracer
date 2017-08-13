package red.man10.man10moneytracer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sho on 2017/08/13.
 */
public class Man10MoneyTracerManager {
    private Man10MoneyTracer plugin;

    public Man10MoneyTracerManager(Man10MoneyTracer pluginn){
        plugin = pluginn;
    }

    void createLog(Inventory inv, Location l , String name, UUID UUID, String containerType, String id) {
        if(containerType.equals("Player")){
            plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE  final_editor_uuid ='" + UUID + "' and container_type ='Player'");
       }else if(containerType.equals("Ender")){
            plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE  final_editor_uuid ='" + UUID + "' and container_type ='Ender'");
        }else{
            plugin.mysql.execute("DELETE FROM man10_money_tracer WHERE world ='" + l.getWorld().getName() +"' and x='" + l.getX() + "' and y ='" + l.getY() + "' and z ='" + l.getZ() + "'");
        }
        String query = "";
        for(int i = 0;i < inv.getContents().length;i++){
            if(inv.getContents()[i] != null){

                ItemStack item = inv.getContents()[i];
                double value = getValue(item);
                if(item.getType().name().contains("SHULKER_BOX")){
                    BlockStateMeta bm = (BlockStateMeta)item.getItemMeta();
                    ShulkerBox s = (ShulkerBox) bm.getBlockState();
                    double d = 0;
                    for(int ii = 0;ii < s.getInventory().getContents().length;ii++){
                        if(s.getInventory().getContents()[i] != null){
                            d = d + getValue(s.getInventory().getContents()[i]);
                            Bukkit.broadcastMessage(s.getInventory().getContents()[i].getItemMeta().getDisplayName());
                        }
                    }
                    Bukkit.broadcastMessage(String.valueOf(d));
                    value = d;
                }
                query = query + "('0','" + id +"','" + name  +"','" + UUID +"','" + containerType +"','" + i +"','" + item.getType().name() +"','" + item.getDurability() +"','" + item.getItemMeta().getDisplayName() +"','" + item.getAmount() +"','" + getLore(item) +"','" + getEnchant(item) +"','" + getFlag(item) +"','" + value +"','" + l.getWorld().getName() +"','" + l.getX() +"','" + l.getY() +"','" + l.getZ() +"','" + currentTimeNoBracket() +"','" + System.currentTimeMillis()/1000 + "')";
                    query = query + ",";
            }
        }
        if(query.toCharArray().length == 0){
            return;
        }
        plugin.mysql.execute("INSERT INTO man10_money_tracer VALUES " + query.substring(0, query.length() - 1) + ";");
    }

    public String currentTimeNoBracket(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd' 'HH':'mm':'ss");
        return sdf.format(date);
    }

    String containerIdGenerator(String type,Location l){
        return type+"#"+l.getWorld().getName() + "#"+(int) l.getX() + "#"+(int) l.getY() + "#"+(int) l.getZ();
    }

    String getLore(ItemStack item){
        String lore = "";
        if(item.getItemMeta() == null || item.getItemMeta().getLore() == null){
            return "";
        }
        for(int i = 0;i < item.getItemMeta().getLore().size();i++){
            lore = lore + item.getItemMeta().getLore().get(i) + "\n";
        }
        return lore;
    }

    String getEnchant(ItemStack item){
        String lore = "";
        if(item.getItemMeta() == null || item.getItemMeta().getEnchants() == null){
            return "";
        }
        for(int i = 0;i < item.getItemMeta().getEnchants().size();i++){
            lore = lore + item.getItemMeta().getEnchants().get(i) + "\n";
        }
        return lore;
    }

    String getFlag(ItemStack item){
        String lore = "";
        if(item.getItemMeta() == null || item.getItemMeta().getItemFlags() == null){
            return "";
        }
        for(int i = 0;i < item.getItemMeta().getItemFlags().size();i++){
            lore = lore + item.getItemMeta().getItemFlags().toArray()[i] + "\n";
        }
        return lore;
    }

    double getValue(ItemStack item){
        return plugin.atmapi.getPrice(item) * item.getAmount();
    }

}
