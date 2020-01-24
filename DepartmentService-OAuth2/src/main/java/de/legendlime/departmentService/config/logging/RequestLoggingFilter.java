package de.legendlime.departmentService.config.logging;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import io.opentracing.Tracer;


@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements Filter {

	public static final String TRACE_ID = "x-trace-id";
	private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

	@Autowired
	Tracer tracer;

	@Autowired
	private ApplicationContext applicationContext;
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		boolean buildSpan = false;
		String traceId = httpServletRequest.getHeader(TRACE_ID);
		if (traceId == null || traceId.isEmpty()) {
			if (tracer.activeSpan() != null)
				traceId = tracer.activeSpan().context().toTraceId();
			else {
				traceId = tracer.buildSpan(applicationContext.getId()).start().context().toTraceId();
				buildSpan = true;
			}
		}
		logger.info("Incoming Trace-ID: {}", traceId);

		if (buildSpan && tracer.activeSpan() != null)
			tracer.activeSpan().finish();
		
		filterChain.doFilter(httpServletRequest, servletResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
	
}
