package com.heyso.SeedBEApp.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Enumeration;

@Service
@RequiredArgsConstructor
public class ExceptionLogService {
    private final ExceptionLogMapper mapper;

    @Async
    public void saveAsync(Throwable ex, int status, String code, String message,
                          HttpServletRequest req, String traceId, String bodyText) {

        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stack = sw.toString();

        StringBuilder headers = new StringBuilder("{");
        Enumeration<String> names = req.getHeaderNames();
        boolean first = true;
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (!first) headers.append(",");
            headers.append("\"").append(name).append("\":\"")
                    .append(req.getHeader(name).replace("\"", "\\\""))
                    .append("\"");
            first = false;
        }
        headers.append("}");

        ExceptionLog log = ExceptionLog.builder()
                .occurredAt(LocalDateTime.now())
                .traceId(traceId)
                .status(status)
                .errorCode(code)
                .message(message)
                .path(req.getRequestURI())
                .httpMethod(req.getMethod())
                .queryString(req.getQueryString())
                .headersJson(headers.toString())
                .bodyText(bodyText)
                .stacktrace(stack)
                .build();

        mapper.insert(log);
    }
}
