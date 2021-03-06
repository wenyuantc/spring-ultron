package org.springultron.logging.appender;

import ch.qos.logback.classic.LoggerContext;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springultron.logging.UltronLoggingProperties;
import org.springultron.logging.utils.LogstashUtils;

/**
 * Logstash Appender 输出Json
 * https://github.com/logstash/logstash-logback-encoder
 *
 * @author brucewuu
 * @date 2021/4/9 下午1:42
 */
public class LoggingLogstashAppender implements ILoggingAppender {
    private static final Logger log = LoggerFactory.getLogger(LoggingLogstashAppender.class);

    private static final String LOGSTASH_APPENDER_NAME = "LOGSTASH";

    private final UltronLoggingProperties properties;
    private final String customFieldsJson;

    public LoggingLogstashAppender(UltronLoggingProperties properties, Environment environment) {
        this.properties = properties;
        String appName = environment.getProperty("spring.application.name", "ultron-server");
        String profile = environment.getProperty("spring.profiles.active", "default");
        this.customFieldsJson = String.format("{\"appName\":\"%s\",\"profile\":\"%s\"}", appName, profile);
//        log.info(customFieldsJson);
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.start(context);
    }

    @Override
    public void start(LoggerContext context) {
        log.info("Logstash logging start.");
        reload(context);
    }

    @Override
    public void reset(LoggerContext context) {
        log.info("Logstash logging reset.");
        reload(context);
    }

    private void reload(LoggerContext context) {
        UltronLoggingProperties.Logstash logstash = properties.getLogstash();
        if (logstash.isEnabled()) {
            addLogstashTcpSocketAppender(context, logstash, customFieldsJson);
        }
    }

    private static void addLogstashTcpSocketAppender(LoggerContext context, UltronLoggingProperties.Logstash logstash, String customFieldsJson) {
        LogstashTcpSocketAppender logstashTcpSocketAppender = new LogstashTcpSocketAppender();
        logstashTcpSocketAppender.setName(LOGSTASH_APPENDER_NAME);
        logstashTcpSocketAppender.setContext(context);
        logstashTcpSocketAppender.addDestination(logstash.getDestinations());
        logstashTcpSocketAppender.setQueueSize(logstash.getQueueSize());
        logstashTcpSocketAppender.setWriteBufferSize(logstash.getWriteBufferSize());
        logstashTcpSocketAppender.setEncoder(logstashEncoder(customFieldsJson));
        logstashTcpSocketAppender.start();
        // 先删除，再添加
        context.getLogger(Logger.ROOT_LOGGER_NAME).detachAppender(LOGSTASH_APPENDER_NAME);
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(logstashTcpSocketAppender);
    }

    private static LogstashEncoder logstashEncoder(String customFields) {
        LogstashEncoder logstashEncoder = new LogstashEncoder();
        logstashEncoder.setThrowableConverter(LogstashUtils.throwableConverter());
        logstashEncoder.setCustomFields(customFields);
        return logstashEncoder;
    }
}
