package dev.adamhodgkinson;

import java.util.function.Function;

public class UIButton {

    Function callback;

    public UIButton(Function callback){
        this.callback = callback;
    }

    public void clicked(){
        
    }
}
