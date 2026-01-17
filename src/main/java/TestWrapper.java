import java.util.logging.Level;
import java.util.logging.Logger;

public class TestWrapper
{
    public TestWrapper(Example ex){
        ex.run();
//        logger.log(Level.INFO, "TestWrapper initialized");
    }
}
