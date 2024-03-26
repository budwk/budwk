# wk-starter-excel

Excel导入/导出组件

## 使用说明

### 实体类定义

```java

@Table("iot_device")
public class Iot_device extends BaseModel implements Serializable {
    // 字符串字段 示例，type不设置则导入导出都有
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Excel(name = "设备通信号", cellType = Excel.ColumnType.STRING, prompt = "设备通信号")
    private String deviceNo;

    // 字典类型字段 字典示例，注意dict定义方式
    @Column
    @ColDefine(type = ColType.BOOLEAN)
    @Default("0")
    @Excel(name = "是否在线", cellType = Excel.ColumnType.STRING, prompt = "是否在线",dict = "false=离线,true=在线",type = Excel.Type.EXPORT)
    private Boolean online;

    // 时间戳字段 格式化日期示例，支持Date、Long等类型字段
    @Column
    @Comment("最新通讯连接时间")
    @Excel(name = "通讯时间", cellType = Excel.ColumnType.NUMERIC, prompt = "通讯时间", dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    private Long lastConnectionTime;

    // 枚举类示例 
    @Column
    @Excel(name = "数据类型", cellType = Excel.ColumnType.STRING,prompt = "数据类型")
    private DeviceDataType dataType;
}
```

* 枚举类中需要有 `text()` `fromText(String text)` 两个方法用于导出/导入

```java
import org.nutz.json.JsonShape;

@JsonShape(JsonShape.Type.OBJECT)
public enum DeviceAttrType {
    INDEX(0, "指标"),
    STATE(1, "状态"),
    INFO(2, "信息"),
    OTHER(3, "其他");
    private int value;
    private String text;

    DeviceAttrType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int value() {
        return this.value;
    }

    // excel导出用方法
    public String text() {
        return this.text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static DeviceAttrType from(int value) {
        for (DeviceAttrType v : values()) {
            if (v.value() == value) {
                return v;
            }
        }
        return OTHER;
    }

    // excel导入用方法
    public static DeviceAttrType fromText(String text) {
        for (DeviceAttrType v : values()) {
            if (v.text().equals(text)) {
                return v;
            }
        }
        return OTHER;
    }
}

```

### 控制类

```java
    @At("/device/export")
    @Ok("void")
    @POST
    @ApiOperation(name = "导出设备数据")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "productId", description = "产品ID"),
                    @ApiFormParam(name = "fieldNames", description = "字段名以,分割"),
                    @ApiFormParam(name = "ids", description = "部分数据导出的ID以,分割")
            }
    )
    @ApiResponses
    @SaCheckPermission("iot.device.product.device.export")
    public void export(@Param("ids") String[] ids, @Param("fieldNames") String[] fieldNames, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy, HttpServletRequest req, HttpServletResponse response) {
        Cnd cnd = Cnd.NEW();
        if (ids != null) {
            cnd.and("id", "in", ids);
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        try {
            List<Iot_device> list = iotDeviceService.query(cnd);
            ExcelUtil<Iot_device> util = new ExcelUtil<>(Iot_device.class);
            util.exportExcel(response, list, "设备数据", fieldNames);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @At("/device/import")
    @POST
    @Ok("json:full")
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:fileUpload"})
    @SaCheckLogin
    @ApiOperation(name = "导入设备数据")
    @SLog(value = "导入设备数据")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", example = "", description = "文件表单对象名"),
                    @ApiFormParam(name = "cover", example = "", description = "是否覆盖"),
                    @ApiFormParam(name = "productId", example = "", description = "产品ID"),
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses
    public Result<?> deviceImport(@Param("Filedata") TempFile tf, @Param(value = "cover", df = "false") boolean cover, @Param("productId") String productId, HttpServletRequest req, AdaptorErrorContext err) {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("上传文件错误");
            } else if (tf == null) {
                return Result.error("未上传文件");
            } else {
                String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
            if (!".xls".equalsIgnoreCase(suffixName) && !".xlsx".equalsIgnoreCase(suffixName)) {
                return Result.error("请上传.xls/.xlsx格式文件");
            }
                try {
                    ExcelUtil<Iot_device> deviceExcelUtil = new ExcelUtil<>(Iot_device.class);
                    List<Iot_device> list = deviceExcelUtil.importExcel(tf.getInputStream());
                    iotDeviceService.importData(productId, tf.getSubmittedFileName(), list, cover, SecurityUtil.getUserId(), SecurityUtil.getUserLoginname());
                    return Result.success("文件上传成功，处理结果将通过站内信通知");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BaseException("文件处理失败");
                }
                }
    }
```

### 前端页面

```vue
<import
    v-permission="['iot.device.product.device.import']"
    btn-text="导入设备"
    :action="API_IOT_DEVICE_PRODUCT_DEVICE_IMPORT"
    :data="queryParams"
    temp-url="/tpl/template_device.xlsx"
    @refresh="handleSearch"
    style="margin-right: 12px"
/>

<export
    v-permission="['iot.device.product.device.export']"
    btn-text="导出设备"
    :check-list="multipleSelection"
    :action="API_IOT_DEVICE_PRODUCT_DEVICE_EXPORT"
    :columns="columns"
    :data="{
        productId: id
    }"
    style="margin-right: 12px"
/>
```

multipleSelection 为el-table多选集合

```javascript
const columns = ref([
    { prop: 'deviceNo', label: '设备通信号', show: true, fixed: 'left' },
    { prop: 'meterNo', label: '设备编号', show: true },
    { prop: 'iotPlatformId', label: '第三方平台设备号', show: false },
    { prop: 'imei', label: 'IMEI', show: true },
    { prop: 'iccid', label: 'ICCID', show: true },
    { prop: 'lastConnectionTime', label: '最后通信时间', show: true },
    { prop: 'online', label: '在线状态', show: true },
    { prop: 'createdAt', label: '创建时间', show: true },
])
```
