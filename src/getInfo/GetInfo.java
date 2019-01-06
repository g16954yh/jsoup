package getInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;

public class GetInfo {

	public static void main(String[] args) {
		FileWriter fw = null;
		try {
			//ファイルの書き込み
			fw = new FileWriter("data",true);
			Document document = Jsoup.connect("https://travel.yahoo.co.jp/dhotel/shisetsu/HT10024471/review?facet_flg=1&dcbpt=1").get();
			Elements url = document.select(".ttl h2 a");
			for(Element element : url) {
				String data = element.attr("href");
				fw.write(data+"\n");
			}
			/**Elements elements = document.select(".navi li p span");
			for(Element element : elements) {
				String data = element.text();
				fw.write(data+",");
			}
			*/
			fw.flush();
		} catch (IOException e) {
			System.out.println("ファイル書き込みエラーです");
		} finally {
			if(fw!=null) {
				try {
					if(fw!=null) {
						fw.close();
					}
				} catch (IOException e) {}
			}
		}
		
		
	}
}
