package me.panxin.jumptocontroller.modulea.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import me.panxin.jumptocontroller.modulea.common.Result;
import me.panxin.jumptocontroller.modulea.vo.UserVo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author PanXin
 * @version $ Id: UserController, v 0.1 2023/05/22 20:27 PanXin Exp $
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 查询
     * 查询用户仅作展示
     *
     * @param userId 用户id
     * @return {@link Result}<{@link UserVo}>
     */
    @ApiOperation("查询用户")
    @GetMapping(value = "/query")
    public Result<UserVo> query(
            @ApiParam(name = "userId", value = "用户ID", required = true) @RequestParam(value = "userId", required = true) String userId) {

        return null;
    }
    @ApiOperation("删除用户")
    @GetMapping(value = "/delete")
    public Result<UserVo> delete(
            @ApiParam(name = "userId", value = "用户ID", required = true) @RequestParam(value = "userId", required = true) String userId) {

        return null;
    }
}