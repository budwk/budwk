# wk-starter-job

任务组件

## 使用说明

```java
/**
 * ioc名称要一致,非单例
 */
@IocBean(name = "abc",singleton = false)
@Slf4j
public class Abc {

    @SJob("jobNameXXX")
    // taskId 必须为第一个参数,params 必须为第二个参数
    // SJob 任务名称必须全局唯一
    public void execute(String taskId, String params) {
        log.info("taskId:{} params:{}", taskId, params);
    }
}

```
## 配置
```yaml
job:
  # 定时任务所在的包名 多个包名用,分割
  package: com.budwk.app.sys.commons.task
```