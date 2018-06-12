package red.man10.man10moneytracer;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.man10atm.Man10ATMAPI;
import red.man10.man10mysqlapi.Man10MysqlAPI;
import red.man10.man10mysqlapi.MySQLAPI;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Man10MoneyTracer extends JavaPlugin {

    Man10ATMAPI atmapi = null;
    MySQLAPI mysql = null;
    Man10MoneyTracerManager manager = new Man10MoneyTracerManager(this);

    String createTable = "CREATE TABLE `man10_money_tracer` (\n" +
            "\t`id` INT NOT NULL AUTO_INCREMENT,\n" +
            "\t`container_id` VARCHAR(128) NULL,\n" +
            "\t`final_editor_name` VARCHAR(32) NULL,\n" +
            "\t`final_editor_uuid` VARCHAR(64) NULL,\n" +
            "\t`container_type` VARCHAR(128) NULL,\n" +
            "\t`slot` INT NULL,\n" +
            "\t`item` VARCHAR(128) NULL,\n" +
            "\t`damage` DOUBLE NULL,\n" +
            "\t`display_name` VARCHAR(2048) NULL,\n" +
            "\t`amount` INT NULL,\n" +
            "\t`lore` VARCHAR(8192) NULL,\n" +
            "\t`enchant` VARCHAR(4096) NULL,\n" +
            "\t`flag` VARCHAR(4096) NULL,\n" +
            "\t`value` DOUBLE NULL,\n" +
            "\t`world` VARCHAR(64) NULL,\n" +
            "\t`x` INT NULL,\n" +
            "\t`y` INT NULL,\n" +
            "\t`z` INT NULL,\n" +
            "\t`date_time` DATETIME NULL,\n" +
            "\t`time` BIGINT NULL,\n" +
            "\t PRIMARY KEY (`id`)\n" +
            ")\n" +
            "COLLATE='utf8_general_ci'\n" +
            "ENGINE=InnoDB\n" +
            ";\n";

    String money_tracer_player = "CREATE TABLE `man10_money_tracer_player` (\n" +
            "\t`id` BIGINT NOT NULL AUTO_INCREMENT,\n" +
            "\t`name` VARCHAR(32) NULL DEFAULT '0',\n" +
            "\t`uuid` VARCHAR(64) NULL DEFAULT '0',\n" +
            "\t`total` BIGINT NULL DEFAULT '0',\n" +
            "\t`balance` BIGINT NULL DEFAULT '0',\n" +
            "\t`estate` BIGINT NULL DEFAULT '0',\n" +
            "\t`date_time` DATETIME NULL DEFAULT NULL,\n" +
            "\t`date` DATE NULL DEFAULT NULL,\n" +
            "\t`hour` VARCHAR(8) NULL DEFAULT NULL,\n" +
            "\t`time` BIGINT NULL DEFAULT '0',\n" +
            "\t PRIMARY KEY (`id`)\n" +
            ")\n" +
            "COLLATE='utf8_general_ci'\n" +
            "ENGINE=InnoDB\n" +
            ";\n";

    String money_tracer_server = "CREATE TABLE `man10_money_tracer_server` (\n" +
            "\t`id` BIGINT NOT NULL AUTO_INCREMENT,\n" +
            "\t`total` BIGINT NULL DEFAULT '0',\n" +
            "\t`balance` BIGINT NULL DEFAULT '0',\n" +
            "\t`estate` BIGINT NULL DEFAULT '0',\n" +
            "\t`date_time` DATETIME NULL DEFAULT NULL,\n" +
            "\t`date` DATE NULL DEFAULT NULL,\n" +
            "\t`hour` INT NULL DEFAULT NULL,\n" +
            "\t`time` BIGINT NULL DEFAULT '0',\n" +
            "\t PRIMARY KEY (`id`)\n" +
            ")\n" +
            "COLLATE='utf8_general_ci'\n" +
            "ENGINE=InnoDB\n" +
            ";\n";

    ExecutorService executorService = null;
    HashMap<UUID,List<Man10MoneyTracerItem>> playerSearchHashmap = new HashMap<>();
    HashMap<UUID,Long> playerSearchTotalHashMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        executorService = Executors.newFixedThreadPool(15);
        Bukkit.getPluginManager().registerEvents(new Man10MoneyTracerListener(this),this);
        this.saveDefaultConfig();
        getCommand("msearch").setExecutor(new Man10MoneyTracerSearchCommand(this));
        getCommand("mtp").setExecutor(new Man10TPCommand());
        mysql = new MySQLAPI(this,"money_trace");
        atmapi = new Man10ATMAPI();
        mysql.execute(createTable);
        mysql.execute(money_tracer_player);
        mysql.execute(money_tracer_server);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        executorService.shutdown();
    }
}
