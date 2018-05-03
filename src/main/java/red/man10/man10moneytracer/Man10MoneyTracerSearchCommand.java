package red.man10.man10moneytracer;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sho on 2017/08/22.
 */
public class Man10MoneyTracerSearchCommand implements CommandExecutor {
    private Man10MoneyTracer plugin;

    public Man10MoneyTracerSearchCommand(Man10MoneyTracer pluginn){
        plugin = pluginn;
    }

    String prefix = "§6[§eMan10Search§6]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player == false){
            sender.sendMessage(prefix + "このコマンドはプレイヤーからのみ実行できます");
            return false;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("man10.moneytracer.search")){
            p.sendMessage(prefix  +"あなたには権限がありません");
            return false;
        }
        UUID uuid = p.getUniqueId();
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){
                help(p);
            }
            return false;
        }
        if(args.length > 1){
            if(args[0].equalsIgnoreCase("query")){
                Runnable r = () -> {
                    String query = "SELECT *,sum(amount) as Totalamount FROM man10_money_tracer";
                    for(int i = 1;i < args.length;i++){
                        query +=  " " + args[i];
                    }
                    ArrayList<Man10MoneyTracerItem> item = man10MoneyTracerItemListQuery(query);
                    plugin.playerSearchHashmap.put(uuid,item);
                    plugin.playerSearchTotalHashMap.put(uuid,getTotalAmoutOfItemQuery(query));
                    Bukkit.dispatchCommand(sender,"msearch preview 0");
                };
                plugin.executorService.submit(r);
                return false;
            }
        }
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("preview")){
                Runnable r = () -> {
                    try {
                        ArrayList<Man10MoneyTracerItem> itemList = (ArrayList<Man10MoneyTracerItem>) plugin.playerSearchHashmap.get(uuid);
                        int page = 0;
                        page = Integer.parseInt(args[1]);
                        int untill =  10 * page + 10;
                        int totalPages = 0;
                        totalPages = (itemList.size()/10);
                        if(untill > itemList.size()){
                            untill = itemList.size();
                        }
                        p.sendMessage("§e表示している検索結果" + (page + 1) + "/" +( totalPages + 1) + "ページ §6サーバーの保有総数:" + plugin.playerSearchTotalHashMap.get(uuid));
                        for(int i = 10 * page;i < untill;i++){
                            int num = i + 1;
                            playJsonMessage(p, "§2" + num + "." + " " + cCT(itemList.get(i).containerType) + " §e" +  itemList.get(i).name +"§6|" + itemList.get(i).totalCount + "§6|§f",createLore(itemList.get(i)),itemList.get(i),cCT(itemList.get(i).containerType));
                        }
                        int prev = page - 1;
                        int next = page + 1;
                        if(prev == -1){
                            prev = 0;
                        }
                        if(next == totalPages + 1){
                            next = totalPages;
                        }
                        controlMenuMessage(p,prev,next);
                    }catch (NumberFormatException e){
                        sender.sendMessage(prefix + "ページは数字のみです");
                    }
                };
                plugin.executorService.submit(r);
                return false;
            }



            if(args[0].equalsIgnoreCase("name")){
                Runnable r = () -> {
                    ArrayList<Man10MoneyTracerItem> item = man10MoneyTracerItemList(escape(args[1]));
                    plugin.playerSearchHashmap.put(uuid,item);
                    plugin.playerSearchTotalHashMap.put(uuid,getTotalAmoutOfItemName(escape(args[1])));
                    Bukkit.dispatchCommand(sender,"msearch preview 0");
                };
                plugin.executorService.submit(r);
                return false;
            }
            if(args[0].equalsIgnoreCase("lore")){
                Runnable r = () -> {
                    ArrayList<Man10MoneyTracerItem> item = man10MoneyTracerItemListLore(escape(args[1]));
                    plugin.playerSearchHashmap.put(uuid,item);
                    plugin.playerSearchTotalHashMap.put(uuid,getTotalAmoutOfItemLore(escape(args[1])));
                    Bukkit.dispatchCommand(sender,"msearch preview 0");
                };
                plugin.executorService.submit(r);
                return false;
            }
            if(args[0].equalsIgnoreCase("type")){
                Runnable r = () -> {
                    ArrayList<Man10MoneyTracerItem> item = man10MoneyTracerItemListType(escape(args[1]));
                    plugin.playerSearchHashmap.put(uuid,item);
                    plugin.playerSearchTotalHashMap.put(uuid,getTotalAmoutOfItemType(escape(args[1])));
                    Bukkit.dispatchCommand(sender,"msearch preview 0");
                };
                plugin.executorService.submit(r);
                return false;
            }
        }
        help(p);
        return false;
    }

    public void help(Player p){
        p.sendMessage("§6==========" + prefix + "=========");
        p.sendMessage("§d/msearch help ヘルプ表示");
        p.sendMessage("§d/msearch name <name> アイテム名検索");
        p.sendMessage("§d/msearch lore <lore> 説明文検索");
        p.sendMessage("§d/msearch type <type> アイテム種検索");
        p.sendMessage("§6===============================");
        p.sendMessage("§d§lCreated By Sho0");
    }



    public String createLore(Man10MoneyTracerItem item){
        String message = "§d==========§e[§c検索結果§e]§d==========\n" +
                "§6所有者名:§e" + item.name + "\n" +
                "§6倉庫種:§e" + item.containerType + "\n" +
                "§6アイテム名:§e"  + item.displayName + "\n" +
                "§6総アイテム数:§e" + item.totalCount +"\n" +
                "§6アイテム種:§e" + item.type + "\n" ;
        message +=  "§6==アイテムロア===\n";
        for(int i = 0;i < item.lore.size();i++){
            message += "§f" + item.lore.get(i) + "\n";
        }
        String loc =                 "§6===保管位置===\n" +
                "" + "§6World:§e" + item.world + "\n" +
                "§6x:§e" + item.x + "\n" +
                "§6y:§e" + item.y + "\n" +
                "§6z:§e" + item.z;
        message += loc;
        return message;
    }

    public String escape(String s){
        return s.replaceAll("&","§").replaceAll("'", "\''").replaceAll("\"","\"");
    }


    public long getTotalAmoutOfItemQuery(String name){
        ResultSet rs = plugin.mysql.query(name);
        long a = 0;
        try {
            while (rs.next()){
                a = rs.getLong("Totalamount");
            }
            rs.close();
            plugin.mysql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }

    public ArrayList<Man10MoneyTracerItem> man10MoneyTracerItemListQuery(String lroe){
        ResultSet rs = plugin.mysql.query(lroe);
        ArrayList<Man10MoneyTracerItem> list = new ArrayList<>();
        try {
            while (rs.next()){
                Man10MoneyTracerItem item = new Man10MoneyTracerItem(rs.getLong("id"),rs.getString("container_id"),rs.getString("final_editor_name"),UUID.fromString(rs.getString("final_editor_uuid")),rs.getString("container_type"),rs.getInt("slot"), Material.getMaterial(rs.getString("item")),rs.getLong("damage"),rs.getString("display_name"),rs.getLong("amount"),rs.getString("lore"),rs.getString("flag"),rs.getString("enchant"),rs.getLong("value"),rs.getString("date_time"),rs.getLong("time"),rs.getString("world"),rs.getDouble("x"),rs.getDouble("y"),rs.getDouble("z"),rs.getLong("Totalamount"));
                list.add(item);
            }
            rs.close();
            plugin.mysql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public long getTotalAmoutOfItemType(String name){
        ResultSet rs = plugin.mysql.query("SELECT sum(amount) as Totalamount FROM man10_money_tracer WHERE item like '%" + name + "%'");
        long a = 0;
        try {
            while (rs.next()){
                a = rs.getLong("Totalamount");
            }
            rs.close();
            plugin.mysql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }

    public ArrayList<Man10MoneyTracerItem> man10MoneyTracerItemListType(String lroe){
        ResultSet rs = plugin.mysql.query("SELECT *,sum(amount) as Totalamount FROM man10_money_tracer WHERE item like '%" + lroe + "%' GROUP BY container_id ORDER BY Totalamount DESC ");
        ArrayList<Man10MoneyTracerItem> list = new ArrayList<>();
        try {
            while (rs.next()){
                Man10MoneyTracerItem item = new Man10MoneyTracerItem(rs.getLong("id"),rs.getString("container_id"),rs.getString("final_editor_name"),UUID.fromString(rs.getString("final_editor_uuid")),rs.getString("container_type"),rs.getInt("slot"), Material.getMaterial(rs.getString("item")),rs.getLong("damage"),rs.getString("display_name"),rs.getLong("amount"),rs.getString("lore"),rs.getString("flag"),rs.getString("enchant"),rs.getLong("value"),rs.getString("date_time"),rs.getLong("time"),rs.getString("world"),rs.getDouble("x"),rs.getDouble("y"),rs.getDouble("z"),rs.getLong("Totalamount"));
                list.add(item);
            }
            rs.close();
            plugin.mysql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public long getTotalAmoutOfItemLore(String name){
        ResultSet rs = plugin.mysql.query("SELECT sum(amount) as Totalamount FROM man10_money_tracer WHERE lore like '%" + name + "%'");
        long a = 0;
        try {
            while (rs.next()){
                a = rs.getLong("Totalamount");
            }
            rs.close();
            plugin.mysql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }

    public ArrayList<Man10MoneyTracerItem> man10MoneyTracerItemListLore(String lroe){
        ResultSet rs = plugin.mysql.query("SELECT *,sum(amount) as Totalamount FROM man10_money_tracer WHERE lore like '%" + lroe + "%' GROUP BY container_id ORDER BY Totalamount DESC ");
        ArrayList<Man10MoneyTracerItem> list = new ArrayList<>();
        try {
            while (rs.next()){
                Man10MoneyTracerItem item = new Man10MoneyTracerItem(rs.getLong("id"),rs.getString("container_id"),rs.getString("final_editor_name"),UUID.fromString(rs.getString("final_editor_uuid")),rs.getString("container_type"),rs.getInt("slot"), Material.getMaterial(rs.getString("item")),rs.getLong("damage"),rs.getString("display_name"),rs.getLong("amount"),rs.getString("lore"),rs.getString("flag"),rs.getString("enchant"),rs.getLong("value"),rs.getString("date_time"),rs.getLong("time"),rs.getString("world"),rs.getDouble("x"),rs.getDouble("y"),rs.getDouble("z"),rs.getLong("Totalamount"));
                list.add(item);
            }
            rs.close();
            plugin.mysql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public long getTotalAmoutOfItemName(String name){
        ResultSet rs = plugin.mysql.query("SELECT sum(amount) as Totalamount FROM man10_money_tracer WHERE display_name like '%" + name + "%'");
        long a = 0;
        try {
            while (rs.next()){
                a = rs.getLong("Totalamount");
            }
            rs.close();
            plugin.mysql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }

    public ArrayList<Man10MoneyTracerItem> man10MoneyTracerItemList(String name){
        ResultSet rs = plugin.mysql.query("SELECT *,sum(amount) as Totalamount FROM man10_money_tracer WHERE display_name like '%" + name + "%' GROUP BY container_id ORDER BY Totalamount DESC ");
        ArrayList<Man10MoneyTracerItem> list = new ArrayList<>();
        try {
            while (rs.next()){
                Man10MoneyTracerItem item = new Man10MoneyTracerItem(rs.getLong("id"),rs.getString("container_id"),rs.getString("final_editor_name"),UUID.fromString(rs.getString("final_editor_uuid")),rs.getString("container_type"),rs.getInt("slot"), Material.getMaterial(rs.getString("item")),rs.getLong("damage"),rs.getString("display_name"),rs.getLong("amount"),rs.getString("lore"),rs.getString("flag"),rs.getString("enchant"),rs.getLong("value"),rs.getString("date_time"),rs.getLong("time"),rs.getString("world"),rs.getDouble("x"),rs.getDouble("y"),rs.getDouble("z"),rs.getLong("Totalamount"));
                list.add(item);
            }
            rs.close();
            plugin.mysql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void playJsonMessage(Player player,String main,String messageWhenHovered,Man10MoneyTracerItem item,String cCt){
        String commandWhenClicked = createMTPCommand(item);
        if(cCt.equals("§7Inv")){
            commandWhenClicked = "/cmi inv " + item.name;
        }
        if(cCt.equals("§5End")){
            commandWhenClicked = "/cmi ender " + item.name;
        }
        PacketPlayOutChat iChatBaseComponent = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + main + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + commandWhenClicked + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\""+ messageWhenHovered +"\"}}"));
        CraftPlayer p  = ((CraftPlayer)player);
        //p.getHandle().playerConnection.networkManager.sendPacket(iChatBaseComponent);
        sendPacket(player,iChatBaseComponent);

    }

    public void controlMenuMessage(Player player,int prev,int next){
        PacketPlayOutChat iChatBaseComponent = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("[\"\",{\"text\":\"<<Prev Page \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/msearch preview " + prev + "\"}},{\"text\":\"||\"},{\"text\":\" Next Page>>\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/msearch preview " + next + "\"}}]"));
        CraftPlayer p  = ((CraftPlayer)player);
        //p.getHandle().playerConnection.networkManager.sendPacket(iChatBaseComponent);
        sendPacket(player,iChatBaseComponent);
    }



    public String createMTPCommand(Man10MoneyTracerItem l){
        String string = "/mtp " + l.world + " " + l.x + " " + l.y + " " + l.z;
        return string;
    }

    public String cCT(String s){
        if(s.equals("Chest")){
            return "§6Che";
        }
        if(s.equals("DoubleChest")){
            return "§6DCh";
        }
        if(s.equals("Player")){
            return "§7Inv";
        }
        if(s.equals("Ender")){
            return "§5End";
        }
        if(s.equals("Shulker")){
            return "§dShu";
        }
        if(s.equals("Furnace")){
            return "§8Fur";
        }
        if(s.equals("Dispenser")){
            return "§3Disp";
        }
        return "§0Unk";
    }

    public void sendPacket(Player p, Object packet) {
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket((Packet)packet);
    }

    /*
        PacketPlayOutChat iChatBaseComponent = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"TextHere\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/test command 1 2 3\"}}"));
        CraftPlayer p  = (CraftPlayer) sender;
        p.getHandle().playerConnection.sendPacket(iChatBaseComponent);
     */
}
