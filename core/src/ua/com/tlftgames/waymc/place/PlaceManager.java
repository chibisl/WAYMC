package ua.com.tlftgames.waymc.place;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Save;
import ua.com.tlftgames.waymc.listener.Dispatcher;

public class PlaceManager {
    private int lastPlace = 0;
    private int currentPlace = 0;
    private int openPlace = 0;
    private Place[] places;
    private int currentSearchPlaceIndex = 0;
    private ArrayList<Integer> searchPlaces;
    private ArrayList<Integer> searchedPlaces;
    private int stepCount = 0;

    public PlaceManager() {
        this.createPlaces();
        this.searchedPlaces = new ArrayList<Integer>();
    }

    private void createPlaces() {
        this.places = new Place[13];
        if (Gdx.files.internal("data/places.json").exists()) {
            JsonValue placeDatas = new JsonReader().parse(Gdx.files.internal("data/places.json"));
            JsonValue placeData = placeDatas.child();
            int i = 0;
            while (placeData != null) {
                this.places[i] = new Place(i, placeData.getString("name"), placeData.getString("info"),
                        placeData.getInt("type"));
                placeData = placeData.next();
                i++;
            }
        }
    }

    public void generateSearchPlaces() {
        this.searchPlaces = new ArrayList<Integer>();
        int lastSearchIndex = 0;
        int minSearchDistance = 6;
        int placeCount = 13;
        for (int i = 0; i < Config.getInstance().searchCount; i++) {
            int searchIndex = lastSearchIndex;
            if (isSearchIndexInArray(i, Config.getInstance().searchTypeIndexes)) {
                searchIndex = (int) (Math.random() * placeCount);
            } else {
                int distance = lastSearchIndex;
                if (lastSearchIndex > placeCount - lastSearchIndex - 1) {
                    searchIndex = 0;
                    searchIndex += (distance > minSearchDistance)
                            ? (int) (Math.random() * (distance - minSearchDistance + 1)) : 0;
                } else {
                    distance = placeCount - lastSearchIndex - 1;
                    searchIndex += distance;
                    searchIndex -= (distance > minSearchDistance)
                            ? (int) (Math.random() * (distance - minSearchDistance + 1)) : 0;
                }
            }
            this.searchPlaces.add(searchIndex);
            lastSearchIndex = searchIndex;
        }
        GameCore.getInstance().getSave().saveProgress(Save.SEARCH_PLACES_KEY, this.searchPlaces);
    }

    public String getSearchText(int searchIndex) {
        return "search." + searchIndex;
    }

    public String getCurrentSearchText() {
        return this.getSearchText(this.getCurrentSearchPlaceIndex());
    }

    public void setSearchPlaces(ArrayList<Integer> savedList) {
        this.searchPlaces = savedList;
    }

    public void updateCurrentSearchPlace() {
        if (this.currentSearchPlaceIndex >= this.searchPlaces.size() - 1) {
            GameCore.getInstance().setGameWin();
        } else {
            this.currentSearchPlaceIndex++;
        }
        this.clearSearchedPlaces();
        GameCore.getInstance().getSave().saveProgress(Save.CURRENT_SEARCH_PLACE_KEY, this.currentSearchPlaceIndex);
    }

    public void setCurrentSearchPlaceIndex(int currentSearchPlace) {
        if (currentSearchPlace > this.searchPlaces.size() - 1) {
            currentSearchPlace = this.searchPlaces.size() - 1;
        }
        this.currentSearchPlaceIndex = currentSearchPlace;
    }

    public int getCurrentSearchPlaceIndex() {
        return this.currentSearchPlaceIndex;
    }

    public int getCurrentShowedSearchPlace() {
        if (this.searchPlaces == null)
            return 0;
        if (isSearchIndexInArray(currentSearchPlaceIndex, Config.getInstance().searchTypeIndexes)) {
            return getCurrentSearchPlace().getType();
        } else {
            return getCurrentSearchPlace().getIndex();
        }
    }

    public boolean isSearchIndexInArray(int searchPlaceIndex, int[] array) {
        for (int searchIndex : array) {
            if (searchPlaceIndex == searchIndex)
                return true;
        }
        return false;
    }

    public Place getCurrentSearchPlace() {
        if (this.searchPlaces == null)
            return this.getPlace(0);
        return this.getPlace(this.searchPlaces.get(currentSearchPlaceIndex));
    }

    public void addSearchedPlace(int placeIndex) {
        this.searchedPlaces.add(placeIndex);
        GameCore.getInstance().getSave().saveProgress(Save.SEARCHED_PLACES_KEY, this.searchedPlaces);
    }

    public void addSearchedPlace(Place place) {
        this.addSearchedPlace(place.getIndex());
    }

    public void clearSearchedPlaces() {
        this.searchedPlaces.clear();
        GameCore.getInstance().getSave().saveProgress(Save.SEARCHED_PLACES_KEY, this.searchedPlaces);
    }

    public void setSearchedPlaces(ArrayList<Integer> searchedPlaces) {
        this.searchedPlaces = searchedPlaces;
    }

    public boolean isSearchedPlace(int placeIndex) {
        return this.searchedPlaces.contains(placeIndex);
    }

    public boolean isSearchedPlace(Place place) {
        return this.isSearchedPlace(place.getIndex());
    }

    public Place getCurrentPlace() {
        return this.places[currentPlace];
    }

    public int getCurrentPlaceIndex() {
        return this.currentPlace;
    }

    public void setCurrentPlace(int place, boolean byQuest) {
        this.currentPlace = place;
        GameCore.getInstance().getSave().saveProgress(Save.CURRENT_PLACE_KEY, this.currentPlace);
        if (byQuest) {
            Dispatcher.getInstance().dispatch(Dispatcher.EVENT_CURRENT_PLACE_CHANGED_BY_QUEST);
        } else {
            Dispatcher.getInstance().dispatch(Dispatcher.EVENT_CURRENT_PLACE_CHANGED);
        }
    }

    public void setCurrentPlace(int place) {
        this.setCurrentPlace(place, false);
    }

    public void returnToLastPlace() {
        this.currentPlace = lastPlace;
        GameCore.getInstance().getSave().saveProgress(Save.CURRENT_PLACE_KEY, this.currentPlace);
        Dispatcher.getInstance().dispatch(Dispatcher.EVENT_RETURNED_TO_LAST_PLACE);
    }

    public Place getOpenPlace() {
        return this.places[openPlace];
    }

    public int getOpenPlaceIndex() {
        return this.openPlace;
    }

    public void setOpenPlace(int place) {
        this.openPlace = place;
    }

    public void setOpenPlace(Place place) {
        this.openPlace = place.getIndex();
    }

    public Place[] getPlaces() {
        return this.places;
    }

    public Place getPlace(int index) {
        if (index < this.places.length) {
            return this.places[index];
        }
        return null;
    }

    public int getMoveCost() {
        int distance = Math.abs(currentPlace - openPlace);
        return Config.getInstance().ticketPrice * distance;
    }

    public void setLastPlace(int place) {
        this.lastPlace = place;
        GameCore.getInstance().getSave().saveProgress(Save.LAST_PLACE_KEY, this.lastPlace);
    }

    public void moveToOpenPlace() {
        setLastPlace(this.currentPlace);
        this.setCurrentPlace(this.getOpenPlaceIndex());
    }

    public void incStepCount() {
        stepCount++;
        GameCore.getInstance().getSave().saveProgress(Save.STEP_COUNT, stepCount);
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getStepCount() {
        return this.stepCount;
    }
}
