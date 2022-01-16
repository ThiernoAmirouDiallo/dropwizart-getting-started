import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.PatternLayout
import static ch.qos.logback.classic.Level.INFO

scan("30 seconds")
def LOG_PATH = "logs"
def LOG_ARCHIVE = "${LOG_PATH}/archive"
appender("Console-Appender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy.MM.dd;HH:mm:ss.SSS} %-5p [%c] [%t] %m - %n"
    }
}

appender("RollingFile-Appender", RollingFileAppender) {
    file = "${LOG_PATH}/rollingfile.log"
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${LOG_ARCHIVE}/rollingfile.log%d{yyyy-MM-dd}.%i.log.gz"
        maxHistory = 30
        totalSizeCap = "500MB"
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy.MM.dd;HH:mm:ss.SSS} %-5p [%c] [%t] %m - %n"
        outputPatternAsHeader = true
    }
}
appender("Async-Appender", AsyncAppender) {
    appenderRef("RollingFile-Appender")
}

logger("com.thierno.dropwizard.service", INFO, ["Console-Appender", "Async-Appender"], false)
root(INFO, ["Console-Appender"])