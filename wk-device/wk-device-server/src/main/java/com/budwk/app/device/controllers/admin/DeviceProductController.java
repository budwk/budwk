package com.budwk.app.device.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.device.enums.IotPlatform;
import com.budwk.app.device.enums.ProtocolType;
import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.services.DeviceHandlerService;
import com.budwk.app.device.services.DeviceProductService;
import com.budwk.app.device.services.DeviceSupplierService;
import com.budwk.app.device.services.DeviceTypeService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * @author wizzer.cn
 */
@IocBean
@At("/admin/product")
@SLog(tag = "产品管理")
@ApiDefinition(tag = "产品管理")
@Slf4j
public class DeviceProductController {
    @Inject
    private DeviceProductService deviceProductService;
    @Inject
    private DeviceTypeService deviceTypeService;
    @Inject
    private DeviceSupplierService supplierService;
    @Inject
    private DeviceHandlerService deviceHandlerService;

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "获取初始化数据")
    @ApiImplicitParams
    @ApiResponses(
            value = {
                    @ApiResponse(name = "typeList", description = "设备类型"),
                    @ApiResponse(name = "supplierList", description = "厂家列表"),
                    @ApiResponse(name = "iotPlatform", description = "接入平台")
            }
    )
    @SaCheckLogin
    public Result<?> init(HttpServletRequest req) {
        NutMap map = NutMap.NEW();
        map.addv("typeList", deviceTypeService.query(Cnd.NEW()));
        map.addv("supplierList", supplierService.query(Cnd.NEW(), "codeList"));
        map.addv("handlerList", deviceHandlerService.query(Cnd.NEW()));
        map.addv("iotPlatform", IotPlatform.values());
        map.addv("protocolType", ProtocolType.values());
        return Result.success(map);
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "分页查询")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式"),
                    @ApiFormParam(name = "typeId", example = "", description = "设备类型"),
                    @ApiFormParam(name = "supplierId", example = "", description = "设备厂家"),
                    @ApiFormParam(name = "name", example = "", description = "产品名称")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("devcie.manage.product")
    public Result<?> list(@Param("typeId") String typeId, @Param("supplierId") String supplierId, @Param("name") String name, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(typeId)) {
            cnd.and("typeId", "=", typeId);
        }
        if (Strings.isNotBlank(supplierId)) {
            cnd.and("supplierId", "=", supplierId);
        }
        if (Strings.isNotBlank(name)) {
            cnd.and(Cnd.likeEX("name", name));
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(deviceProductService.listPageLinks(pageNo, pageSize, cnd,"^(deviceType|deviceSupplier|deviceHandler)$"));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增产品")
    @ApiFormParams(
            implementation = Device_product.class
    )
    @ApiResponses
    @SLog("新增产品:${deviceProduct.name}")
    @SaCheckPermission("devcie.manage.product.create")
    public Result<?> create(@Param("..") Device_product deviceProduct, HttpServletRequest req) {
        deviceProduct.setCreatedBy(SecurityUtil.getUserId());
        deviceProductService.insert(deviceProduct);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改产品")
    @ApiFormParams(
            implementation = Device_product.class
    )
    @ApiResponses
    @SLog("修改产品:${deviceProduct.name}")
    @SaCheckPermission("devcie.manage.product.update")
    public Result<?> update(@Param("..") Device_product deviceProduct, HttpServletRequest req) {
        deviceProduct.setUpdatedBy(SecurityUtil.getUserId());
        deviceProductService.updateIgnoreNull(deviceProduct);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取Device_product")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("devcie.manage.product")
    public Result<?> getData(String id, HttpServletRequest req) {
        Device_product deviceProduct = deviceProductService.fetch(id);
        if (deviceProduct == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(deviceProduct);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除产品")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除产品:")
    @SaCheckPermission("devcie.manage.product.delete")
    public Result<?> delete(String id, HttpServletRequest req) {
        Device_product deviceProduct = deviceProductService.fetch(id);
        if (deviceProduct == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        deviceProductService.delete(id);
        req.setAttribute("_slog_msg", deviceProduct.getName());
        return Result.success();
    }
}