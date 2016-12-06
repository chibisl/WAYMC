package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.natification.Notification;
import ua.com.tlftgames.waymc.place.Place;
import ua.com.tlftgames.waymc.screen.ui.TextButton;

public class PlaceWindowBody extends WindowBody {
    private PlaceWindowManager manager;

    public PlaceWindowBody(PlaceWindowManager manager, Place place) {
        super();
        this.manager = manager;
        this.addPlaceElements(place);
    }

    public PlaceWindowManager getManager() {
        return this.manager;
    }

    private void addPlaceElements(Place place) {
        Label placeInfo = new Label(this.replaceVarsAndTranslate(place.getInfo(), null, false),
                Config.getInstance().normalStyle);
        placeInfo.setWidth(this.getWidth());
        placeInfo.setBounds(0, 135, this.getWidth(), this.getHeight() - 135);
        placeInfo.setAlignment(Align.topLeft);
        placeInfo.setWrap(true);
        this.addActor(placeInfo);

        StringBuilder placeParamsText = new StringBuilder();
        ArrayList<String> vars = new ArrayList<String>();
        placeParamsText.append("place.type");
        vars.add("place.type." + place.getType());

        int currentPlaceIndex = GameCore.getInstance().getPlaceManager().getCurrentPlaceIndex();
        if (place.getIndex() >= currentPlaceIndex - GameCore.getInstance().getMoveDistance()
                && place.getIndex() <= currentPlaceIndex + GameCore.getInstance().getMoveDistance()) {

            String buttonText;
            if (place.getIndex() == currentPlaceIndex) {
                buttonText = "place.stay";
            } else {
                buttonText = "place.move";
                placeParamsText.append("+place.move.cost");
                vars.add(Integer.toString(GameCore.getInstance().getPlaceManager().getMoveCost()));
                vars.add(Translator.getInstance().getMoneyText(GameCore.getInstance().getPlaceManager().getMoveCost()));
            }

            TextButton go = this.getManager().getHelper().createTextButton(buttonText);
            go.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (GameCore.getInstance().getPlaceManager().getMoveCost() > 0) {
                        if (GameCore.getInstance().canMoveToOpenPlace()) {
                            PlaceWindowBody.this.getManager().getUIGroup().moveToOpenPlace();
                        } else {
                            GameCore.getInstance().getNotificationManager()
                                    .addNotification(new Notification("money", "notification.money.not.match"));
                        }
                    } else {
                        PlaceWindowBody.this.getManager().startPlaceEvents(false);
                    }
                }
            });
            go.setHeight(60);
            go.setPosition((this.getWidth() - go.getWidth()) / 2, 30);
            this.addActor(go);
        }
        String paramsText = this.replaceVarsAndTranslate(placeParamsText.toString(), vars, true);

        NinePatch patch = new NinePatch(this.manager.getAtlas().findRegion("place-label-bg"), 26, 26, 26, 26);
        Image placeLabelBg = new Image(new NinePatchDrawable(patch));
        placeLabelBg.setPosition(10, this.getHeight() + 20);
        this.addActor(placeLabelBg);

        Label placeParams = new Label(paramsText, Config.getInstance().colorStyle);
        placeParams.setPosition(45, this.getHeight() + 35);
        this.addActor(placeParams);

        Label placeName = new Label(Translator.getInstance().translate(place.getName()),
                Config.getInstance().headerStyle);
        placeName.setPosition(45, placeParams.getY() + placeParams.getHeight() + 10);
        this.addActor(placeName);

        placeLabelBg.setSize(Math.max(placeParams.getWidth(), placeName.getWidth()) + 70,
                placeName.getHeight() + placeParams.getHeight() + 35);
    }
}
