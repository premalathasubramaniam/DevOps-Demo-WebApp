package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.apache.commons.collections.FactoryUtils;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;

import servlet.createpage;

public class createpage_junit {
	
	
	public createpage obj = new createpage();
	
	
	@Test
	public void test() throws ServletException, IOException
	{
		
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		MockHttpServletResponse response = new MockHttpServletResponse(); response.setContentType("text/html");
		 request.addParameter("product", "BROADBAND");
		 request.addParameter("problemdescription", "ADDITIONAL CONNECTION");
		 request.addParameter("contact", "739222687");
		 request.addParameter("date", "13/04/2017");
		 request.addParameter("status", "NEW");
		
		obj.doPost(request, response);
		
	}
	
	
	
	
	
	
	
	
	

}
