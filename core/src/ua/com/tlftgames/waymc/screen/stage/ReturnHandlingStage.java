package ua.com.tlftgames.waymc.screen.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import ua.com.tlftgames.waymc.screen.StageScreen;

public abstract class ReturnHandlingStage extends LoadStage {
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            if (this.getClass().getName().contentEquals(MenuStage.class.getName()) || this.getClass().getName().contentEquals(StartStage.class.getName())) {
                Gdx.app.exit();
            } else {
                StageScreen.getInstance().setStage(new MenuStage());
            }
        }
        return super.keyDown(keycode);
    }
}
