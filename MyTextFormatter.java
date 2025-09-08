package editor;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import java.util.function.UnaryOperator;

class MyTextFormatter extends TextFormatter<String> {

    public MyTextFormatter() {
        super(inputFilter);   
    }
    
    public static boolean allowChange = false;
    
    
    private static UnaryOperator<Change> inputFilter =
        change -> { 
        String s = change.getText();
         System.out.println("change: " + s);
            if (allowChange) {
                allowChange = false;
                return change;
            }
            return null;
        };
    


   /* private static UnaryOperator<Change> filter =
        change -> {
        
            System.out.println("AAAAAAAAAAAAAAAAAA");
            // String s = change.getControlNewText();
            String s = change.getText();
            System.out.println("change: " + s);
         
            
            if (s != null && !s.equals("x")) {
                return change;
            }
            
            if (s == "") return null;
            if (s != null) {                    
                if (s.equals("x")) {
                    change.setText("X");
                    
                    System.out.println("change: " + s);
                    //change.setText("");
                }
                return change;
            } 
            return null;
        };
*/

}

