package com.sunchaser.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc 消息协议头
 *
 * +-----------------------------------------------------------+
 * | 魔数 2byte | 协议版本号 1byte | 协议信息 1byte | 消息ID 8byte |
 * +-----------------------------------------------------------+
 * |         时间戳 8byte         |         消息长度 4byte       |
 * +-----------------------------------------------------------+
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcHeader implements Serializable {

    private static final long serialVersionUID = 3050587176252634022L;

    /**
     * 魔数
     * 天王盖地虎
     * 宝塔镇河妖
     */
    private short magic;

    /**
     * 版本号
     */
    private byte version;

    /**
     * 协议信息
     */
    private byte protocolInfo;

    /**
     * 消息ID
     */
    private long messageId;

    /**
     * 时间戳
     */
    private long ts;

    /**
     * 消息体长度
     */
    private int length;
}
