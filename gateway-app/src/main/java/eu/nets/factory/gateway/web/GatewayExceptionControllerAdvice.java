package eu.nets.factory.gateway.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import eu.nets.factory.gateway.GatewayException;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@ControllerAdvice
public class GatewayExceptionControllerAdvice {
    private final Logger log = getLogger(getClass());

    private final Charset utf8 = Charset.forName("utf-8");

    @ExceptionHandler(Exception.class)
    public void constraintException(Exception e, HttpServletResponse res) throws IOException {
        if (res.isCommitted()) {
            log.warn("Got exception but the output has either been written", e);
            return;
        }

        ConstraintViolationException cve = findRelatedException(e, ConstraintViolationException.class);
        GatewayException ge = findRelatedException(e, GatewayException.class);

        int status = INTERNAL_SERVER_ERROR.value();
        StringBuilder s;

        if (cve != null) {
            status = BAD_REQUEST.value();
            s = new StringBuilder("Internal validation error: ");
            for (ConstraintViolation<?> v : cve.getConstraintViolations()) {
                s.append("The field '").append(v.getPropertyPath()).append("' ").append(v.getMessage()).append(". ");
            }
        } else if(ge != null) {
            status = ge.getHttpStatus().value();
            s = new StringBuilder(ge.getMessage());
        } else {
            StringWriter buf = new StringWriter();
            e.printStackTrace(new PrintWriter(buf));
            s = new StringBuilder(buf.toString());
        }

        String message = s.toString();
        log.warn("Validation error: {}", message);

        res.setStatus(status);
        res.setContentType("text/plain;charset=utf-8");
        ServletOutputStream os = res.getOutputStream();
        os.write(message.getBytes(utf8));
        os.println();
        os.flush();
    }

    private <T extends Exception> T findRelatedException(Throwable e, Class<T> exceptionType) {
        if (e == null)
            return null;
        if (exceptionType.isAssignableFrom(e.getClass()))
            return exceptionType.cast(e);

        Exception relatedByCause = findRelatedException(e.getCause(), exceptionType);
        return exceptionType.cast(relatedByCause);
    }
}
