package cz.mzk.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class SolrCachingHeaderFilter implements Filter {

	private static final String MIN_REQUEST_TIME_PARAM = "minRequestTime"; 

	private int minRequestTime = 500;

	private static class CachingHttpServletResponseWrapper extends
			HttpServletResponseWrapper {

		private final ByteArrayOutputStream output = new ByteArrayOutputStream();

		public CachingHttpServletResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return new ServletOutputStream() {

				@Override
				public void write(int b) throws IOException {
					output.write(b);
				}

			};
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			return new PrintWriter(new OutputStreamWriter(output));
		}

		public byte[] getOutput() {
			return output.toByteArray();
		}

	}

	public void init(FilterConfig config) throws ServletException {
		if (config.getInitParameter(MIN_REQUEST_TIME_PARAM) != null) {
			minRequestTime = Integer.valueOf(config.getInitParameter(MIN_REQUEST_TIME_PARAM));
		}
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		CachingHttpServletResponseWrapper responseWrapper = new CachingHttpServletResponseWrapper(
				response);
		long startTime = System.currentTimeMillis();
		chain.doFilter(request, responseWrapper);
		long endTime = System.currentTimeMillis();
		long requestTime = endTime - startTime;
		if (requestTime <= minRequestTime) {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		}
		byte[] output = responseWrapper.getOutput();
		response.setContentLength(output.length);
		response.getOutputStream().write(output);
		response.getOutputStream().close();
	}

	public void destroy() {
	}

}
