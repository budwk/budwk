package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.sys.models.Sys_post;
import com.budwk.app.sys.services.SysPostService;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.excel.utils.ExcelUtil;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/sys/post")
@SLog(tag = "职务管理")
@ApiDefinition(tag = "职务管理")
@Slf4j
public class SysPostController {
    @Inject
    private SysPostService sysPostService;

    @At("/import")
    @POST
    @Ok("json:full")
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:fileUpload"})
    @SaCheckLogin
    @ApiOperation(name = "导入数据")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", example = "", description = "文件表单对象名"),
                    @ApiFormParam(name = "cover", example = "", description = "是否覆盖"),
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses
    public Result<?> importData(@Param("Filedata") TempFile tf, @Param(value = "cover", df = "false") boolean cover, HttpServletRequest req, AdaptorErrorContext err) {
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
                ExcelUtil<Sys_post> util = new ExcelUtil<>(Sys_post.class);
                List<Sys_post> list = util.importExcel(tf.getInputStream());
                sysPostService.importData(tf.getSubmittedFileName(), list, cover, SecurityUtil.getUserId(), SecurityUtil.getUserLoginname());
                return Result.success("文件上传成功，处理结果将通过站内信通知");
            } catch (Exception e) {
                throw new BaseException("文件处理失败");
            }
        }

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
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.post")
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(sysPostService.listPage(pageNo, pageSize, Cnd.NEW().asc("location")));
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "新增职务")
    @ApiFormParams(
            implementation = Sys_post.class
    )
    @ApiResponses
    @SLog("新增职务,职务:${post.name}")
    @SaCheckPermission("sys.manage.post.create")
    public Result<?> create(@Param("..") Sys_post post, HttpServletRequest req) {
        if (sysPostService.count(Cnd.where("code", "=", post.getCode())) > 0) {
            return Result.error("职务编号已存在");
        }
        post.setCreatedBy(SecurityUtil.getUserId());
        sysPostService.insert(post);
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改职务")
    @ApiFormParams(
            implementation = Sys_post.class
    )
    @ApiResponses
    @SLog("修改职务,职务:${post.name}")
    @SaCheckPermission("sys.manage.post.update")
    public Result<?> update(@Param("..") Sys_post post, HttpServletRequest req) {
        if (sysPostService.count(Cnd.where("code", "=", post.getCode()).and("id", "<>", post.getId())) > 0) {
            return Result.error("职务编号已存在");
        }
        post.setUpdatedBy(SecurityUtil.getUserId());
        sysPostService.updateIgnoreNull(post);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取职务")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.post")
    public Result<?> getData(String id, HttpServletRequest req) {
        Sys_post post = sysPostService.fetch(id);
        if (post == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(post);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @ApiOperation(name = "删除职务")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    @SLog("删除职务,职务:")
    @SaCheckPermission("sys.manage.post")
    public Result<?> delete(String id, HttpServletRequest req) {
        Sys_post post = sysPostService.fetch(id);
        if (post == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        sysPostService.delete(id);
        req.setAttribute("_slog_msg", post.getName());
        return Result.success();
    }

    @At
    @Ok("json")
    @POST
    @ApiOperation(name = "修改职务排序")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "location", description = "排序序号"),
                    @ApiFormParam(name = "id", description = "主键ID")
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.post.update")
    public Result<?> location(@Param("location") int location, @Param("id") String id, HttpServletRequest req) {
        sysPostService.update(Chain.make("location", location), Cnd.where("id", "=", id));
        return Result.success();
    }
}
