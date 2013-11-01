package workspaces;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;

public class EnvironmentWeb extends Artifact {
	void init() {
		defineObsProperty("day", "");
		defineObsProperty("menu_p1", "");
		defineObsProperty("menu_p2", "");
	}
	
	@OPERATION
	void loadMenus(){
		try {
			Socket s = new Socket("www.uece.br", 80);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));                                                            
            String path = "/uece/index.php/restauranteuniversitario";                        
            
            out.println("GET " + path + " HTTP/1.1");
            out.println("Host: www.uece.br");
            out.println("Connection: keep-alive");
            out.println("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            out.println();                
            
            boolean loop = true, p = false;
            StringBuffer sb = new StringBuffer();
            StringBuffer page = new StringBuffer();            
            
            // recupera a resposta quando ela estiver disponível
            while (loop) {
                if (in.ready()) {
                    int i = 0;
                    while ((i = in.read()) != -1) {
                    	if (((char) i == '<') || (p == true)){
                    		page.append((char)i);
                    		p = true;
                    	}
                    	else
                    		 sb.append((char) i);
                    }
                    loop = false;
                }
            }	                                                         
            Document d = Jsoup.parse(page.toString());
            processResults(d);
			s.close();
			
		} catch (java.io.IOException e) { e.printStackTrace();}
	}	
	
	@INTERNAL_OPERATION
	void processResults(Document d){
		for (Element e : d.getElementsByClass("ru_head_title")){
			getObsProperty("day").updateValue(e.children().get(0).text());
			
			if (e.nextElementSibling().children().get(0).text().length() > 139){
				getObsProperty("menu_p2").updateValue(e.nextElementSibling().children().get(0).text().substring(139));
				getObsProperty("menu_p1").updateValue(e.nextElementSibling().children().get(0).text().substring(0, 139));
			}
			else{
				getObsProperty("menu_p1").updateValue(e.nextElementSibling().children().get(0).text().substring(0));
				getObsProperty("menu_p2").updateValue("");
			}
				
			await_time(10);
		}			
	}		
}