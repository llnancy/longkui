package com.sunchaser.shushan.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc response
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -9107303026305544424L;

    /**
     * 请求成功时返回的数据
     */
    private Object result;

    /**
     * 请求失败时返回的错误信息
     */
    private String errorMsg;
}
