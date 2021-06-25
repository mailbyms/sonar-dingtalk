package com.example.ding.sonar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;

@Data
public class SonarMessage {
    String serverUrl;
    String taskId;
    String status;
    String analysedAt;
    String revision;
    String changedAt;

    @Data
    public class Project {
        String key;
        String name;
        String url;
    };
    Project project;

    @Data
    class Branch {
        String name;
        String type;
        boolean isName;
        String url;
    }
    Branch branch;

    @Data
    class QualityGate{
        String name;
        String status;
        //JSONObject conditions;
        ArrayList<Conditions> conditions;
    }

    JSONObject qualityGate;

    JSONObject properties;
}

/*

        "conditions": [
            {
            "metric": "bugs",
            "operator": "GREATER_THAN",
            "value": "21",
            "status": "ERROR",
            "errorThreshold": "9"
            }
        ]


        "properties": {
        "sonar.analysis.detectedscm": "git",
        "sonar.analysis.detectedci": "DroneCI"
        }
*/
