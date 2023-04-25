package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.models.Device_supplier;
import com.budwk.app.device.models.Device_supplier_code;
import com.budwk.app.device.services.DeviceSupplierCodeService;
import com.budwk.app.device.services.DeviceSupplierService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * @author wizzer.cn
 */
@IocBean
@At("/admin/supplier")
@SLog(tag = "Device_supplier")
@ApiDefinition(tag = "Device_supplier")
@Slf4j
public class DeviceSupplierController {
    @Inject
    private DeviceSupplierService deviceSupplierService;
    @Inject
    private DeviceSupplierCodeService deviceSupplierCodeService;

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "分页查询")
    @ApiFormParams
    @ApiResponses
    @SaCheckPermission("device.settings.supplier")
    public Result<?> list() {
        Cnd cnd = Cnd.NEW();
        List<Device_supplier> list = deviceSupplierService.query(cnd);
        List<NutMap> mapList = new ArrayList<>();
        for (Device_supplier supplier : list) {
            NutMap map = Lang.obj2nutmap(supplier);
            map.put("isSupplier", true);
            map.put("code", "");
            List<Device_supplier_code> codeList = deviceSupplierCodeService.query(Cnd.NEW().and("supplierId", "=", supplier.getId()));
            if (codeList.size() > 0) {
                map.put("children", codeList);
            }
            mapList.add(map);
        }
        return Result.data(mapList);
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增厂家")
    @ApiFormParams(
            implementation = Device_supplier.class
    )
    @ApiResponses
    @SLog("新增厂家:${deviceSupplier.name}")
    @SaCheckPermission("device.settings.supplier.create")
    public Result<?> create(@Param("..") Device_supplier deviceSupplier, HttpServletRequest req) {
        deviceSupplier.setCreatedBy(SecurityUtil.getUserId());
        deviceSupplierService.insert(deviceSupplier);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改厂家")
    @ApiFormParams(
            implementation = Device_supplier.class
    )
    @ApiResponses
    @SLog("修改厂家:${deviceSupplier.name}")
    @SaCheckPermission("device.settings.supplier.update")
    public Result<?> update(@Param("..") Device_supplier deviceSupplier, HttpServletRequest req) {
        deviceSupplier.setUpdatedBy(SecurityUtil.getUserId());
        deviceSupplierService.updateIgnoreNull(deviceSupplier);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取厂家")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.settings.supplier")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_supplier deviceSupplier = deviceSupplierService.fetch(id);
        if (deviceSupplier == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceSupplier);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除厂家")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除厂家:")
    @SaCheckPermission("device.settings.supplier.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_supplier deviceSupplier = deviceSupplierService.fetch(id);
        if (deviceSupplier == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceSupplierService.delete(id);
        deviceSupplierCodeService.clear(Cnd.NEW().and("supplierId", "=", id));
        req.setAttribute("_slog_msg", deviceSupplier.getName());
        return Result.success();
    }

    @At("/code/create")
    @Ok("json")
    @POST
    @ApiOperation(name = "新增厂家设备")
    @ApiFormParams(
            implementation = Device_supplier_code.class
    )
    @ApiResponses
    @SLog("新增厂家设备:${deviceSupplierCode.name}")
    @SaCheckPermission("device.settings.supplier.create")
    public Result<?> createCode(@Param("..") Device_supplier_code deviceSupplierCode, HttpServletRequest req) {
        deviceSupplierCode.setCreatedBy(SecurityUtil.getUserId());
        deviceSupplierCodeService.insert(deviceSupplierCode);
        return Result.success();
    }

    @At("/code/update")
    @Ok("json")
    @POST
    @ApiOperation(name = "修改厂家设备")
    @ApiFormParams(
            implementation = Device_supplier_code.class
    )
    @ApiResponses
    @SLog("修改厂家设备:${deviceSupplierCode.name}")
    @SaCheckPermission("device.settings.supplier.update")
    public Result<?> updateCode(@Param("..") Device_supplier_code deviceSupplierCode, HttpServletRequest req) {
        deviceSupplierCode.setUpdatedBy(SecurityUtil.getUserId());
        deviceSupplierCodeService.updateIgnoreNull(deviceSupplierCode);
        return Result.success();
    }

    @At("/code/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取厂家设备")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("device.settings.supplier")
    public Result<?> getDataCode(String id, HttpServletRequest req) {
        Device_supplier_code deviceSupplierCode = deviceSupplierCodeService.fetch(id);
        if (deviceSupplierCode == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceSupplierCode);
    }

    @At("/code/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除厂家设备")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除厂家设备:")
    @SaCheckPermission("device.settings.supplier.delete")
    public Result<?> deleteCode(String id, HttpServletRequest req) {
        Device_supplier_code deviceSupplierCode = deviceSupplierCodeService.fetch(id);
        if (deviceSupplierCode == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceSupplierCodeService.delete(id);
        req.setAttribute("_slog_msg", deviceSupplierCode.getName());
        return Result.success();
    }
}