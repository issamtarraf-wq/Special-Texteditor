/* @author Issam Tarraf */

package editor;

import java.lang.Runnable;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.TextInputControl;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.Map;


public class Editor extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	private Map<String, Runnable> moveCommands = new HashMap<>();
	private Map<String, Runnable> selectCommands = new HashMap<>();
	private Map<String, Runnable> editCommands = new HashMap<>();
    
    /*
    Vor dem allerersten Zeichen ist der Kursor = 0.
    Nach dem allersten Zeichen ist der Kursor = 1. 
    Jedes "Enter" inkrementiert die KursorPosition um 1.
    */

    //variablen für linesEnd, lineEnd
    boolean isPressedLineStart = false;
    boolean isPressedLineEnd =  false;
    
    // Gebe die aktuelle Zeile zurück 
    public int getActuallyLine(TextArea ta, String[] lines, int caretPos) {
        int actuallyLine = 0;       
        int linesLength = 0;
        for (int i=0; i<lines.length; i++) {
            linesLength = linesLength + lines[i].length() + 1;
            if (caretPos >= (linesLength)) {
                actuallyLine = actuallyLine + 1;
            }
        }                
        return actuallyLine;
    }
    
    // Gebe die aktuelle Kursor-Zeilenposition zurück
    public int getActuallyCaretLinePos(TextArea ta, String[] lines, int caretPos, int actuallyLine) {
        int actuallyCaretLinePos = 0;   
        //Berechne die die Endposition der vorherigen Zeile
        int previousEndPos = 0;
        if (actuallyLine > 1) {
            for (int i=0; i<actuallyLine; i++) {
                previousEndPos = previousEndPos + lines[i].length();
            }
        } else if (actuallyLine == 1) {
            previousEndPos = lines[0].length();
        } 
        previousEndPos = previousEndPos + actuallyLine - 1;        
        actuallyCaretLinePos = ((caretPos - previousEndPos) - 1);
        return actuallyCaretLinePos;
    }    
    
    // Bewegt den Kursor um eine Zeile nach oben
    public void moveUp(TextArea ta) {                
        String[] lines = ta.getText().split("\n");
        int caretPos = ta.getCaretPosition();  
        int actuallyLine = getActuallyLine(ta, lines, caretPos);
        int actuallyCaretLinePos = getActuallyCaretLinePos(ta, lines, caretPos, actuallyLine);        
        int targetLineLength = 0;
        try {
            targetLineLength = lines[actuallyLine - 1].length();
        } catch (Exception e) {   
            System.out.println("In die obere Zeile schiften nicht möglich.");      
        }        
        if ( actuallyLine == 0 ){            
        } else {
            if( actuallyCaretLinePos <= targetLineLength) {
                ta.positionCaret( caretPos -(actuallyCaretLinePos+(Math.abs(actuallyCaretLinePos - targetLineLength)))-1 );                
            } else {
                ta.positionCaret( caretPos - (actuallyCaretLinePos) - 1 );
            }
        }        
    }        
    
    // Bewegt den Kursor um eine Zeile nach unten
    public void moveDown(TextArea ta) {
        String[] lines = ta.getText().split("\n");
        int caretPos = ta.getCaretPosition();  
        int actuallyLine = getActuallyLine(ta, lines, caretPos);
        int actuallyCaretLinePos = getActuallyCaretLinePos(ta, lines, caretPos, actuallyLine);         
        int targetLineLength = 0;
        try {
            targetLineLength = lines[actuallyLine + 1].length();
        } catch (Exception e) {   
            System.out.println("In die untere Zeile schiften nicht möglich.");            
        }           
        int actuallyEndPos = 0;
        if (actuallyLine + 1 <= lines.length) {
            for (int i=0; i<=actuallyLine; i++) {
                actuallyEndPos = actuallyEndPos + lines[i].length();
            }
        } 
        actuallyEndPos = actuallyEndPos + actuallyLine;        
        int actuallyLineLength = lines[actuallyLine].length();    
        if ( actuallyLine + 1 == lines.length ){
            ta.positionCaret(actuallyEndPos);              
        } else {
            if( actuallyCaretLinePos <= targetLineLength) {
                ta.positionCaret( caretPos + (actuallyLineLength - actuallyCaretLinePos) + 1 + actuallyCaretLinePos);                
            } else {
                ta.positionCaret( caretPos + (actuallyLineLength - actuallyCaretLinePos) + 1 + targetLineLength );
            }
        }     
    }    
    
    // Kursor springt an den Anfang des aktuellen bzw. vorherigen Wortes
    public void movePreviousWord(TextArea ta) {
        ta.previousWord();
    }
    
    // Kursor springt an den Anfang des nächsten Wortes
    public void moveNextWord(TextArea ta) {
        ta.nextWord();
    }    
    
    // Kursor springt beim ersten Tippen an den Anfang des aktuellen bzw. vorherigen Wortes, 
    //beim zweiten klicken zum Zeilenanfang  
    public void lineStart(TextArea ta) {
        String[] lines = ta.getText().split("\n");
        int caretPos = ta.getCaretPosition();  
        int actuallyLine = getActuallyLine(ta, lines, caretPos);
        int actuallyCaretLinePos = getActuallyCaretLinePos(ta, lines, caretPos, actuallyLine);         
        if (isPressedLineStart == false) {
            movePreviousWord(ta);
            isPressedLineStart = true;             
        } else {           
            ta.positionCaret(caretPos - actuallyCaretLinePos);       
            isPressedLineStart = false;
        }
    }    
  
    // Kursor springt an das Zeilenende
    public void lineEnd(TextArea ta) {
        String[] lines = ta.getText().split("\n");
        int caretPos = ta.getCaretPosition();  
        int actuallyLine = getActuallyLine(ta, lines, caretPos);
        int actuallyCaretLinePos = getActuallyCaretLinePos(ta, lines, caretPos, actuallyLine);         
        if (isPressedLineEnd == false) {
            moveNextWord(ta);
            isPressedLineEnd = true;             
        } else {           
            ta.positionCaret(caretPos + (lines[actuallyLine].length() - actuallyCaretLinePos));       
            isPressedLineEnd = false;
        }
    }
        
    /*
    Selektionen
    - Nach jeder Selektierung in Richtung links/oben bleibt der Kursor an seiner aktuellen Position (am ende der Selektion)
    - Nach jeder Selektierung in Richtung rechts/unten springt der Kursor an das ende der Selektion
    */   
    
    boolean left = false;
    boolean right = false; 
    boolean up = false;
    boolean down = false;
    
    int leftCounter;
    int rightCounter; 
    int upCounter;
    int downCounter;
   
    public boolean isTextSelected(TextArea ta) {
        if (!(ta.getSelection().getStart() == ta.getSelection().getEnd())) {   
            return true;        
        }
        return false;
    }
    
    // upCounter und downCounter werden nicht ressetet
    public void resetAllSelectionVariables(TextArea ta) {
        rightCounter = 0;
        leftCounter = 0;
        upCounter = 0;   
        downCounter = 0;
        right = false;
        left = false;
        up = false;
        down = false;
    }    
    
    // Inkrementiert eine Linksselektion um 1 Zeichen bzw. dekrementiert eine  Rechteselection um 1 Zeichen 
    public void selectLeft(TextArea ta, MyTextFormatter tf) {  
        
        if(isTextSelected(ta)) {              
            if (up) {
                System.out.println("LEFT bei vorherigem UP");  
                int selectionBegin = ta.getSelection().getStart();
                int selectionEnd = ta.getSelection().getEnd();
                tf.allowChange = true;
                ta.selectRange(selectionBegin - 1, selectionEnd);
                right = false;
                left = true;
                leftCounter++;
            
            } else  if (right) {
                System.out.println("LEFT bei vorhandenem RIGHT");  
                tf.allowChange = true;            
                ta.selectBackward();    
            
            } else if (down) {  
                tf.allowChange = true;            
                ta.selectBackward();
                leftCounter++;
                right = false;
                left = true;               
            
            } else {
                System.out.println("LEFT bei vorherigem Left");  
                tf.allowChange = true;
                ta.selectBackward();
                leftCounter++;
            }
            
        } else {
            System.out.println("LEFT bei Wurzel");  
            resetAllSelectionVariables(ta);
            tf.allowChange = true;
            ta.selectBackward();
            leftCounter++;
            left = true;
        }
    }
            
   // Inkrementiert eine Linksselektion um 1 Zeichen bzw. dekrementiert eine  Rechteselection um 1 Zeichen 
    public void selectRight(TextArea ta, MyTextFormatter tf) {  
        
        if(isTextSelected(ta)) { 
            if (up) {   
                System.out.println("RIGHT bei vorherigem UP");  
                int selectionBegin = ta.getSelection().getStart();
                int selectionEnd = ta.getSelection().getEnd();
                tf.allowChange = true;
                ta.selectRange(selectionBegin + 1, selectionEnd );
                left = false;
                right = true;
                rightCounter++;              
            
            } else if (left) {
                System.out.println("RIGHT mit vorhandenem LEFT");
                tf.allowChange = true;
                ta.selectForward();
            
            } else if (down) {
                System.out.println("RIGHT mit vorhandenem DOWN");
                tf.allowChange = true;
                ta.selectForward();
                left = false;
                right = true;
                rightCounter++;
            
            } else {
                System.out.println("RIGHT bei vorherigem RIGHT");  
                tf.allowChange = true;
                ta.selectForward();
                rightCounter++;
            }
            
        } else {
            System.out.println("RIGHT bei Wurzel");
            resetAllSelectionVariables(ta);
            tf.allowChange = true;
            ta.selectForward();
            rightCounter++;
            right = true;
        }
    }
  
    // Inkrementiert eine Obenselektion um eine Zeile bzw. dekrementiert eine Untenselektion um eine Zeile 
    public void selectLineUp(TextArea ta, MyTextFormatter tf) { 
        
         if(isTextSelected(ta)) {            
            if (down) {
            System.out.println("UP bei vorhandenem DOWN");   
                int selectionBegin = ta.getSelection().getStart();
                tf.allowChange = true;            
                ta.positionCaret(ta.getSelection().getEnd());
                tf.allowChange = true;
                moveUp(ta);
                int selectionEnd = ta.getCaretPosition();
                tf.allowChange = true;
                ta.selectRange(selectionBegin, selectionEnd);
                downCounter --;
                if (downCounter == 0) {
                    down = false;
                    right = false;
                }          
                
           
            } else if (left) {
                if (upCounter == 0) {
                    System.out.println("UP bei vorhandenem LEFT upCounter == 0"); 
                    int selectionEnd = ta.getSelection().getEnd();
                    tf.allowChange = true;
                    ta.positionCaret(ta.getSelection().getStart());
                    tf.allowChange = true;
                    moveUp(ta);
                    System.out.println("leftcounter " + leftCounter);
                    System.out.println("rightcounter " + rightCounter);
                    int selectionBegin = ta.getCaretPosition();
                    tf.allowChange = true;
                    ta.selectRange(selectionBegin, selectionEnd);
                    up = true;
                    upCounter++;   
                    left = false;
                    
                } else {
                    System.out.println("UP bei vorhandenem LEFT upCounter > 0"); 
                    int selectionEnd = ta.getSelection().getEnd();
                    tf.allowChange = true;
                    ta.positionCaret(ta.getSelection().getStart());
                    tf.allowChange = true;
                    moveUp(ta);
                    int selectionBegin = ta.getCaretPosition();
                    tf.allowChange = true;
                    ta.selectRange(selectionBegin, selectionEnd);
                    up = true;
                    upCounter++;   
                }
                
            } else if (right) {  
                if (upCounter == 0) {
                    System.out.println("UP bei vorhandenem RIGHT upCounter == 0"); 
                    int selectionEnd = ta.getSelection().getEnd();
                    tf.allowChange = true;
                    ta.positionCaret(ta.getSelection().getStart());
                    tf.allowChange = true;
                    moveUp(ta);
                    int selectionBegin =  ta.getCaretPosition() + rightCounter;
                    tf.allowChange = true;
                    ta.selectRange(selectionBegin, selectionEnd);      
                    resetAllSelectionVariables(ta);
                    up = true;
                    upCounter++;
                    
                } else {
                    System.out.println("UP bei vorhandenem RIGHT upcounter > 0"); 
                    int selectionEnd = ta.getSelection().getEnd();
                    tf.allowChange = true;
                    ta.positionCaret(ta.getSelection().getStart());
                    tf.allowChange = true;
                    moveUp(ta);
                    int selectionBegin =  ta.getCaretPosition();
                    tf.allowChange = true;
                    ta.selectRange(selectionBegin, selectionEnd);      
                    up = true;
                    upCounter++;
                    rightCounter = 0;
                    right = false;
                }
            
            } else {
                if (upCounter == 0) {
                      System.out.println("Up bei vorherigem Up upcounter == 0");  
                    int selectionEnd = ta.getSelection().getEnd();
                    tf.allowChange = true;
                    ta.positionCaret(ta.getSelection().getEnd());
                    tf.allowChange = true;
                    moveUp(ta);
                    int selectionBegin = ta.getCaretPosition();
                    tf.allowChange = true;
                    ta.selectRange(selectionBegin, selectionEnd);
                    leftCounter = 0;
                    rightCounter = 0;
                    upCounter++;
                    up = true;
                    
                } else {
                    System.out.println("Up bei vorherigem Up upcounter > 0");  
                    int selectionEnd = ta.getSelection().getEnd();
                    tf.allowChange = true;
                    ta.positionCaret(ta.getSelection().getStart());
                    tf.allowChange = true;
                    moveUp(ta);
                    int selectionBegin = ta.getCaretPosition();
                    tf.allowChange = true;
                    ta.selectRange(selectionBegin, selectionEnd);
                    upCounter++;
                    up = true;
                }               
            }
             
        } else {
             System.out.println("UP bei Wurzel");  
             resetAllSelectionVariables(ta);
             int selectionEnd = ta.getCaretPosition();
             tf.allowChange = true;
             moveUp(ta);
             int selectionBegin = ta.getCaretPosition();
             tf.allowChange = true;
             ta.selectRange(selectionBegin, selectionEnd);
             upCounter++;
             up = true;
        }
    }

    // Inkrementiert eine Untenselektion um eine Zeile bzw. dekrementiert eine Obenselektion um eine Zeile 
    public void selectLineDown(TextArea ta, MyTextFormatter tf) {
        
        if(isTextSelected(ta)) {   
            if (up) {
                System.out.println("DOWN bei vorhandenem UP");  
                int selectionEnd = ta.getSelection().getEnd();
                tf.allowChange = true;            
                ta.positionCaret(ta.getSelection().getStart());
                tf.allowChange = true;
                moveDown(ta);
                int selectionBegin = ta.getCaretPosition();
                tf.allowChange = true;
                ta.selectRange(selectionBegin, selectionEnd);
                upCounter--;
                if (upCounter == 0) {
                up = false;
                left = false;
                }
             
            } else if (right) {
                System.out.println("Down bei vorherigem RIGHT");  
                int selectionBegin = ta.getSelection().getStart();
                ta.positionCaret(ta.getSelection().getEnd());
                tf.allowChange = true;
                moveDown(ta);                
                int selectionEnd = ta.getCaretPosition();
                tf.allowChange = true;
                ta.selectRange(selectionBegin, selectionEnd);
                down = true;
                right = false;
                downCounter++;                
            
            } else if (left) {  
                if (downCounter == 0) {
                    System.out.println("DOWN bei vorherigem LEFT downcounter == 0");             
                    int selectionBegin = ta.getSelection().getStart();
                    tf.allowChange = true;
                    ta.positionCaret(ta.getSelection().getEnd());
                    tf.allowChange = true;
                    moveDown(ta);                                                    
                    int selectionEnd = ta.getCaretPosition() - (leftCounter + rightCounter);
                    tf.allowChange = true;
                    ta.selectRange(selectionBegin, selectionEnd);
                    down = true;
                    left = false;
                    downCounter++;
                    /* leftCounter = 0;*/
                  
                } else {
                    System.out.println("DOWN bei vorherigem LEFT downcounter > 0");            
                    int selectionBegin = ta.getSelection().getStart();
                    tf.allowChange = true;
                    ta.positionCaret(ta.getSelection().getEnd());
                    tf.allowChange = true;
                    moveDown(ta);                                                    
                    int selectionEnd = ta.getCaretPosition();
                    tf.allowChange = true;
                    ta.selectRange(selectionBegin, selectionEnd);
                    down = true;
                    left = false;
                    downCounter++;
                    /* leftCounter = 0;*/                          
                }                
                
            } else {
                if (downCounter == 0) {
                    // Zusätzlich wird hir abgefragt, ob die aktuelle Selektionsserie nach rechts oder lenks ausgerichtet ist 
                    if (right) {
                        System.out.println("RIGHT Down bei vorherigem Down down == 0");  
                        int selectionBegin = ta.getSelection().getStart();
                        tf.allowChange = true;
                        ta.positionCaret(ta.getSelection().getEnd());
                        tf.allowChange = true;
                        moveDown(ta);
                        int selectionEnd = ta.getCaretPosition() + rightCounter;
                        tf.allowChange = true;
                        ta.selectRange(selectionBegin, selectionEnd);
                        resetAllSelectionVariables(ta);
                        downCounter++;
                        down = true;
                        
                    } else {
                        System.out.println("LEFT Down bei vorherigem Down down > 0");  
                        int selectionBegin = ta.getSelection().getStart();
                        tf.allowChange = true;
                        ta.positionCaret(ta.getSelection().getEnd());
                        tf.allowChange = true;
                        moveDown(ta);
                        int selectionEnd = ta.getCaretPosition() - leftCounter;
                        tf.allowChange = true;
                        ta.selectRange(selectionBegin, selectionEnd);
                        resetAllSelectionVariables(ta);
                        downCounter++;
                        down = true;
                    }
                    
                } else {
                    System.out.println("Down bei vorherigem Down");  
                    int selectionBegin = ta.getSelection().getStart();
                    tf.allowChange = true;
                    ta.positionCaret(ta.getSelection().getEnd());
                    tf.allowChange = true;
                    moveDown(ta);
                    int selectionEnd = ta.getCaretPosition();
                    tf.allowChange = true;
                    ta.selectRange(selectionBegin, selectionEnd);
                    downCounter++;
                    down = true;
                }
            }
            
        } else {
            System.out.println("DOWN bei Wurzel");  
            resetAllSelectionVariables(ta);       
            int selectionBegin = ta.getSelection().getStart();
            tf.allowChange = true;
            moveDown(ta);
            int selectionEnd = ta.getCaretPosition();
            tf.allowChange = true;
            ta.selectRange(selectionBegin, selectionEnd);
            downCounter++;
            down = true;
        }
    }    
        
    // Selektiere die aktuelle Zeile
    public void selectCurrentLine(TextArea ta, MyTextFormatter tf) {
        lineEnd(ta);
        selectLineUp(ta, tf);
        
    }
    
    // Selektiere Alles
    public void selectAll(TextArea ta, MyTextFormatter tf) {
        tf.allowChange = true;
        ta.home();
        int home = ta.getCaretPosition();
        tf.allowChange = true;
        ta.end();
        int end = ta.getCaretPosition();
        tf.allowChange = true;
        ta.selectRange( home, end );
    }
    
    // Variablen für cut, copy, paste 
    String copiedText;    
    boolean isCopyCommand = false;
    
    // Kopiere selektierten Text
    public void copy(TextArea ta, MyTextFormatter tf) {
        copiedText = ta.getSelectedText();
        isCopyCommand = true;
        tf.allowChange = true;            
        ta.selectRange(ta.getSelection().getStart(), ta.getSelection().getEnd());  
    }
    
    // Schneide kopierten Text aus
    public void cut(TextArea ta, MyTextFormatter tf) {
        copy(ta, tf);
        tf.allowChange = true; 
        ta.deleteText(ta.getCaretPosition()-copiedText.length(), ta.getCaretPosition());
    }
    
    // Füge kopierten oder ausgeschnittenen Text ein
    public void paste(TextArea ta, MyTextFormatter tf) {
        if (isCopyCommand == true) {
            tf.allowChange = true;
            ta.deleteText(ta.getCaretPosition()-copiedText.length(), ta.getCaretPosition());
        }
        isCopyCommand = false;
        tf.allowChange = true;
        ta.insertText(ta.getCaretPosition(), copiedText);
    }

    // Füge neue Zeile ein
    public void enter(TextArea ta, MyTextFormatter tf) {
        tf.allowChange = true;
        ta.insertText(ta.getCaretPosition(), "\n");        
    }
    // Lösche letztes Zeichen
    public void backspace(TextArea ta, MyTextFormatter tf) {
        tf.allowChange = true;
        ta.deleteText(ta.getCaretPosition()-1, ta.getCaretPosition());
    }
    // Lösche nächstes Zeichen
    public void detach(TextArea ta, MyTextFormatter tf) {
        tf.allowChange = true;
        ta.deleteText(ta.getCaretPosition(), ta.getCaretPosition() + 1);
    }
    // Füge 4 Leerzeichen ein
    public void tab(TextArea ta, MyTextFormatter tf) {
        tf.allowChange = true;
        ta.insertText(ta.getCaretPosition(), "    ");
    }
    
	@Override
	public void start(Stage primaryStage) {
		
		primaryStage.setTitle("Textarea Texteditor");        
		TextArea ta = new TextArea();        
        MyTextFormatter tf = new MyTextFormatter();
        ta.setText("12345 abc defg hij\n123 abc456789\n123 abc 456 abc 789\n123456789\n123456789\n123456789\n123456789\n123456789\n1234567896789\n12345678967896789\n123456789\n12345\n12345");
        ta.setFont(Font.font("Consolas", 22));
        ta.setEditable(true);
        ta.setFocusTraversable(true);
        ta.setTextFormatter(tf);
               
        moveCommands.put( "fj", () -> ta.backward() );
		moveCommands.put( "fö", () -> ta.forward() );
        moveCommands.put( "fk", () -> moveUp(ta) );
        moveCommands.put( "fl", () -> moveDown(ta) );        
        moveCommands.put( "fu", () -> movePreviousWord(ta) ); 
        moveCommands.put( "fp", () -> moveNextWord(ta) );
        moveCommands.put( "fi", () -> lineStart(ta) );
        moveCommands.put( "fo", () -> lineEnd(ta) );        
        moveCommands.put( "f,", () -> ta.home() ); 
        moveCommands.put( "f.", () -> ta.end() );

		selectCommands.put( "dj", () -> selectLeft(ta, tf) );
		selectCommands.put( "dö", () -> selectRight(ta, tf) );
        selectCommands.put( "dk", () -> selectLineUp(ta, tf));
        selectCommands.put( "dl", () -> selectLineDown(ta, tf) );        
		selectCommands.put( "du", () -> ta.selectPreviousWord() );	
        selectCommands.put( "dp", () -> ta.selectNextWord() );         
		selectCommands.put( "di", () -> selectCurrentLine(ta, tf) );	
        selectCommands.put( "do", () -> selectAll(ta, tf) );   
        
		editCommands.put( "dn", () -> cut(ta, tf) );			
		editCommands.put( "dm", () -> copy(ta, tf) );			
		editCommands.put( "d,", () -> paste(ta, tf) );	
        editCommands.put( "aj", () -> backspace(ta, tf));
        editCommands.put( "ak", () -> detach(ta, tf));
        editCommands.put( "al", () -> tab(ta, tf));
        editCommands.put( "aö", () -> enter(ta, tf));	
       
		final String[] pressed = { "" };
        final String[] pressedStorage = { "" };
        final boolean[] isCommand = { false };
    
        StackPane root = new StackPane();
		root.getChildren().add(ta);
		Node node = ta;
		node.setFocusTraversable(true);
     
		node.setOnKeyPressed((final KeyEvent keyEvent) -> {	      
            String k = keyEvent.getText();            
			String s = pressed[0];   
           	if (!s.contains(k)) {
			    s = s + k;
			    pressed[0] = s;
    		}                
        });        
        
		node.setOnKeyReleased((final KeyEvent keyEvent) -> {    
            String k = keyEvent.getText();
			String s = pressed[0];        
            
            // Prüfe ob es sich um ein Modifizierungsbefehl handelt           
            if (s.length() == 2 && k.length() == 1) {
                if (s.charAt(1) == k.charAt(0)) {                    
                    if (moveCommands.containsKey(s)) {
                        System.out.println("A");  
                        tf.allowChange = true;                      
                        moveCommands.get(s).run();    
                        pressed[0] = "" + s.charAt(0);  
                        resetAllSelectionVariables(ta); 
                        
                    } else if (selectCommands.containsKey(s)) { 
                        System.out.println("AA");                       
                        tf.allowChange = true;    
                        selectCommands.get(s).run();
                        pressed[0] = "" + s.charAt(0); 
                        
                    } else if (editCommands.containsKey(s)) {
                         System.out.println("AAA"); 
                         tf.allowChange = true;   
                         editCommands.get(s).run();   
                         pressed[0] = "" + s.charAt(0);
                  
                    } else {
                        System.out.println("B");
                        System.out.println(s + " is not a known command");
                        pressed[0] = "" + s.charAt(0); 
                    }
                    isCommand[0] = true;                     
                } else {
                    System.out.println("C");
                    pressed[0] = "" + s.charAt(1);
                    isCommand[0] = true;             
                }                 
                
            } else {
                System.out.println("D");
                if (!isCommand[0]) {
                    int caretPos = ta.getCaretPosition();
                    tf.allowChange = true;
                    ta.insertText(caretPos, s);
                }                
                pressed[0] = ""; 
                isCommand[0] = false;
            }            
        
		});
        primaryStage.setScene(new Scene(root, 500, 350));
		primaryStage.show();
	}
     
}