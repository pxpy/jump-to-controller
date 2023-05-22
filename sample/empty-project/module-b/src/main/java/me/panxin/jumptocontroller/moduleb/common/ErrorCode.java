package me.panxin.jumptocontroller.moduleb.common;

/**
 * @author PanXin
 */

public enum ErrorCode {
    // 错误码
    SUCCESS("00000", "成功"),
    PARAM_ERROR("A0400","请求参数错误"),
    SYSTEM_ERROR("B0001", "系统执行出错"),

    //自定义业务错误码
    RATE_LIMIT_ERROR("3005", "限流异常"),
    FILE_UPLOAD_FAILURE("3006", "文件上传失败"),
    ;

    /**
     * 编码
     */
    private final String code;

    /**
     * 描述信息
     */
    private final String desc;
     ErrorCode(String code, String desc){
         this.code = code;
         this.desc = desc;
     }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
