package cn.percent.usersystemjdk17.common.enums;

import lombok.Getter;

/**
 * @author: zhangpengju
 * Date: 2021/12/16
 * Time: 11:13
 * Description:
 */
@Getter
public enum QrCodeStatusEnum {

    /**
     * **未被扫描**
     */
    NOT_SCAN(0, "未被扫描"),
    /**
     * **被扫描**
     */
    SCANNED(1, "被扫描"),
    /**
     * **确认完后**
     */
    VERIFIED(2, "确认完后"),
    /**
     * **过期**
     */
    EXPIRED(3, "过期"),
    /**
     * **完成**
     */
    FINISH(4, "完成");

    /**
     * **枚举值**
     */
    private final Integer value;

    /**
     * **枚举描述**
     */
    private final String desc;

    /**
     * 构造方法
     *
     * @param value 枚举值
     * @param desc  枚举描述
     */
    QrCodeStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }


}
