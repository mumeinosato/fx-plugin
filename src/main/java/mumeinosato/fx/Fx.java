package mumeinosato.fx;
import mumeinosato.fx.command.CommandClass;
import mumeinosato.fx.sql.SQL;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public final class  Fx extends JavaPlugin {
    private File dbFile;
    private FileConfiguration config;
    private SQL sql;

    @Override
    public void onEnable() {
        getLogger().info("プラグインが開始しました");

        File dataFolder = getDataFolder();

        saveDefaultConfig();
        config = getConfig();

        File resourceFile = new File(dataFolder, "config.yml");
        if(!resourceFile.exists()) {
            try (InputStream is = getResource("config.yml")) {
                Files.copy(is, resourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                getLogger().warning("configのコピーに失敗しました");
            }
        }

        File dbFolder = new File(dataFolder, "db");
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }

        dbFile = new File(dbFolder, "db.db");
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
                getLogger().info("dbが作成されました");
            } catch (Exception e) {
                getLogger().warning("dbの作成に失敗しました");
            }
        }

        try {
            sql = new SQL();
            sql.SQLiteConnector(dbFile.getAbsolutePath());
            sql.createTable();
        } catch (SQLException e) {
            getLogger().warning("SQLiteの初期化に失敗しました: " + e.getMessage());
        }

        getCommand("fx").setExecutor(new CommandClass());
    }

    @Override
    public void onDisable() {
        getLogger().info("プラグインが停止しました");

        // SQLiteのコネクションをクローズする
        try {
            sql.closeConnection();
        } catch (SQLException e) {
            getLogger().warning("SQLiteのコネクションのクローズに失敗しました: " + e.getMessage());
        }
    }

    public File getDBFile() {
        return dbFile;
    }

    public FileConfiguration getPluginConfig() {
        return config;
    }

    public SQL getSql() {
        return sql;
    }
}