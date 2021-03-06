/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.exception;

import static org.springframework.web.servlet.DispatcherServlet.EXCEPTION_ATTRIBUTE;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.app.rest.security.RestAuthenticationService;
import org.dspace.authorize.AuthorizeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.QueryMethodParameterConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This Controller advice will handle all exceptions thrown by the DSpace API module
 *
 * @author Tom Desair (tom dot desair at atmire dot com)
 * @author Frederic Van Reet (frederic dot vanreet at atmire dot com)
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@ControllerAdvice
public class DSpaceApiExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    @Autowired
    private RestAuthenticationService restAuthenticationService;

    @ExceptionHandler({AuthorizeException.class, RESTAuthorizationException.class})
    protected void handleAuthorizeException(HttpServletRequest request, HttpServletResponse response, Exception ex)
        throws IOException {
        if (restAuthenticationService.hasAuthenticationData(request)) {
            sendErrorResponse(request, response, ex, ex.getMessage(), HttpServletResponse.SC_FORBIDDEN);
        } else {
            sendErrorResponse(request, response, ex, ex.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @ExceptionHandler(SQLException.class)
    protected void handleSQLException(HttpServletRequest request, HttpServletResponse response, Exception ex)
        throws IOException {
        sendErrorResponse(request, response, ex,
                          "An internal database error occurred", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    protected void handleIOException(HttpServletRequest request, HttpServletResponse response, Exception ex)
        throws IOException {
        sendErrorResponse(request, response, ex,
                          "An internal read or write operation failed (IO Exception)",
                          HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingParameterException.class)
    protected void MissingParameterException(HttpServletRequest request, HttpServletResponse response, Exception ex)
        throws IOException {

        //422 is not defined in HttpServletResponse.  Its meaning is "Unprocessable Entity".
        //Using the value from HttpStatus.
        //Since this is a handled exception case, the stack trace will not be returned.
        sendErrorResponse(request, response, null,
                          ex.getMessage(),
                          HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @ExceptionHandler(QueryMethodParameterConversionException.class)
    protected void ParameterConversionException(HttpServletRequest request, HttpServletResponse response, Exception ex)
        throws IOException {

        //422 is not defined in HttpServletResponse.  Its meaning is "Unprocessable Entity".
        //Using the value from HttpStatus.
        //Since this is a handled exception case, the stack trace will not be returned.
        sendErrorResponse(request, response, null,
                          ex.getMessage(),
                          HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    private void sendErrorResponse(final HttpServletRequest request, final HttpServletResponse response,
                                   final Exception ex, final String message, final int statusCode) throws IOException {
        //Make sure Spring picks up this exception
        request.setAttribute(EXCEPTION_ATTRIBUTE, ex);

        //Exception properties will be set by org.springframework.boot.web.support.ErrorPageFilter
        response.sendError(statusCode, message);
    }

}
