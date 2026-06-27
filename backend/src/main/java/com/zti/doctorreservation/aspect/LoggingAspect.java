package com.zti.doctorreservation.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspekt Spring AOP implementujący przekrojowe logowanie wywołań serwisów.
 * Automatycznie rejestruje każde wywołanie metody w warstwie serwisowej:
 * czas wykonania, argumenty, błędy oraz zdarzenia audytowe (rezerwacja, anulowanie).
 * Działa bez modyfikacji kodu biznesowego -- korzysta z mechanizmu proxy Spring AOP.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut obejmujący wszystkie metody w warstwie serwisowej aplikacji.
     */
    @Pointcut("execution(* com.zti.doctorreservation.service.*.*(..))")
    public void serviceLayer() {}

    /**
     * Pointcut obejmujący wszystkie metody w warstwie kontrolerów REST.
     */
    @Pointcut("execution(* com.zti.doctorreservation.controller.*.*(..))")
    public void controllerLayer() {}

    /**
     * Advice typu Around logujący każde wywołanie metody serwisowej.
     * Rejestruje nazwę metody, argumenty wywołania oraz czas wykonania.
     *
     * @param joinPoint punkt złączenia reprezentujący wywołanie metody
     * @return wynik wywołania oryginalnej metody
     * @throws Throwable jeśli oryginalna metoda rzuci wyjątek
     */
    @Around("serviceLayer()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        String user = getCurrentUser();
        log.info("[{}] Calling {} with args: {}", user, method, Arrays.toString(joinPoint.getArgs()));

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        log.info("[{}] {} completed in {}ms", user, method, elapsed);
        return result;
    }

    /**
     * Advice typu AfterThrowing logujący wyjątki rzucane przez metody serwisowe.
     *
     * @param joinPoint punkt złączenia w którym wystąpił wyjątek
     * @param ex        rzucony wyjątek
     */
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        String user = getCurrentUser();
        log.error("[{}] Exception in {}: {}", user, joinPoint.getSignature().toShortString(), ex.getMessage());
    }

    /**
     * Advice audytowy rejestrujący pomyślne rezerwacje wizyt.
     *
     * @param joinPoint punkt złączenia metody book()
     */
    @After("execution(* com.zti.doctorreservation.service.AppointmentService.book(..))")
    public void logAppointmentBooked(JoinPoint joinPoint) {
        log.info("[AUDIT] Appointment booked by user: {}", getCurrentUser());
    }

    /**
     * Advice audytowy rejestrujący anulowania wizyt.
     *
     * @param joinPoint punkt złączenia metody cancel()
     */
    @After("execution(* com.zti.doctorreservation.service.AppointmentService.cancel(..))")
    public void logAppointmentCancelled(JoinPoint joinPoint) {
        log.info("[AUDIT] Appointment cancelled by user: {}", getCurrentUser());
    }

    /**
     * Pobiera nazwę aktualnie zalogowanego użytkownika z kontekstu Spring Security.
     *
     * @return nazwa użytkownika lub "anonymous" gdy brak uwierzytelnienia
     */
    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}
