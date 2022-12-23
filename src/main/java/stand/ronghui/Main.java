package stand.ronghui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {
		String username = args[0];
		String password = args[1];

		//初始化
		String[][] headers = {{"User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"}};
		HttpRequest request = HttpRequest.get("https://bbs.binmt.cc/misc.php?mod=mobile", headers);
		String saltKey = "";
		Matcher matcher = Pattern.compile("saltkey=(.*?);").matcher(request.cookies);
		if (matcher.find()) saltKey = matcher.group(1);
		String formHash = "";
		matcher = Pattern.compile("formhash=(.*?)&amp").matcher(request.text);
		if (matcher.find()) formHash = matcher.group(1);

		//登录
		String data = "formhash=" + formHash + "&referer=https%3A%2F%2Fbbs.binmt.cc%2Fk_misign-sign.html&fastloginfield=username&cookietime=31104000&username=" + username + "&password=" + password + "&questionid=0&answer=&submit=true";
		headers = new String[][]{{"Cookie","cQWy_2132_saltkey=" + saltKey},{"Content-type","application/x-www-form-urlencoded; charset=UTF-8"},{"User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"}};
		request = HttpRequest.post("https://bbs.binmt.cc/member.php?mod=logging&action=login&loginsubmit=yes&loginhash=&handlekey=loginform&inajax=1", data, headers);
		String cQWy_2132_auth = "";
		matcher = Pattern.compile("cQWy_2132_auth=(.*?);").matcher(request.cookies);
		if (matcher.find()) cQWy_2132_auth = matcher.group(1);

		//获取formhash
		headers = new String[][]{{"accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"},{"User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"},{"Cookie","cQWy_2132_saltkey=" + saltKey + ";cQWy_2132_auth=" + cQWy_2132_auth}};
		request = HttpRequest.get("https://bbs.binmt.cc/k_misign-sign.html", headers);
		matcher = Pattern.compile("formhash=(.*?)&amp").matcher(request.text);
		if (matcher.find()) formHash = matcher.group(1);

		//签到
		String[][] params = {{"operation","qiandao"},{"formhash",formHash},{"Cookie","cQWy_2132_saltkey=" + saltKey + ";cQWy_2132_auth=" + cQWy_2132_auth}};
		headers = new String[][]{{"Cookie",params[2][1]},{"Referer","https://bbs.binmt.cc/forum.php"},{"User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36"}};
		request = HttpRequest.get("https://bbs.binmt.cc/k_misign-sign.html", params, headers);
		if (request.text.contains("今日已签")) {
			System.out.println("MT论坛---今日已签");
		} else if (request.text.length() == 2) {
			System.out.println("MT论坛---签到成功");
		} else {
			System.out.print("MT论坛---签到失败");
			matcher = Pattern.compile("(?<=CDATA).*?.\\]").matcher(request.text);
			if (matcher.find()) {
				System.out.println(matcher.group(0));
			}
		}
    }
}
