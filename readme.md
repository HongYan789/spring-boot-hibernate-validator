# spring-boot-hibernate-validator
该工程主要采用hibernate-validator的注解方式实现一些繁琐的校验工作，使代码更加简洁明了，用户只需关注核心业务代码即可

* 本项目介绍了3种方式实现功能校验操作
1. post请求+@Valid+@RequestBody+BindingResult方式实现(对象中实现@NotBlank等常规注解)
````
@PostMapping("/demo1")
    public R validDemo1(@Valid @RequestBody User user, BindingResult result){
        StringBuilder sb = new StringBuilder();
        if(result.hasErrors()){
            for (ObjectError error :result.getAllErrors()){
                log.error("error:{}",error.getDefaultMessage());

                FieldError fieldError = (FieldError) error;
                log.error("fieldError:{}",fieldError.getDefaultMessage());
                sb.append(error.getDefaultMessage()).append(",");
            }
        }
        return new R(OpCode.Success,"success",sb.deleteCharAt(sb.length()-1).toString());
        return new R(OpCode.Success,"success","");
    }
````
2.post请求+@Valid+@RequestBody+aop方式实现(对象中实现@NotBlank等常规注解)
````
@PostMapping("/demo1")
    public R validDemo1(@Valid @RequestBody User user, BindingResult result){
        //由于实现了ValidHandlerAop通用异常处理，因此此处代码可剔除，通过aop统一处理
        return new R(OpCode.Success,"success","");
    }
    
    
@Aspect
@Component
@ControllerAdvice
@Slf4j
public class ValidHandlerAop {

    @Pointcut("execution(* com.hongyan.study.springboothibernatevalidator.controller.*.*.*(..))")
    public void handlerAop() {
    }

    @Around("handlerAop()")
    public R around(ProceedingJoinPoint pjp) {
        try {
            Object[] objects = pjp.getArgs();
            for (Object obj : objects) {
                if (obj instanceof BindingResult) {
                    BindingResult result = (BindingResult) obj;
                    StringBuilder sb = new StringBuilder();
                    if (result.hasErrors()) {
                        for (ObjectError error : result.getAllErrors()) {
                            FieldError fieldError = (FieldError) error;
                            log.error("error:{}", error.getDefaultMessage());
                            log.error("fieldError:{}", fieldError.getDefaultMessage());
                            if(sb.length() > 0){//这种写法比无脑的都新增逗号，然后剔除最后一个字段优雅，且不会出现数组下标越界的风险
                                sb.append(",");
                            }
                            sb.append(error.getDefaultMessage());
                        }
                        return new R(OpCode.InvalidArgument, sb.toString(), null);
                    }
                }
            }
            Object object = pjp.proceed(pjp.getArgs());
            return (R) object;
        }catch (ConstraintViolationException e){
            log.error("valid校验异常",e);
            StringBuilder buffer = new StringBuilder();
            for (ConstraintViolation violation : e.getConstraintViolations()) {
                if (buffer.length() > 0) {//这种写法比无脑的都新增逗号，然后剔除最后一个字段优雅，且不会出现数组下标越界的风险
                    buffer.append(",");
                }
                buffer.append(violation.getMessage());
            }
            return new R(OpCode.InvalidArgument, buffer.toString(), null);
        }catch (Throwable throwable) {
            log.error("aop解析异常",throwable);
            throwable.printStackTrace();
            return new R(OpCode.Internal,"系统内部异常",null);
        }

    }
}    
````

3.get请求+@NotBlank等常规注解+exceptionHandler
````
@GetMapping("/demo2")
    public R validDemo2(@NotBlank(message = "name不能为空") @RequestParam("name") String name,
                        @Min(value = 10,message = "idCard最小10位数") @NotNull(message = "idCard不能为空") @RequestParam(name="idCard", required = true) Integer idCard){
        StringBuilder sb = new StringBuilder();
        return new R(OpCode.Success,"success",null);
    }
    


@ControllerAdvice
@Component
@Slf4j
public class GlobalExceptionHandler {

    //捕获全局异常，处理所有不可知的异常
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handle(Exception exception, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        if(exception instanceof ConstraintViolationException){
            ConstraintViolationException exs = (ConstraintViolationException) exception;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                if (sb.length() > 0) {//这种写法比无脑的都新增逗号，然后剔除最后一个字段优雅，且不会出现数组下标越界的风险
                    sb.append(",");
                }
                sb.append(item.getMessage());
            }
            return new R(OpCode.InvalidArgument,sb.toString(),request.getRequestURL());
        }
        return new R(OpCode.InvalidArgument,exception.getMessage(),request.getRequestURL());
    }

    //处理自定义异常
    @ExceptionHandler(value= MyException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMyException(MyException e, HttpServletRequest request) {
        //返回Json数据，由前端进行界面跳转
        Map<String, Object> map = new HashMap<>();
        map.put("code", e.getCode());
        map.put("msg", e.getMsg());
        map.put("url", request.getRequestURL());
        log.error("custom exception ...{}",map);
        return map;
    }

}    
    

````
4.采用自定义异常+exceptionHandler
````
@GetMapping("/demo3")
    public Object validDemo3(@RequestParam("name") String name,
                        Integer idCard){
        throw new MyException(OpCode.Internal,"custom exception...");
    }
    
//处理自定义异常
    @ExceptionHandler(value= MyException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMyException(MyException e, HttpServletRequest request) {
        //返回Json数据，由前端进行界面跳转
        Map<String, Object> map = new HashMap<>();
        map.put("code", e.getCode());
        map.put("msg", e.getMsg());
        map.put("url", request.getRequestURL());
        log.error("custom exception ...{}",map);
        return map;
    }
        
````

