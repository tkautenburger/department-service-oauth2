package de.legendlime.departmentService.config.logging;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.opentracing.Tracer;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseLoggingFilter implements Filter {

	public static final String TRACE_ID = "x-trace-id";
	private static final Logger logger = LoggerFactory.getLogger(ResponseLoggingFilter.class);

	@Autowired
	Tracer tracer;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

		String traceId = httpServletRequest.getHeader(TRACE_ID);
		if (traceId == null || traceId.isEmpty()) {
			if (tracer.activeSpan() != null)
				traceId = tracer.activeSpan().context().toTraceId();
			else {
				traceId = tracer.buildSpan("department-service").start().context().toTraceId();
			}
		}
		logger.info("Incoming Trace-ID: {}", traceId);

		// Add trace ID to the response
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
		if (httpServletResponse.getHeader(TRACE_ID) == null)
			httpServletResponse.addHeader(TRACE_ID, traceId);

		if (tracer.activeSpan() != null)
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
