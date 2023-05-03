package com.pos.monitoring.exceptions;

import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.utils.ServerUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = ValidatorException.class)
    public SingleResponse error(ValidatorException e, HttpServletRequest request) {
        logger.error("unknown url ==>> {} message  ==>> {} :: cause ==>>{} :: trace ==>>{} ", request.getRequestURI(), e.getMessage(), e.getCause(), ServerUtils.tracer(e.getStackTrace()));
        return new SingleResponse(e.getMessage(),400);
    }

    @ExceptionHandler(value = Exception.class)
    public SingleResponse responseEntity(Exception e) {
        logger.error("unknown  message  ==>> {} :: cause ==>>{} :: trace ==>>{} ", e.getMessage(), e.getCause(), ServerUtils.tracer(e.getStackTrace()));
        return new SingleResponse(e.getMessage(),400);
    }

}
