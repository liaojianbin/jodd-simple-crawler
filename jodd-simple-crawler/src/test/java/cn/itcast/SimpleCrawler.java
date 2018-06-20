package cn.itcast;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.jerry.Jerry;
import jodd.util.StringUtil;
import org.junit.Test;

import static jodd.jerry.Jerry.jerry;

public class SimpleCrawler {

    //抓取拉钩网数据地址
    private static final String FETCH_URL = "https://www.lagou.com/zhaopin/Java/PAGE/?filterOption=PAGE";

    @Test
    public void test() throws Exception {
        String url = FETCH_URL.replaceAll("PAGE", "1");
        //1、获取总页数
        HttpRequest httpRequest = HttpRequest.get(url);
        HttpResponse httpResponse = httpRequest.send();
        String html = new String(httpResponse.bodyBytes(), "utf-8");

        Jerry doc = jerry(html);

        String totalNumStr = doc.$(".totalNum").text();
        System.out.println("总页数为：" + totalNumStr);

        int totalNum = Integer.parseInt(totalNumStr);

        //2、遍历每页数据
        for (int page = 1; page <= totalNum; page++) {
            url = FETCH_URL.replaceAll("PAGE", page+"");
            httpRequest = HttpRequest.get(url);
            httpResponse = httpRequest.send();
            html = new String(httpResponse.bodyBytes(), "utf-8");
            doc = jerry(html);

            //解析每页数据
            doc.$(".con_list_item").each(($this, index)->{
                String position = $this.$(".position_link h3").text();
                String address = $this.$(".add").text();
                System.out.println("岗位名称：" + position + address);
                String companyName = $this.$(".company_name a").text();
                System.out.println("公司名称：" + companyName);
                String money = $this.$(".money").text();
                System.out.println("薪资待遇：" + money);
                String request = $this.$(".li_b_l").text();

                String[] strings = request.split("\n");
                System.out.println("岗位要求：" + StringUtil.replace(strings[2], " ", ""));

                String link = $this.$(".position_link").attr("href");
                System.out.println("链接地址：" + link);
                System.out.println("-------------------------------------------------");
                return true;
            });
            System.out.printf("抓取第 %d 页完成。\n", page);
            System.out.println("=================================================");
        }
    }
}
