package com.sunchaser.shushan.rpc.core.registry.impl;

import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.MyDataCenterInfo;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/31
 */
@Setter
public class ConfigurableEurekaInstanceConfig implements EurekaInstanceConfig {

    private String appname;

    private String appGroupName;

    private boolean instanceEnabledOnit;

    private int nonSecurePort;

    private int securePort;

    private boolean nonSecurePortEnabled = true;

    private boolean securePortEnabled;

    private int leaseRenewalIntervalInSeconds = 30;

    private int leaseExpirationDurationInSeconds = 90;

    private String virtualHostName = "unknown";

    private String instanceId;

    private String secureVirtualHostName = "unknown";

    private String aSGName;

    private Map<String, String> metadataMap = new HashMap<>();

    private DataCenterInfo dataCenterInfo = new MyDataCenterInfo(DataCenterInfo.Name.MyOwn);

    private String ipAddress;

    private String statusPageUrlPath;

    private String statusPageUrl;

    private String homePageUrlPath = "/";

    private String homePageUrl;

    private String healthCheckUrlPath;

    private String healthCheckUrl;

    private String secureHealthCheckUrl;

    private String namespace = "eureka";

    private String hostname;

    private String[] defaultAddressResolutionOrder = new String[0];

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String getAppname() {
        return appname;
    }

    @Override
    public String getAppGroupName() {
        return appGroupName;
    }

    @Override
    public boolean isInstanceEnabledOnit() {
        return instanceEnabledOnit;
    }

    @Override
    public int getNonSecurePort() {
        return nonSecurePort;
    }

    @Override
    public int getSecurePort() {
        return securePort;
    }

    @Override
    public boolean isNonSecurePortEnabled() {
        return nonSecurePortEnabled;
    }

    @Override
    public boolean getSecurePortEnabled() {
        return securePortEnabled;
    }

    @Override
    public int getLeaseRenewalIntervalInSeconds() {
        return leaseRenewalIntervalInSeconds;
    }

    @Override
    public int getLeaseExpirationDurationInSeconds() {
        return leaseExpirationDurationInSeconds;
    }

    @Override
    public String getVirtualHostName() {
        return virtualHostName;
    }

    @Override
    public String getSecureVirtualHostName() {
        return secureVirtualHostName;
    }

    @Override
    public String getASGName() {
        return aSGName;
    }

    @Override
    public String getHostName(boolean refresh) {
        return hostname;
    }

    @Override
    public Map<String, String> getMetadataMap() {
        return metadataMap;
    }

    @Override
    public DataCenterInfo getDataCenterInfo() {
        return dataCenterInfo;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String getStatusPageUrlPath() {
        return statusPageUrlPath;
    }

    @Override
    public String getStatusPageUrl() {
        return statusPageUrl;
    }

    @Override
    public String getHomePageUrlPath() {
        return homePageUrlPath;
    }

    @Override
    public String getHomePageUrl() {
        return homePageUrl;
    }

    @Override
    public String getHealthCheckUrlPath() {
        return healthCheckUrlPath;
    }

    @Override
    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    @Override
    public String getSecureHealthCheckUrl() {
        return secureHealthCheckUrl;
    }

    @Override
    public String[] getDefaultAddressResolutionOrder() {
        return defaultAddressResolutionOrder;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }
}
