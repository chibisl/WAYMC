package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Group;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.Translator;

public class WindowBody extends Group {

    public WindowBody() {
        this.setBounds(30, 90, 780, 349);
    }

    protected String replaceVarsAndTranslate(String text, ArrayList<String> vars, boolean needColor) {
        StringBuilder textBuilder = new StringBuilder();
        if (needColor) {
            textBuilder.append("[#").append(Config.getInstance().textColor.toString()).append("]");
        }
        textBuilder.append(Translator.getInstance().translate(text));
        String transText = textBuilder.toString();
        if (vars != null) {
            for (String var : vars) {
                StringBuilder varBuilder = new StringBuilder();
                if (needColor) {
                    varBuilder.append("[#").append(Config.getInstance().selectColor.toString()).append("]")
                            .append(Translator.getInstance().translate(var)).append("[]");
                } else {
                    varBuilder.append(Translator.getInstance().translate(var));
                }
                String transVar = varBuilder.toString();
                transText = transText.replaceFirst("\\{\\{var\\}\\}", transVar);
            }
        }
        transText = transText.replace("{{var}}", "").replace("[].", ".[]").replace("[],", ",[]").replace("[]!", "![]")
                .replace("[]?", "?[]");
        return transText;
    }

    protected String replaceVarsAndTranslate(String text, ArrayList<String> vars) {
        return this.replaceVarsAndTranslate(text, vars, false);
    }

    public void afterShow() {

    }
}
