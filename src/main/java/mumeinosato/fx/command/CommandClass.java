package mumeinosato.fx.command;
import static mumeinosato.fx.command.fxdata.getfx;
import mumeinosato.fx.Fx;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CommandClass implements CommandExecutor {

    private Map<String, Boolean> showToggleMap = new HashMap<>();

    Fx plugin = Fx.getInstance();
    String dbPath = plugin.getDBPath();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("fx")){
            if(args.length == 0) {
                sender.sendMessage("サブコマンドが必要です");
            } else {
                if(args[0].equalsIgnoreCase("buy")){
                    if(args.length < 2) {
                        sender.sendMessage("購入する数を指定してください");
                    } else {
                        int value;
                        try {
                            value = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("数は数値で指定してください");
                            return false;
                        }
                        // value 処理
                        Player player = (Player) sender;
                        String uuid = player.getUniqueId().toString();
                    }
                } else if (args[0].equalsIgnoreCase("sell")) {
                    if(args.length < 2) {
                        sender.sendMessage("売る数を指定してください");
                    } else {
                        int value;
                        try {
                            value = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("数は数値で指定してください");
                        }
                        // value 処理
                        Player player = (Player) sender;
                        String uuid = player.getUniqueId().toString();
                    }
                } else if (args[0].equalsIgnoreCase("show")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("このコマンドはプレイヤーしか実行できません");
                        return false;
                    }
                    Player player = (Player) sender;
                    String uuid = player.getUniqueId().toString();
                    ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
                    Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
                    Objective objective = scoreboard.registerNewObjective("fxscore", "dummy", "Scoreboard Title");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    showToggleMap.putIfAbsent(uuid, false);
                    boolean isToggledOn = showToggleMap.get(uuid);
                    if (!isToggledOn) {
                        final double[] rate = {getfx(dbPath)};
                        Score score = objective.getScore("");
                        score.setScore(1);
                        Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                double newRate = getfx(dbPath);
                                if (rate[0] != newRate) {
                                    rate[0] = newRate;
                                    Bukkit.getScheduler().runTask(plugin, () -> {
                                        score.setScore((int) rate[0]);
                                    });
                                }
                            }
                        };
                        timer.schedule(task, 0L, 10000L);
                        player.setScoreboard(scoreboard);
                        showToggleMap.put(uuid, true);
                        sender.sendMessage("Scoreboardを表示しました");
                    } else {
                        player.setScoreboard(scoreboardManager.getMainScoreboard());
                        showToggleMap.put(uuid, false);
                        sender.sendMessage("Scoreboardを非表示にしました");
                    }
                } else {
                    sender.sendMessage("コマンドが見つかりません");
                }
            }
        }
        return false;
    }
}