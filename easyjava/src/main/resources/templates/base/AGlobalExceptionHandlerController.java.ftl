package ${packages.controller};

import ${packages.enums}.ResponseCodeEnum;
import ${packages.exception}.BusinessException;
import ${packages.vo}.ResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class AGlobalExceptionHandlerController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(AGlobalExceptionHandlerController.class);

    @ExceptionHandler(Exception.class)
    public ResponseVO<?> handleException(Exception e, HttpServletRequest request) {
        logger.error("请求错误，请求地址: {}, 错误信息: {}", request.getRequestURI(), e.getMessage(), e);
        ResponseVO<Object> response = new ResponseVO<>();
        response.setStatus(STATUS_ERROR);
        if (e instanceof NoHandlerFoundException) {
            response.setCode(ResponseCodeEnum.CODE_404.getCode());
            response.setInfo(ResponseCodeEnum.CODE_404.getMsg());
        } else if (e instanceof BusinessException biz) {
            response.setCode(biz.getCode());
            response.setInfo(biz.getMessage());
        } else if (e instanceof BindException
                || e instanceof MethodArgumentNotValidException
                || e instanceof IllegalArgumentException) {
            response.setCode(ResponseCodeEnum.CODE_600.getCode());
            response.setInfo(ResponseCodeEnum.CODE_600.getMsg());
        } else if (e instanceof DuplicateKeyException) {
            response.setCode(ResponseCodeEnum.CODE_601.getCode());
            response.setInfo(ResponseCodeEnum.CODE_601.getMsg());
        } else {
            response.setCode(ResponseCodeEnum.CODE_500.getCode());
            response.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        }
        return response;
    }
}
