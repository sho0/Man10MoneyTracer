package red.man10.man10moneytracer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sho on 2017/08/23.
 */
public class Man10MoneyTracerItem {
    public long id;
    public String containerId;
    public String name;
    public UUID uuid;
    public String containerType;
    public int slot;
    public Material type;
    public long damage;
    public String displayName;
    public long amount;
    public List<String> lore;
    public List<ItemFlag> flags;
    public List<Enchantment> enchantments;
    public long value;
    public String dateTime;
    public long time;

    public String world;
    public double x;
    public double y;
    public double z;

    public long totalCount;

    public Man10MoneyTracerItem(long id,String containerId,String name,UUID uuid,String containerType,int slot,Material material,long damage,String displayName,long amount,String lore,String itemFlag,String enchant,long value,String date,long time,String world,double x,double y,double z,long totalCount){
        this.id = id;
        this.totalCount = totalCount;
        this.containerId = containerId;
        this.name = name;
        this.uuid = uuid;
        this.containerType = containerType;
        this.slot = slot;
        this.type = material;
        this.damage = damage;
        this.displayName = displayName;
        this.amount = amount;
        List<String> lores = new ArrayList<>();
        String[] loreList = lore.split("\n");
        for(int i = 0;i < loreList.length;i++){
            lores.add(loreList[i]);
        }
        this.lore = lores;

        List<Enchantment> enchants = new ArrayList<>();
        String[] enchantList = enchant.split("\n");
        for(int i = 0;i < enchantList.length;i++){
            enchants.add(Enchantment.getByName(enchantList[i]));
        }
        this.enchantments = enchants;

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;

        this.value = value;
        this.dateTime = date;
        this.time = time;
    }
}
