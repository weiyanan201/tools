package wei.tools.aop;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import wei.tools.entity.PageRequest;
import wei.tools.entity.PageResult;

import java.util.List;

@Aspect
@Component
public class PageAspect {

    @Around("@annotation(wei.tools.aop.WithPage)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();
        PageRequest request = null;
        PageResult result = null;

        for (Object arg : args ){
            if (arg instanceof PageRequest){
                request = (PageRequest) arg;
            }else if(arg instanceof PageResult){
                result = (PageResult) arg;
            }
        }
        if (request==null || result==null){
            throw new Exception("Parameter abnormal");
        }
        Object o = null;
        try{
            PageHelper.startPage(request.getPageNum(),request.getPageSize());
            o = joinPoint.proceed();
            PageInfo info = new PageInfo((List) o);
            result.setContent(info.getList());
            result.setPageNum(info.getPageNum());
            result.setPageSize(info.getPageSize());
            result.setTotalPages(info.getPages());
            result.setTotalSize(info.getTotal());
        }catch (Exception e){
            throw e;
        }finally {
            PageHelper.clearPage();
        }
        return o;
    }

}
