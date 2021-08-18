package com.example.ding.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.example.ding.sonar.SonarMessage;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * DingController
 *
 * @author Mike
 * @date 2021-06-24
 */
@RestController
@RequestMapping("/ding")
public class DingController{


    @RequestMapping("/send")
    public String sendMessage(@RequestBody SonarMessage msg) throws Exception {
        System.out.println(msg);
        String accessToken = msg.getProperties().getString("sonar.analysis.dingtalktoken");
        if (accessToken == null || accessToken.isEmpty()){
            System.out.println("Dingtalk access_token is empty, return");
            return "failed";
        }

        String projectName = msg.getProject().getName();
        String projectUrl = msg.getProject().getUrl();

        // 如果环境变量定义了 SONAR_HOST，则把 projectUrl 的域名和端口部分，替换成 SONAR_HOST 的值。
        // 因为 sonarqube 回调时会设置成 http://localhost:9000/dashboard?id=xxx 的形式
        String envSonarHost = System.getenv("SONAR_HOST");
        if ((envSonarHost != null) && (!envSonarHost.isEmpty())){
            URL url = new URL(projectUrl);
            projectUrl = envSonarHost + url.getFile();
        }

        String sonarResult = "通过👍🏻";
        String titleColor = "#008000";
        String qualityGateStatus = msg.getQualityGate().getString("status");
        if ("ERROR".equals(qualityGateStatus)){
            sonarResult = "不通过👎";
            titleColor = "#FF0000";
        }


        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=" + accessToken);
        OapiRobotSendRequest robbotRequest = new OapiRobotSendRequest();

        robbotRequest.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("sonar build " + sonarResult);

        StringBuilder bld = new StringBuilder();
        bld.append("### <font color=").append(titleColor).append(">").append(projectName).append(" 代码质量检测结果：").append(sonarResult).append("</font>  \n\n");
        JSONArray conditionArray = msg.getQualityGate().getJSONArray("conditions");
        // 遍历JSONArray
        int i = 1;
        for (Iterator<Object> iterator = conditionArray.iterator(); iterator.hasNext(); i++) {
            LinkedHashMap<String, String> ob = (LinkedHashMap<String, String>)iterator.next();

            bld.append("■ 判定条件[" + i + "]: " + ob.get("status") + "  \n\n");
            bld.append("◇规则: ").append(ob.get("metric")).append(" not ").append(ob.get("operator")).append(" ").append(ob.get("errorThreshold")).append("  \n\n");
            bld.append("◇当前值: ").append(ob.get("value")).append("  \n\n");
        }

        bld.append("Rev: " + msg.getRevision()).append(")  \n\n");
        bld.append("[点击查看 sonarqube 完整报告](").append(projectUrl).append(")  \n\n");

        markdown.setText(bld.toString());
        robbotRequest.setMarkdown(markdown);
        OapiRobotSendResponse response = client.execute(robbotRequest);
        System.out.println("发送结果：" + JSON.toJSON(response));


        return "success";
    }

}

