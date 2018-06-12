package red.man10.man10moneytracer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by sho on 2017/08/23.
 */
public class Man10TPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player == false){
            sender.sendMessage("このコマンドはプレイヤーからのみ実行できます");
            return false;
        }
        if(!sender.hasPermission("man10.tp")){
            sender.sendMessage("あなたには権限がありません");
            return false;
        }
        Player p = (Player) sender;
        if(args.length != 4){
            p.sendMessage("使い方が間違っています/mtp <world> <x> <y> <z>");
            return false;
        }
        World w = Bukkit.getWorld(args[0]);
        if(w == null){
            p.sendMessage("ワールドが存在しません");
            return false;
        }
        try {
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            Location l = new Location(w,x,y,z);
            p.teleport(l);
        }catch (NumberFormatException e){
            p.sendMessage("座標は数字です");
            return false;
        }
        return false;
    }
}
