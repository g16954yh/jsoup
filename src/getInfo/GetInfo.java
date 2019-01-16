package getInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GetInfo {

	public static void main(String[] args) {

		FileWriter fw = null;
		List<String> urls = new ArrayList<>();
		long interval = 30000;

		// 口コミページのURLの取得
		try {
			fw = new FileWriter("url.txt", true);
			for (int i = 2; i <= 54; i++) {
				long startTime = System.currentTimeMillis();
				Document document = Jsoup.connect("https://search.travel.rakuten.co.jp/ds/yado/tokyo/p" + i).get();
				long endTime = System.currentTimeMillis();
				long time = interval - (endTime - startTime);

				if (time > 0) {
					try {
						// 待機ミリ秒分、待つ
						Thread.sleep(time);
					} catch (InterruptedException e) {
						System.out.println("ミリ秒待機失敗");
					}
				}

				Elements url = document.select(".cstmrEvl a");
				for (Element element : url) {
					String HotelURL = element.attr("href");
					fw.write(HotelURL + "\n");
				}

				fw.flush();
				System.out.println("書き出し完了");

			}
		} catch (IOException e) {
			System.out.println("ファイル書き込みエラーです");
		} finally {
			if (fw != null) {
				try {
					if (fw != null) {
						fw.close();
					}
				} catch (IOException e) {
				}
			}
		}

		// URLファイルの読み込み
		String filename = "url.txt";
		try (BufferedReader in = new BufferedReader(new FileReader(new File(filename)))) {
			String line;
			// 1行ずつ読み込んでリストに入れる
			while ((line = in.readLine()) != null) {
				urls.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1); // 0 以外は異常終了
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("読み込み完了");

		try {
			// 取得したURLから評価を取得して書き出す
			fw = new FileWriter("data.txt", true);
			int size = urls.size();
			for (int i = 0; i < size; i++) {
				long startTime = System.currentTimeMillis();
				Document document = Jsoup.connect(urls.get(i)).get();
				long endTime = System.currentTimeMillis();
				Random r = new Random();
				interval += r.nextInt(300);
				long time = interval - (endTime - startTime);

				if (time > 0) {
					try {
						// 待機ミリ秒分、待つ
						Thread.sleep(time);
					} catch (InterruptedException e) {
						System.out.println("ミリ秒待機失敗");
					}
				}
				System.out.println("待機完了　" + i);

				Elements HotelName = document.select("#RthNameArea h2 a");
				Elements TotalRate = document.select(".rateTotal span");
				Elements DitailRate = document.select(".rateItem span");

				String htlname = HotelName.text();
				fw.write(htlname + "\n");
				String trate = TotalRate.text();
				fw.write(trate + "\n");

				// 評価の数値のみ取り出すためのカウント
				int count = 1;
				for (Element element : DitailRate) {
					String drate = element.text();
					if (count % 2 == 0) {
						fw.write(drate + "\n");
					}
					count++;
				}
			}

			fw.flush();
			System.out.println("書き出し完了");

		} catch (IOException e) {
			System.out.println("ファイル書き込みエラーです");
		} finally {
			if (fw != null) {
				try {
					if (fw != null) {
						fw.close();
					}
				} catch (IOException e) {
				}
			}
		}

		WriteCSV write = new WriteCSV();
		write.writeCSV("data.txt");

	}
}
