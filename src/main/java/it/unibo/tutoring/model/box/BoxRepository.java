package it.unibo.tutoring.model.box;

import java.util.ArrayList;
import java.util.List;

public final class BoxRepository {

    private static final List<BoxTutoraggio>
        BOXES = new ArrayList<>();

    private BoxRepository() {
    }

    public static void addBox(
        final BoxTutoraggio box
    ) {

        BOXES.add(box);
    }

    public static List<BoxTutoraggio>
        getAllBoxes() {

        return new ArrayList<>(BOXES);
    }
}