package com.osdiot.gas.device.processor.core;

import com.budwk.app.device.handler.common.message.DeviceDataMessage;
import com.budwk.app.device.handler.common.message.DeviceEventMessage;
import com.budwk.app.device.handler.common.message.DeviceMessage;
import com.budwk.app.device.handler.common.message.DeviceResponseMessage;
import com.budwk.app.device.message.MessageTransfer;
import com.budwk.app.device.models.Device_event_record;
import com.budwk.app.device.objects.cache.DeviceProcessCache;
import com.budwk.app.device.objects.dto.SubscribeDTO;
import com.budwk.app.device.providers.IDeviceInfoProvider;
import com.budwk.app.device.storage.objects.dto.DeviceDTO;
import com.budwk.app.device.storage.objects.dto.EventInfoDTO;
import com.budwk.app.device.storage.objects.dto.NativeWarningInfoDTO;
import com.budwk.app.device.storage.objects.dto.ValueItemDTO;
import com.budwk.app.device.storage.storages.DeviceDataStorage;
import com.budwk.app.device.storage.storages.DeviceEventStorage;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 数据保存处理
 *
 */
@IocBean
@Slf4j
public class SaveDataProcessor implements Processor {
    @Inject
    private MessageTransfer messageTransfer;

    @Inject
    private DeviceDataStorage deviceDataStorage;

    @Inject
    private DeviceEventStorage deviceEventStorage;
    @Inject
    private DeviceAlarmStorageImpl deviceAlarmStorage;
    @Inject
    private DeviceNativeWarningStorage deviceNativeWarningStorage;

    @Inject
    @Reference(check = false)
    private IDeviceInfoProvider deviceInfoProvider;
    @Inject
    @Reference(check = false)
    private IDeviceWarningRecordProvider deviceWarningRecordProvider;

    @Inject
    @Reference(check = false)
    private IDeviceEventProvider deviceEventProvider;

    @Inject
    @Reference(check = false)
    private IDeviceAlarmNoticeProvider deviceAlarmNoticeProvider;

    @Inject
    @Reference(check = false)
    private IDeviceSubscribeProvider deviceSubscribeProvider;
    @Inject
    private ProductCaching productCaching;

    /**
     * 初始化一个默认的线程池
     */
    private final ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4,
                    new NamedThreadFactory("saveprocessor"));

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public void process(ProcessorContext context) {

        DeviceProcessCache device = context.getDevice();
        DeviceDTO dto = new DeviceDTO();
        dto.setDeviceId(device.getDeviceId());
        dto.setDeviceNo(device.getDeviceNo());
        dto.setDeviceCode(device.getDeviceCode());
        dto.setProductId(device.getProductId());
        dto.setHandler(context.getProperty("handler", String.class));
        log.debug("[processor]数据保存: "+Json.toJson(dto));

        executorService.execute(() -> {
            context.getDecodeResult().getMessages().stream().sorted(Comparator.comparingLong(DeviceMessage::getTimestamp))
                    .forEach(message -> {
                        if (message instanceof DeviceDataMessage) {
                            ((DeviceDataMessage) message).setDeviceId(device.getDeviceId());
                            processData(dto, (DeviceDataMessage) message);
                        } else if (message instanceof DeviceEventMessage) {
                            ((DeviceEventMessage) message).setDeviceId(device.getDeviceId());
                            processEvent(device, (DeviceEventMessage) message);
                        }
                        processSubscribe(device, message);
                    });
        });
    }

    private void processSubscribe(DeviceProcessCache device, DeviceMessage message) {
        String productId = device.getProductId();

        Map<String, Object> data = buildData(device, message);
        data.put("deviceNo",device.getDeviceNo());

        List<SubscribeDTO> subscribeList = deviceSubscribeProvider.getSubscribeList(productId, SubscribeDTO.Type.DATA_REPORT);
        for (SubscribeDTO dto : subscribeList) {
            if (Strings.isNotBlank(dto.getDeviceId()) && !dto.getDeviceId().equalsIgnoreCase(device.getDeviceId())) {
                continue;
            }
            this.push(data, dto);
        }
    }

    private Map<String, Object> buildData(DeviceProcessCache device, DeviceMessage message) {
        Map<String, Object> data = Lang.obj2map(message);
        data.put("deviceId", device.getDeviceId());
        data.put("timestamp", System.currentTimeMillis());
        data.put("messageType", getMessageType(message));
        return data;
    }

    private String getMessageType(DeviceMessage message) {
        if (message instanceof DeviceDataMessage) {
            return "dataReport";
        }
        if (message instanceof DeviceResponseMessage) {
            return "commandResponse";
        }
        if (message instanceof DeviceEventMessage) {
            return "eventReport";
        }
        return null;
    }

    private void push(Map<String, Object> data, SubscribeDTO dto) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(dto.getUrl()))
                .POST(HttpRequest.BodyPublishers.ofString(Json.toJson(data, JsonFormat.tidy())))
                .header("Content-Type", "application/json")
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).whenComplete((stringHttpResponse, throwable) -> {
            if (null != throwable) {
                log.error("推送数据到 {} 失败", dto.getUrl(), throwable);
            }
        });
    }

    private void processEvent(DeviceProcessCache device, DeviceEventMessage message) {
        EventInfoDTO event = new EventInfoDTO();
        event.setEventType(message.getType().name());
        event.setEventTime(message.getTimestamp());
        event.setContent(message.getContent());
        event.setAttributes(message.getProperties()
                .stream()
                .map(it -> new ValueItemDTO<>(it.getCode(), it.getName(), it.getValue()))
                .collect(Collectors.toList()));
        event.setAlarmTypeName(message.getEventName());
        event.setAlarmType(message.getEventType());
        //[设备告警] 设备原生告警告警与恢复
        if (DeviceEventMessage.Type.ALARM.equals(message.getType())) {
            // 告警
            NativeWarningInfoDTO nativeWarningInfo = new NativeWarningInfoDTO();
            nativeWarningInfo.setWarningTime(message.getTimestamp());
            nativeWarningInfo.setWarningType(message.getEventType());
            nativeWarningInfo.setWarningTypeName(message.getEventName());
            nativeWarningInfo.setWarningValue(message.getWarningValue());
            //告警更新设备为异常状态
            device.setAbnormal(true);
            device.setAbnormalTime(System.currentTimeMillis());
            // 发送弹框
            Device_native_warning_record record = this.saveWarning(device, nativeWarningInfo);
            this.alarmNotice(device, record);
        } else if (DeviceEventMessage.Type.ALARM_RECOVER.equals(message.getType())) {
            // 告警恢复
            deviceWarningRecordProvider.recoverNativeWarning(device.getProductId(), device.getDeviceId(), message.getEventType());

        }
        this.saveEvent(device, event);
    }

    private void saveEvent(DeviceProcessCache device, EventInfoDTO event) {
        Device_event_record eventRecord = new Device_event_record();
        if (Lang.isNotEmpty(event.getAttributes())) {
            eventRecord.setEventData(event.getAttributes().stream().map(Lang::obj2nutmap).collect(Collectors.toList()));
        }
        eventRecord.setContent(event.getContent());
        eventRecord.setEventType(event.getAlarmType());
        eventRecord.setEventTime(event.getEventTime());
        eventRecord.setProductId(device.getProductId());
        eventRecord.setAccountNo(device.getAccountNo());
        eventRecord.setDeviceId(device.getDeviceId());
        eventRecord.setDeviceNo(device.getDeviceNo());
        deviceEventProvider.save(eventRecord);
    }

    private Device_native_warning_record saveWarning(DeviceProcessCache device, NativeWarningInfoDTO nativeWarningInfo) {
        Device_native_warning_record record = new Device_native_warning_record();
        record.setCompanyId(device.getCompanyId());
        record.setProductId(device.getProductId());
        record.setAccountNo(device.getAccountNo());
        record.setWaningValue(nativeWarningInfo.getWarningValue());
        record.setWarningAt(nativeWarningInfo.getWarningTime());
        record.setDeviceNo(device.getDeviceNo());
        record.setDeviceId(device.getDeviceId());
        record.setWarningTypeId(nativeWarningInfo.getWarningType());
        record.setWarningType(nativeWarningInfo.getWarningTypeName());
        //新增字段
        record.setAccountName(device.getAccountName());
        record.setAreaId(device.getAreaId());
        record.setCommunityId(device.getCommunityId());
        record.setUserType(device.getUserType());
        record.setTypeId(device.getTypeId());
        record.setIdentifier(device.getIdentifier());
        record.setServerStationId(device.getServerStationId());
        record.setDeviceEquipment(device.getDeviceEquipment());
        record.setMeterNo(device.getMeterNo());
        record.setSupplierId(device.getSupplierId());
        //设备无阀门则显示无阀门
        record.setValveState(Boolean.TRUE.equals(device.getValveType()) ?
                DeviceValveStateEnum.from(Optional.ofNullable(device.getValveState()).orElse(DeviceValveStateEnum.UNKNOWN.value())) : DeviceValveStateEnum.NONE);
        record.setNoticeType(DeviceRuleWarningNoticeTypeEnum.STATION_MESSAGE);
        record.setStatus(DeviceWarningHandleStatusEnum.UNHANDLED);
        return deviceWarningRecordProvider.saveNativeWarning(record);


    }

    private EventTypeEnum convertEventType(DeviceEventMessage.Type type) {
        switch (type) {
            case ALARM:
            case ALARM_RECOVER:
                return EventTypeEnum.WARNING;
            default:
                return EventTypeEnum.INFO;
        }
    }

    private void processData(DeviceDTO dto, DeviceDataMessage message) {
        if (Lang.isNotEmpty(message.getProperties())) {
            message.addProperty("ts", message.getTimestamp());
            message.addProperty("receive_time", System.currentTimeMillis());
            deviceDataStorage.saveData(dto, message.getMessageType(), message.getTimestamp(), message.getProperties());
            // 同步更新到 device_attributes
            deviceInfoProvider.saveLatestAttributes(dto.getDeviceId(), message.getProperties());
        }
    }

    private void alarmNotice(DeviceProcessCache device, Device_native_warning_record record) {
        Device_alarm_notice_record dto = new Device_alarm_notice_record();
        dto.setWarningId(record.getId());
        dto.setProductId(device.getProductId());
        dto.setDeviceNo(device.getDeviceNo());
        dto.setDeviceEquipment(device.getDeviceEquipment());
        dto.setDeviceId(device.getDeviceId());
        dto.setAccountNo(device.getAccountNo());
        dto.setAccountName(device.getAccountName());
        dto.setAddress(device.getAddress());
        dto.setWarningType(WarningTypeEnum.NATIVE.getCode() + ":" + record.getWarningTypeId());
        dto.setWaringName(record.getWarningType());
        dto.setWarningValue(Strings.sBlank(record.getWaningValue(), "--"));
        dto.setCompanyId(device.getCompanyId());
        dto.setWarningTime(record.getWarningAt());
//        原生告警弹窗注释,原生告警太多了
//        deviceAlarmNoticeProvider.notify(dto);
    }

    private ValueItemDTO<?> convertTo(com.osdiot.gas.device.protocols.dto.ValueItemDTO<?> dto) {
        return new ValueItemDTO<>(dto.getIdentifier(), dto.getName(), dto.getValue());
    }

    private DeviceDataTypeEnum getDataType(Object value) {
        if (null == value) {
            return DeviceDataTypeEnum.INT;
        }
        Mirror<Object> me = Mirror.me(value);
        if (me.isIntLike()) {
            return DeviceDataTypeEnum.INT;
        }
        return DeviceDataTypeEnum.STRING;
    }
}
