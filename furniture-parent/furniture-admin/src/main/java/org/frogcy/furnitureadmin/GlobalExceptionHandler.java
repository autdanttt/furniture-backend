package org.frogcy.furnitureadmin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.frogcy.furnitureadmin.category.CategoryAlreadyExistsException;
import org.frogcy.furnitureadmin.category.CategoryNotFoundException;
import org.frogcy.furnitureadmin.product.ProductAlreadyExistsException;
import org.frogcy.furnitureadmin.product.impl.ProductNotFoundException;
import org.frogcy.furnitureadmin.security.jwt.JwtValidationException;
import org.frogcy.furnitureadmin.user.EmailAlreadyExistsException;
import org.frogcy.furnitureadmin.user.RoleNotFoundException;
import org.frogcy.furnitureadmin.user.UserNotFoundException;
import org.frogcy.furnitureadmin.user.impl.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleUserNotFoundException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleProductNotFoundException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleCategoryNotFoundException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }


    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleRoleNotFoundException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ExceptionHandler(JwtValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorDTO handleJwtValidationException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();
        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setPath(request.getServletPath());
        error.addError(ex.getMessage());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO handleProductAlreadyExistsException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO handleEmailAlreadyExistsException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO handleUserAlreadyExistsException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO handleCategoryAlreadyExistsException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleConstrainViolationException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();
        ConstraintViolationException  violationException = (ConstraintViolationException) ex;
        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(request.getServletPath());

        var constraintViolation =  violationException.getConstraintViolations();
        constraintViolation.forEach(constraint ->{
            error.addError(constraint.getPropertyPath() + ": "+ constraint.getMessage());
        });
        LOGGER.error(ex.getMessage(), ex);
        return error;
    }


    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleBadRequestException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleIllegalArgumentException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }



    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorDTO error = new ErrorDTO();
        error.setTimestamp(new Date());

        LOGGER.error(ex.getMessage(), ex);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(((ServletWebRequest) request).getRequest().getServletPath());
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(fieldError -> {
            error.addError(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        });

        return new ResponseEntity<>(error, headers, status);

    }
}
