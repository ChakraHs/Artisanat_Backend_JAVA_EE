package com.Pf_Artis.exception.handler;

import java.util.Date;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.Pf_Artis.exception.EntityAlreadyExistsException;
import com.Pf_Artis.exception.EntityNotFoundException;
import com.Pf_Artis.shared.ErrorMessage;

@Provider
public class AppExceptionHandler implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable ex) {
		if (ex instanceof EntityNotFoundException) {
            return buildErrorResponse((EntityNotFoundException) ex, 404);
        } else if (ex instanceof EntityAlreadyExistsException) {
            return buildErrorResponse((EntityAlreadyExistsException) ex, 409);
        } else {
            // Gérer d'autres types d'exceptions si nécessaire
            return Response.serverError().build();
        }
	}
	
	private Response buildErrorResponse(Exception ex, int status) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .message(ex.getMessage())
                .timestamp(new Date())
                .code(status)
                .build();
        return Response.status(status).entity(errorMessage).build();
    }

}
