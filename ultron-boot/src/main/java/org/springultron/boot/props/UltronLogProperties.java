package org.springultron.boot.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springultron.boot.enums.LogLevel;

/**
 * 请求日志配置
 *
 * @Auther: brucewuu
 * @Date: 2019-06-17 17:31
 * @Description:
 */
@ConfigurationProperties(LogLevel.ULTRON_LOG_PROPS_PREFIX)
public class UltronLogProperties {
    /**
     * 是否开启日志
     */
    private boolean enable = true;
    /**
     * 日志等级
     */
    private LogLevel level = LogLevel.BASIC;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }
}