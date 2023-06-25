package mumeinosato.fx.command;
import static mumeinosato.fx.command.fxdata.getfx;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import mumeinosato.fx.Fx;

import mumeinosato.fx.sql.SQL;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.math.BigDecimal;
import java.util.*;

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
                        double rate = getfx(dbPath);
                        BigDecimal amount = BigDecimal.valueOf((rate * value));
                        try {
                            Economy.subtract(UUID.fromString(uuid), amount);
                            sender.sendMessage( amount + "円で株を" + value + "つ購入しました");
                            SQL sql = new SQL();
                            sql.addfx(uuid, value);
                        } catch (NoLoanPermittedException e) {
                            // ユーザーの残高が負の値になることが許可されていない場合の処理
                            sender.sendMessage("残高が足りません");
                        } catch (ArithmeticException e) {
                            // 残高からの引き算が失敗した場合の処理
                            sender.sendMessage("残高の引き出しに失敗しました");
                        } catch (MaxMoneyException e) {
                            // 残高が最大金額を超える場合の処理
                            sender.sendMessage("残高が最大金額を超えます");
                        } catch (UserDoesNotExistException e) {
                            // 指定したユーザーが存在しない場合の処理
                            sender.sendMessage("ユーザーが存在しません");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("sell")) {
                    if(args.length < 2) {
                        sender.sendMessage("売る数を指定してください");
                    } else {
                        int value = 0;
                        try {
                            value = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("数は数値で指定してください");
                        }
                        // value 処理
                        Player player = (Player) sender;
                        String uuid = player.getUniqueId().toString();
                        SQL sql = new SQL();
                        int check = sql.pgetfx(uuid);
                        if (check == 1){
                            int val = (int) sql.checkfx(uuid);
                            if (val <= value){
                                try {
                                    double rate = getfx(dbPath);
                                    BigDecimal amount = BigDecimal.valueOf((rate * value));
                                    Economy.add(UUID.fromString(uuid), amount);
                                    sender.sendMessage( amount + "円で株を" + value + "つ売却りました");
                                    sql.subtractfx(uuid, value);
                                } catch (NoLoanPermittedException e) {
                                    // ユーザーの残高が負の値になることが許可されていない場合の処理
                                    sender.sendMessage("残高が足りません");
                                } catch (MaxMoneyException e) {
                                    // 残高が最大金額を超える場合の処理
                                    sender.sendMessage("残高が最大金額を超えます");
                                } catch (UserDoesNotExistException e) {
                                    // 指定したユーザーが存在しない場合の処理
                                    sender.sendMessage("ユーザーが存在しません");
                                }
                            } else {
                                sender.sendMessage("売却するための株が足りません");
                            }
                        } else {
                            sender.sendMessage("あなたは株を持っていません");
                        }
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
                    Objective objective = scoreboard.registerNewObjective("fxscore", "dummy", "現在のレート");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    showToggleMap.putIfAbsent(uuid, false);
                    boolean isToggledOn = showToggleMap.get(uuid);
                    if (!isToggledOn) {
                        final double[] rate = {getfx(dbPath)};
                        Score score = objective.getScore("");
                        score.setScore(0);
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
                        sender.sendMessage("現在のレートを表示しました\n表示には少し時間がかかります");
                    } else {
                        player.setScoreboard(scoreboardManager.getMainScoreboard());
                        showToggleMap.put(uuid, false);
                        sender.sendMessage("現在のレートを非表示にしました");
                    }
                } else if (args[0].equalsIgnoreCase("check")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("このコマンドはプレイヤーしか実行できません");
                        return false;
                    }
                    Player player = (Player) sender;
                    String uuid = player.getUniqueId().toString();
                    SQL sql = new SQL();
                    int check = sql.pgetfx(uuid);
                    if (check == 1) {
                        sender.sendMessage("株を" + sql.checkfx(uuid) + "つ持っています");
                    } else {
                        sender.sendMessage("あなたは株を持っていません");
                    }
                }else {
                    sender.sendMessage("コマンドが見つかりません");
                }
            }
        }
        return false;
    }
}