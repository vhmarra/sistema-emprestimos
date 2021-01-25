package br.com.victor.emprestimos.exceptions;

import br.com.victor.emprestimos.dtos.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ControllerAdvice
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RestExceptionHandler {

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<?> handleNotFoundException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ErrorResponse(ex.getMessage(), 204L),new HttpHeaders(), NO_CONTENT);
    }

    @ExceptionHandler({ InvalidTokenException.class })
    public ResponseEntity<?> handleInvalidTokenException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ErrorResponse(ex.getMessage(), 403L),new HttpHeaders(), FORBIDDEN);
    }

    @ExceptionHandler({ InvalidClienteException.class })
    public ResponseEntity<?> handleInvalidClienteException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ErrorResponse(ex.getMessage(), 403L),new HttpHeaders(), FORBIDDEN);
    }

    @ExceptionHandler({ InvalidInputException.class })
    public ResponseEntity<?> handleInvalidInputException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ErrorResponse(ex.getMessage(), 400L),new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler({ InvalidCredencialsException.class })
    public ResponseEntity<?> handleInvalidCredencialException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ErrorResponse(ex.getMessage(), 403L),new HttpHeaders(), FORBIDDEN);
    }

    @ExceptionHandler({ ForbiddenException.class })
    public ResponseEntity<?> handleForbiddenException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ErrorResponse(ex.getMessage(), 403L),new HttpHeaders(), FORBIDDEN);
    }

}
