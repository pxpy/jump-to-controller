package me.panxin.jumptocontroller.moduleb.feign;

import me.panxin.jumptocontroller.moduleb.common.Result;
import me.panxin.jumptocontroller.moduleb.vo.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author PanXin
 * @version $ Id: UserFeignClientInvokes, v 0.1 2023/05/22 20:29 PanXin Exp $
 */
@FeignClient(value = "user-service")
public interface UserFeignClientInvokes {

    @RequestMapping(value = "/user/query", method = RequestMethod.GET)
    Result<UserVo> queryIntegral(@RequestParam("userId") String userId);
}
