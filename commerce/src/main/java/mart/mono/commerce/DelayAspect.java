package mart.mono.commerce;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Profile("chaos")
@Slf4j
public class DelayAspect {

    private final Random random = new Random();

    @Around("execution(* mart.mono.commerce.cart.CartService.*(..))")
    public Object handleCartService(ProceedingJoinPoint pjp) throws Throwable {
        delay();
        return pjp.proceed();
    }

    @Around("execution(* mart.mono.commerce.purchase.PurchasesService.*(..))")
    public Object handlePurchaseService(ProceedingJoinPoint pjp) throws Throwable {
        delay();
        return pjp.proceed();
    }

    @SneakyThrows
    private void delay() {
        int nextInt = random.nextInt(100);
        if (nextInt < 10) {
            log.warn("Waiting on mission critical operation");
            TimeUnit.SECONDS.sleep(1);
        }
    }
}