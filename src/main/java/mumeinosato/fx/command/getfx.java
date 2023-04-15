package mumeinosato.fx.command;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joml.RoundingMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class getfx {

        private static final String URL = "https://jp.tradingview.com/symbols/USDJPY/?exchange=FX";
        private static final String RATE_PATTERN = "<span class=\"last-JWoJqCpY js-symbol-last\"><span>(\\d+\\.\\d+)<sup>";

        public static void main(String[] args) {
                try {
                        // 為替レートを取得
                        double rate = getRate();

                        // 小数点第4位で四捨五入
                        BigDecimal rateBD = BigDecimal.valueOf(rate).setScale(4, RoundingMode.HALF_UP);

                        // 値を表示
                        System.out.println(rateBD);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public static double getRate() throws IOException {
                // URLを指定してHTMLを取得
                Document document = Jsoup.connect(URL).get();

                // 正規表現を使って為替レートのテキストを取得
                Pattern pattern = Pattern.compile(RATE_PATTERN);
                Matcher matcher = pattern.matcher(document.html());
                String rateText = null;
                if (matcher.find()) {
                        rateText = matcher.group(1);
                }
                if (rateText == null) {
                        throw new RuntimeException("為替レートが見つかりませんでした");
                }

                // 余分な文字列を削除
                rateText = rateText.replaceAll("[^\\d.]", "");

                // 為替レートをdouble型に変換
                double rate = Double.parseDouble(rateText);

                // 為替レートを返す
                return rate;
        }

        public double getExchangeRate() {
                try {
                        return getRate();
                } catch (IOException e) {
                        e.printStackTrace();
                        return -1.0; //エラー時には-1を返す
                }
        }
}
