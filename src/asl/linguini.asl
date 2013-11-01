/* Initial beliefs and rules */
day("").
menu_p1("").
menu_p2("").

/* Initial goals */
!create.

/* Plans */
+!create: true <- 
	?setupArtifact(ID).
	
+?setupArtifact(E) : true <-
   makeArtifact("env_web", "workspaces.EnvironmentWeb", [], E);
   focus(E);
   !notification.   
	
-?setupArtifact(E) : true <-
	.wait(30);
	!create.

+!notification: true <-
 	loadMenus; 	
 	!monitoring.  		
 	 	
 +!monitoring: true <-
   println("Esperando: ",(1000 * 60)/60000, " minutos");
 	.wait(((1000 * 60) * 60 ) * 24);   //24 horas segundos
 	!notification.
 	
//Perceptions 	
+menu_p1(MenuP1): day(Day) & menu_p2(MenuP2)<-
	if (Day \== ""){
		internalActions.sendTwitter(Day);
		internalActions.sendTwitter(MenuP1);
		if (MenuP2 \== ""){
			internalActions.sendTwitter(MenuP2);
		};		
	}.