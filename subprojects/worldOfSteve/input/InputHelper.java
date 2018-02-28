package input;

/*
 * Handles mouse and keyboard input and stores values for keys
 * down, released, or pressed, that can be accessed from anywhere.
 * 
 * To update the input helper, add this line into the main draw loop:
 *  InputHelper.update();
 * 
 * Use as so (can be used from anywhere):
 *  InputHelper.isKeyDown(Keyboard.KEY_SPACE);
 */

import java.util.ArrayList;
import org.lwjgl.input.*;

/**
 *
 * @author Jocopa3
 */
public class InputHelper {
    private static InputHelper input = new InputHelper(); //Singleton class instance

    private enum EventState {
        NONE,PRESSED,DOWN,RELEASED; 
    }

    private ArrayList<EventState> mouseEvents;
    private ArrayList<EventState> keyboardEvents;

    public InputHelper(){
        //Mouse initialization
        mouseEvents = new ArrayList<EventState>();
        //Add mouse events to Array list
        for(int i = 0; i < Mouse.getButtonCount(); i++) {
            mouseEvents.add(EventState.NONE);
        }

        //Keyboard initialization
        keyboardEvents = new ArrayList<EventState>();
        //Add keyboard events to Array list
        for(int i = 0; i < Keyboard.KEYBOARD_SIZE; i++) {
            keyboardEvents.add(EventState.NONE);
        }
    }

    private void Update(){
        resetKeys(); //clear Keyboard events
        //Set Key down events (more accurate than using repeat-event method)
        for(int i = 0; i < Keyboard.KEYBOARD_SIZE; i++){
            if(Keyboard.isKeyDown(i))
                keyboardEvents.set(i, EventState.DOWN);
        }
        while(Keyboard.next()){ //Handle all Keyboard events
            int key = Keyboard.getEventKey();
            if(key<0) continue; //Ignore no events

            if(Keyboard.getEventKeyState()){
                if(!Keyboard.isRepeatEvent()){
                    keyboardEvents.set(key, EventState.PRESSED);
                }
            }else{
                keyboardEvents.set(key, EventState.RELEASED);
            }
        }


        resetMouse(); //clear Mouse events
        //Set Mouse down events
        for(int i = 0; i < Mouse.getButtonCount(); i++){
            if(Mouse.isButtonDown(i))
                mouseEvents.set(i, EventState.DOWN);
        }
        while (Mouse.next()){ //Handle all Mouse events
            int button = Mouse.getEventButton();
            if(button<0) continue; //Ignore no events
            if (Mouse.getEventButtonState()) {
                mouseEvents.set(button, EventState.PRESSED);
            }else {
                mouseEvents.set(button, EventState.RELEASED);
            }
        }
    }

    //Set all Keyboard events to false
    private void resetKeys(){
        for(int i = 0; i < Keyboard.KEYBOARD_SIZE; i++) {
            keyboardEvents.set(i, EventState.NONE);
        }
    }

    //Set all Mouse events to false
    private void resetMouse(){
        for(int i = 0; i < Mouse.getButtonCount(); i++) {
            mouseEvents.set(i, EventState.NONE);
        }
    }

    //Non-static version of methods (Only used in the singleton instance)
    private boolean KeyDown(int key){
        return keyboardEvents.get(key)==EventState.DOWN;
    }
    private boolean KeyPressed(int key){
        return keyboardEvents.get(key)==EventState.PRESSED;
    }
    private boolean KeyReleased(int key){
        return keyboardEvents.get(key)==EventState.RELEASED;
    }
    private boolean MouseButtonDown(int key){
        return mouseEvents.get(key)==EventState.DOWN;
    }
    private boolean MouseButtonPressed(int key){
        return mouseEvents.get(key)==EventState.PRESSED;
    }
    private boolean MouseButtonReleased(int key){
        return mouseEvents.get(key)==EventState.RELEASED;
    }

    //Static version of methods (called from anywhere, return singleton instance value)
    public static boolean isKeyDown(int key){
        return input.KeyDown(key);
    }
    public static boolean isKeyPressed(int key){
        return input.KeyPressed(key);
    }
    public static boolean isKeyReleased(int key){
        return input.KeyReleased(key);
    }
    public static boolean isButtonDown(int key){
        return input.MouseButtonDown(key);
    }
    public static boolean isButtonPressed(int key){
        return input.MouseButtonPressed(key);
    }
    public static boolean isButtonReleased(int key){
        return input.MouseButtonReleased(key);
    }
    public static void update(){
        input.Update();
    }
}
