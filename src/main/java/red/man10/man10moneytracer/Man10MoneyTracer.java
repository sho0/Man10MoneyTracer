package red.man10.man10moneytracer;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.man10atm.Man10ATMAPI;
import red.man10.man10mysqlapi.Man10MysqlAPI;
import red.man10.man10mysqlapi.MySQLAPI;

public final class Man10MoneyTracer extends JavaPlugin {

    Man10ATMAPI atmapi = null;
    MySQLAPI mysql = null;
    Man10MoneyTracerManager manager = new Man10MoneyTracerManager(this);

    String createTable = "CREATE TABLE `man10_money_tracer` (\n" +
            "\t`id` INT NULL AUTO_INCREMENT,\n" +
            "\t`container_id` VARCHAR(128) NULL,\n" +
            "\t`final_editor_name` VARCHAR(32) NULL,\n" +
            "\t`final_editor_uuid` VARCHAR(64) NULL,\n" +
            "\t`container_type` VARCHAR(128) NULL,\n" +
            "\t`slot` INT NULL,\n" +
            "\t`item` VARCHAR(128) NULL,\n" +
            "\t`damage` DOUBLE NULL,\n" +
            "\t`display_name` VARCHAR(128) NULL,\n" +
            "\t`ammount` INT NULL,\n" +
            "\t`lore` VARCHAR(8192) NULL,\n" +
            "\t`enchant` VARCHAR(4096) NULL,\n" +
            "\t`flag` VARCHAR(4096) NULL,\n" +
            "\t`value` DOUBLE NULL,\n" +
            "\t`world` VARCHAR(64) NULL,\n" +
            "\t`x` DOUBLE NULL,\n" +
            "\t`y` DOUBLE NULL,\n" +
            "\t`z` DOUBLE NULL,\n" +
            "\t`date_time` DATETIME NULL,\n" +
            "\t`time` BIGINT NULL,\n" +
            "\t PRIMARY KEY (`id`)\n" +
            ")\n" +
            "COLLATE='utf8_general_ci'\n" +
            "ENGINE=InnoDB\n" +
            ";\n";

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new Man10MoneyTracerListener(this),this);
        this.saveDefaultConfig();
        mysql = new MySQLAPI(this,"money_trace");
        atmapi = new Man10ATMAPI();
        mysql.execute(createTable);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
