package networking;

//import contracts.soap.BookingSoapService;

import contracts.soap.BookingSoapService;
import jakarta.xml.ws.Service;

import javax.xml.namespace.QName;
import java.net.URL;

public class SoapProvider {
    public static BookingSoapService getBookingSoapService() {
       try{
           URL wsdlURL = new URL("http://localhost:8081/booking?wsdl");
           QName serviceName = new QName("http://soap.example.org/booking", "BookingService");

           // Create the service
           Service service = Service.create(wsdlURL, serviceName);

           // Get the port (endpoint interface) for the BookingSoapService
           return service.getPort(BookingSoapService.class);
       }catch(Exception e){
           return null;
       }
    }
}
