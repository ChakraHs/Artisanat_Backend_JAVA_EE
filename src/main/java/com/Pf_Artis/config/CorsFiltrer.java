package com.Pf_Artis.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.Pf_Artis.dao.DaoFactory;
import com.Pf_Artis.dto.UserDto;
import com.Pf_Artis.service.facade.UserServiceInterface;
import com.Pf_Artis.service.impl.UserServiceImpl;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/api/*")
public class CorsFiltrer implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
        
		httpResponse.addHeader("Access-Control-Allow-Origin", "http://localhost:4200"); // L'origine de votre application Angular
        httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept ,Authorization");
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession();
        
        
        String authorizationHeader = httpRequest.getHeader("Authorization");
        String path=httpRequest.getServletPath();
        
        
//        System.out.println("pathhhhhhhhhhhhhhgetServletPath : "+httpRequest.getServletPath());
//        System.out.println("pathhhhhhhhhhhhhhgetContextPath : "+httpRequest.getContextPath());
//        System.out.println("pathhhhhhhhhhhhhhgetPathTranslated : "+httpRequest.getPathTranslated());
//        System.out.println("pathhhhhhhhhhhhhh : "+path);
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7); // Supprime "Bearer " du token
            // Validez et traitez le jeton JWT comme nécessaire
            
            try {
				
            	boolean check =  GenerateJwtToken.isValidJwt(jwtToken);
				if(check) {
					
					String email = GenerateJwtToken.extractUsername(jwtToken);
//					System.out.println( email );
					UserServiceInterface serviceInterface = new UserServiceImpl(DaoFactory.getInstance());
					UserDto userDto = serviceInterface.getUserByEmail(email);
					
					if(path.equals("/api/logout")) {
						
						session.removeAttribute("userId");
						
						userDto.setToken("");
						serviceInterface.updateUser(userDto);
						
//						System.out.println(" /api/logouttttttttttttttttt ");
					}else {
						
						session.setAttribute("userId", userDto.getUserId());
					}
				}else {
					
					System.out.println("chi tkharbi9a");
					session.removeAttribute("userId");
					
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        chain.doFilter(request, response);
		
	}

}
