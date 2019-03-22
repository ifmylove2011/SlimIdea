package com.xter.slimidea.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by XTER on 2019/3/21.
 * 时间会给我
 */
@Aspect
public class TimeMonitorAspect {
	@Pointcut("call(* **initView())")
	public void time() {

	}

	@Around("time()")
	public void timeTrace(ProceedingJoinPoint proceedingJoinPoint) {
		long currentTime = System.currentTimeMillis();
		try {
			proceedingJoinPoint.proceed();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		long delay = System.currentTimeMillis() - currentTime;
		System.out.println("+++++++ " + delay + "ms +++++++");
	}
}
