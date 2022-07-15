package com.sunchaser.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc 消息协议
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/6/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcProtocol<T> implements Serializable {

    private static final long serialVersionUID = 3022652409163567503L;

    /**
     * 消息头
     */
    private RpcHeader rpcHeader;

    /**
     * 消息体
     */
    private T content;
}
