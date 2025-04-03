package practice_spring.basic_app.resolver;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.repository.AppUserRepository;

@Component
@AllArgsConstructor
@Slf4j
public class AppUserArgumentResolver implements HandlerMethodArgumentResolver {

    private AppUserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AppUser.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = servletRequest.getHeader("X-API-TOKEN");
        log.info("TOKEN {}", token);

        if(token==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        AppUser user = userRepository.findFirstByToken(token)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized"));
        log.info("USER {}", user);

        if(user.getTokenExpiredAt() < System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return user;
    }
}
