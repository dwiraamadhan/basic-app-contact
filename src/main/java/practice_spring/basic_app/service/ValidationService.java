package practice_spring.basic_app.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import practice_spring.basic_app.dto.RegisterUserRequest;

import java.util.Set;

@Service
@AllArgsConstructor
public class ValidationService {

    private Validator validator;

    public void validate(Object request){
        //validate object request
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
