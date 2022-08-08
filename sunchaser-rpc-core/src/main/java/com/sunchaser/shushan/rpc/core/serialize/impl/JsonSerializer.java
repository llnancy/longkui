package com.sunchaser.shushan.rpc.core.serialize.impl;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import lombok.SneakyThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 基于JSON的序列化器实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
public class JsonSerializer implements Serializer {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final ObjectMapper OM;

    static {
        OM = initObjectMapper();
    }

    private static ObjectMapper initObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 忽略空bean转对象异常
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 忽略在json字符串中存在，但是在Java类中不存在对应属性时抛出的异常
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 解析含有结束语控制字符(如：ASCII<32，包含制表符\t、换行符\n和回车\r)
        mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        // 配置使用Java8的LocalDateTime时间模块，避免序列化和反序列化出错
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
        mapper.registerModule(javaTimeModule);
        return mapper;
    }

    /**
     * 将对象进行序列化
     *
     * @param obj 待序列化的对象
     * @return 序列化后的byte字节数组
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    public <T> byte[] serialize(T obj) {
        return OM.writeValueAsBytes(obj);
    }

    /**
     * 将二进制字节数组进行反序列化
     *
     * @param data  二进制字节数组
     * @param clazz 待反序列化的class类型
     * @return 反序列化后的对象
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return OM.readValue(data, clazz);
    }
}
