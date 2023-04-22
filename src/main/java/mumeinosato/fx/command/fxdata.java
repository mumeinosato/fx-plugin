package mumeinosato.fx.command;

import mumeinosato.fx.sql.SQL;

import java.util.Random;

public class fxdata {

        public static double setfx(String dbpath) {
                double now = getfx(dbpath);
                if (Double.isNaN(now)) {
                        SQL sql = new SQL();
                        sql.updaterate(dbpath, 100);
                        return 100;
                } else {
                        Random random = new Random();
                        double randomRate = Math.round((random.nextDouble() * 4 - 2) * 100.0) / 100.0;
                        double rate = now + randomRate;
                        SQL sql = new SQL();
                        sql.updaterate(dbpath, rate);
                        return rate;
                }
        }

        public static double getfx(String dbpath) {
                SQL sql = new SQL();
                double rate = sql.getRate(dbpath);
                return rate;
        }

}
