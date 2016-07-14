package ua.com.tlftgames.waymc.screen.map;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.place.Place;

public class Metro extends Group {
    public final static int ATTENTION_LEFT = 0;
    public final static int ATTENTION_RIGHT = 1;
    public final static int ATTENTION_HIGH_LEFT = 2;
    public final static int ATTENTION_HIGH_RIGHT = 3;
    private float stationY = 200;
    public float stationLineWidth;
    private TextureAtlas atlas;
    private Station[] stations;
    private float leftHighAttentionX = -1;
    private float rightHighAttentionX = -1;
    private float leftAttentionX = -1;
    private float rightAttentionX = -1;

    public Metro(TextureAtlas atlas) {
        this.atlas = atlas;
        this.addStations();
        this.updateAttentions();
        this.updateReach();
    }

    public float getAttentionX(int attentionType) {
        switch (attentionType) {
            case ATTENTION_HIGH_LEFT:
                return leftHighAttentionX;
            case ATTENTION_HIGH_RIGHT:
                return rightHighAttentionX;
            case ATTENTION_LEFT:
                return leftAttentionX;
            case ATTENTION_RIGHT:
                return rightAttentionX;
        }
        return -1;
    }

    private void addStations() {
        TextureRegion lineRegion = atlas.findRegion("metro-line");
        Place[] places = GameCore.getInstance().getPlaceManager().getPlaces();
        stationLineWidth = lineRegion.getRegionWidth() + 60;
        float lineY = this.stationY + (50 - lineRegion.getRegionHeight()) / 2;
        float labelAboveY = this.stationY + 60;
        float labelUnderY = this.stationY - 40;
        this.stations = new Station[places.length];
        for (int i = 0; i < places.length; i++) {
            float stationX = (55 + i * (stationLineWidth));
            if (i < places.length - 1) {
                Image line = new Image(lineRegion);
                float lineX = stationX + 55;
                line.setPosition(lineX, lineY);
                this.addActor(line);
            } else {
                stationX = stationX;
            }
            float labelY = (i % 2 == 1) ? labelUnderY : labelAboveY;
            Station station = this.createStation(places[i], stationX, labelY, i);
            this.addActor(station);
            this.stations[i] = station;
        }

        this.setBounds(0, 0, 110 + (places.length - 1) * stationLineWidth, stationY + 100);
    }

    private Station createStation(Place place, float stationX, float labelY, int i) {
        TextureRegion stationRegion = null;
        TextureRegion stationRegionTouched = null;
        TextureRegion atentionRegion = atlas.findRegion("atention");
        TextureRegion highAtentionRegion = atlas.findRegion("high-atention");
        switch (place.getType()) {
        case Place.TYPE_RESIDENTIAL:
            stationRegion = atlas.findRegion("metro-station-residential");
            stationRegionTouched = atlas.findRegion("metro-station-residential-touched");
            break;
        case Place.TYPE_RECREATIONAL:
            stationRegion = atlas.findRegion("metro-station-recreational");
            stationRegionTouched = atlas.findRegion("metro-station-recreational-touched");
            break;
        case Place.TYPE_MERCHANT:
            stationRegion = atlas.findRegion("metro-station-merchant");
            stationRegionTouched = atlas.findRegion("metro-station-merchant-touched");
            break;
        case Place.TYPE_INDUSTRIAL:
            stationRegion = atlas.findRegion("metro-station-industrial");
            stationRegionTouched = atlas.findRegion("metro-station-industrial-touched");
            break;
        }
        return new Station(stationRegion, stationRegionTouched, atentionRegion, highAtentionRegion, i, place.getName(),
                stationX, stationY, labelY);
    }

    public void updateAttentions() {
        int searchPlace = GameCore.getInstance().getPlaceManager().getCurrentShowedSearchPlace();
        this.clearAttentionXs();
        for (Station station : this.stations) {
            Place place = GameCore.getInstance().getPlaceManager().getPlace(station.getIndex());
            if ((place.getIndex() == searchPlace || place.getType() == searchPlace)
                    && !GameCore.getInstance().getPlaceManager().isSearchedPlace(place)) {
                station.showHighAttention();
                if (this.leftHighAttentionX < 0)
                    this.leftHighAttentionX = station.getX();
                this.rightHighAttentionX = station.getX();
            } else if (GameCore.getInstance().getQuestManager().getMandatoryQuestForPlace(place) != null) {
                station.showAttention();
                if (this.leftAttentionX < 0)
                    this.leftAttentionX = station.getX();
                this.rightAttentionX = station.getX();
            } else {
                station.hideAttentions();
            }
        }
    }

    private void clearAttentionXs() {
        this.leftHighAttentionX = -1;
        this.rightHighAttentionX = -1;
        this.leftAttentionX = -1;
        this.rightAttentionX = -1;
    }

    public void updateReach() {
        int currentPlaceIndex = GameCore.getInstance().getPlaceManager().getCurrentPlaceIndex();
        for (Station station : this.stations) {
            if (station.getIndex() >= currentPlaceIndex - GameCore.getInstance().getMoveDistance()
                    && station.getIndex() <= currentPlaceIndex + GameCore.getInstance().getMoveDistance()) {
                station.setReachable();
            } else {
                station.setUnreachable();
            }
        }
    }
}
