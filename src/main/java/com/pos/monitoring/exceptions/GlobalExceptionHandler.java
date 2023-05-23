package com.pos.monitoring.exceptions;

import com.pos.monitoring.dtos.response.SingleResponse;
import com.pos.monitoring.utils.ServerUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@RestController
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;
    Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = ValidatorException.class)
    public SingleResponse validation(ValidatorException validatorException, HttpServletRequest request) {
        logger.error("unknown url ==>> {} message  ==>> {} :: cause ==>>{} :: trace ==>>{} ", request.getRequestURI(), validatorException.getMessage(), validatorException.getCause(), ServerUtils.tracer(validatorException.getStackTrace()));
        return new SingleResponse(messageSource.getMessage(validatorException.getMessage(),validatorException.objects,request.getLocale()),400);
    }

    @ExceptionHandler(value = Exception.class)
    public SingleResponse exception(Exception e,HttpServletRequest request) {
        logger.error("unknown  message  ==>> {} :: cause ==>>{} :: trace ==>>{} ", e.getMessage(), e.getCause(), ServerUtils.tracer(e.getStackTrace()));
        return new SingleResponse(messageSource.getMessage(e.getMessage(),null,request.getLocale()),400);
    }

    @ExceptionHandler(value = ParameterException.class)
    public SingleResponse paramException(ParameterException e,HttpServletRequest request) {
        logger.error("unknown  message  ==>> {} :: cause ==>>{} :: trace ==>>{} ", e.getMessage(), e.getCause(), ServerUtils.tracer(e.getStackTrace()));
        return new SingleResponse(messageSource.getMessage(e.getMessage(), e.getParams(), request.getLocale()),400);
    }
}
