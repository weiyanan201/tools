package wei.tools.aop;


import cn.hutool.core.exceptions.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import wei.tools.entity.HttpResult;
import wei.tools.enums.ErrorCodeEnum;
import wei.tools.exception.ToolsException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static String INTERNAL_SERVER_ERROR = "系统服务异常，请联系管理员";

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSize ;

    private final static Logger logger = LoggerFactory.getLogger(wei.tools.aop.GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public HttpResult allExceptionHandler(HttpServletRequest request,
                                          Exception exception) throws Exception {
        logger.error("exception : " ,exception );

//        System.out.println("ExceptionUtil.getMessage() : " + ExceptionUtil.getMessage(exception));
//        System.out.println( ExceptionUtil.getSimpleMessage(exception));
//        System.out.println("getRootCauseMessage : " + ExceptionUtil.getRootCauseMessage(exception));

        HttpResult result = HttpResult.error(INTERNAL_SERVER_ERROR,ExceptionUtil.getRootCauseMessage(exception));
        return result;
    }

    @ExceptionHandler(value = ToolsException.class)
    public HttpResult kmsException(HttpServletRequest request,
                                   ToolsException exception){
        logger.error(exception.getMessage(),exception);
        HttpResult result = HttpResult.error(exception.getErrorMsg());
        return result;
    }





    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public HttpResult missingServletRequestParameterException(MissingServletRequestParameterException exception){
        logger.error(exception.getMessage(),exception);
        return HttpResult.error(ErrorCodeEnum.REQUEST_PARAMETER_MISSING.getCode(),exception.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public HttpResult methodArgumentNotValidException(MethodArgumentNotValidException exception){
        logger.error(exception.getMessage(),exception);
        List<ObjectError> errors =  exception.getBindingResult().getAllErrors();
        StringBuilder sb = new StringBuilder();
        for (ObjectError error : errors){
            sb.append(error.getDefaultMessage()).append("\r\n");
        }
        return HttpResult.error(ErrorCodeEnum.REQUEST_PARAMETER_MISSING.getCode(),sb.toString());
    }


}
