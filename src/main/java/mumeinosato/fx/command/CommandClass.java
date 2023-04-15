package mumeinosato.fx.command;
import static mumeinosato.fx.command.getfx.getRate;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class CommandClass implements CommandExecutor {

    private Timer timer;
    private int counter = 0;

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
                    //処理
                    startTimer(sender);
                } else {
                    sender.sendMessage("コマンドが見つかりません");
                }
            }
        }
        return false;
    }

    private void startTimer(CommandSender sender) {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (counter >=10) {
                    timer.cancel();
                    timer = null;
                    return;
                }
                getfx fx = new getfx();
                double rate = fx.getExchangeRate();
                sender.sendMessage(String.valueOf(rate));
                counter++;
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

}