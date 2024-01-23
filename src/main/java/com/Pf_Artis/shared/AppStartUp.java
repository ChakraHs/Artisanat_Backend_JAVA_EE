package com.Pf_Artis.shared;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppStartUp implements ServletContextListener {

	@Override
    public void contextInitialized(ServletContextEvent sce) {
		
		System.out.println("test");
        // Code à exécuter au démarrage de l'application Java EE
        // Vous pouvez mettre votre logique de création de rôles, d'utilisateurs, etc. ici
    }
	
	@Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Code à exécuter lors de l'arrêt de l'application Java EE
    }
	
}
