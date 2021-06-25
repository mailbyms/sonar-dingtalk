## 项目介绍  
一个 spring boot 接口项目，用来接收 sonarqube 的回调(webhook)，使用钉钉发送代码质量阀结果  
![image](https://user-images.githubusercontent.com/16809751/123365618-2f055500-d5a9-11eb-8b4a-099f66f02df6.png)

## 流程说明
- 在 sonar 扫描时，加入自定义参数 -Dsonar.analysis.dingtalktoken=xxx  
  - 以持续集成工具 Drone 为例，使用了 [mailbyms/drone-sonar-plugin](https://github.com/mailbyms/drone-sonar-plugin)，
通过 `custom_ding_token` 参数传入
      ```yaml
      - name: Sonar代码分析
        image: mailbyms/drone-sonar-plugin
        failure: ignore
        pull: if-not-exists
        settings:
          sonar_host:
            from_secret: sonar_host
          sonar_token:
            from_secret: sonar_token
          JAVA_BINARIES: target/classes
          # optional, for sonarqube webhook
          custom_ding_token:
            from_secret: dingtalk_token
        when:
          event:
            - push
      ```
- sonarqube 网站配置 webhook。执行过 sonar 扫描后，在 “最后信息” 可以看到请求调用的参数，样例：  
  ![image](https://user-images.githubusercontent.com/16809751/123365375-bdc5a200-d5a8-11eb-9f6d-0ca90ea0b98c.png)

  ```yaml
  {
    "serverUrl": "http://localhost:9000",
    "taskId": "AXo8-x7Nixoe_8jfSKjq",
    "status": "SUCCESS",
    "analysedAt": "2021-06-24T15:43:41+0800",
    "revision": "4d45eb9a50bc20ebb65102597dd43f0ae2826479",
    "changedAt": "2021-06-24T15:43:41+0800",
    "project": {
      "key": "test:zxjy-admin",
      "name": "test/zxjy-admin",
      "url": "http://localhost:9000/dashboard?id=test%3Azxjy-admin"
    },
    "branch": {
      "name": "master",
      "type": "BRANCH",
      "isMain": true,
      "url": "http://localhost:9000/dashboard?id=test%3Azxjy-admin"
    },
    "qualityGate": {
      "name": "Mike way",
      "status": "ERROR",
      "conditions": [
        {
          "metric": "bugs",
          "operator": "GREATER_THAN",
          "value": "56",
          "status": "ERROR",
          "errorThreshold": "9"
        }
      ]
    },
    "properties": {
      "sonar.analysis.detectedscm": "git",
      "sonar.analysis.detectedci": "DroneCI",
      "sonar.analysis.dingtalktoken": "xxxxxxxxxxxxxxxxxxxxxxxxxxx"
    }
  }
  ```
- 可以看到上面的 `custom_ding_token` 通过 properties 的 `sonar.analysis.dingtalktoken` 传递给本项目接口
- 本项目解析主要通过 dingtalktoken 发送消息给指定的钉钉群组
- 注意因为 sonarqube 回调时会把项目详情地址设置成 http://localhost:9000/dashboard?id=xxx 的形式，
所以本项目运行时，设置系统的环境变量 SONAR_HOST，用来替换成正确的域名和端口，详见项目的 `docker-compose.yml`
 
