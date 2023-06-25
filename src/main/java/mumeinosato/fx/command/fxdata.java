package mumeinosato.fx.command;

import mumeinosato.fx.sql.SQL;

import java.util.Random;

public class fxdata {

        public static double setfx(String dbpath) {
                String dbPath = "";
                double now = getfx(dbPath);
                if (Double.isNaN(now)) {
                        SQL sql = new SQL();
                        sql.updaterate(100);
                        return 100;
                } else {
                        Random random = new Random();
                        double randomRate = Math.round((random.nextDouble() * 4 - 2) * 100.0) / 100.0;
                        double rate = now + randomRate;
                        SQL sql = new SQL();
                        sql.updaterate(rate);
                        return rate;
                }
        }

        public static double getfx(String dbPath) {
                SQL sql = new SQL();
                return sql.getRate();
        }

}
