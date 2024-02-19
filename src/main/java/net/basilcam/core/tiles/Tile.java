package net.basilcam.core.tiles;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import net.basilcam.core.Direction;

import java.util.*;

public class Tile {
    private final int id;
    private TileSection topSection;
    private TileSection leftSection;
    private TileSection bottomSection;
    private TileSection rightSection;
    private final ImmutableList<TileSection> centerSections;
    private final boolean hasCoatOfArms;
    private int clockWiseRotationCount;

    private Tile(int id,
                 TileSection topSection,
                 TileSection leftSection,
                 TileSection bottomSection,
                 TileSection rightSection,
                 ImmutableList<TileSection> centerSections,
                 boolean hasCoatOfArms) {
        this.id = id;
        this.topSection = topSection;
        this.leftSection = leftSection;
        this.bottomSection = bottomSection;
        this.rightSection = rightSection;
        this.centerSections = centerSections;
        this.hasCoatOfArms = hasCoatOfArms;
        this.clockWiseRotationCount = 0;
    }

    public int getId() {
        return id;
    }

    public TileSection getSection(Direction direction) {
        switch (direction) {
            case UP:
                return getTopSection();
            case LEFT:
                return getLeftSection();
            case DOWN:
                return getBottomSection();
            case RIGHT:
                return getRightSection();
            default:
                throw new IllegalStateException();
        }
    }

    public Multimap<TileSectionLocation, TileSection> getSections() {
        Multimap<TileSectionLocation, TileSection> sections = ArrayListMultimap.create();
        sections.put(TileSectionLocation.TOP, getTopSection());
        sections.put(TileSectionLocation.LEFT, getLeftSection());
        sections.put(TileSectionLocation.BOTTOM, getBottomSection());
        sections.put(TileSectionLocation.RIGHT, getRightSection());
        getCenterSections().forEach(section -> sections.put(TileSectionLocation.CENTER, section));
        return sections;
    }

    public TileSection getTopSection() {
        return topSection;
    }

    public TileSection getLeftSection() {
        return leftSection;
    }

    public TileSection getBottomSection() {
        return bottomSection;
    }

    public TileSection getRightSection() {
        return rightSection;
    }

    public ImmutableList<TileSection> getCenterSections() {
        return this.centerSections;
    }

    public boolean hasCoatOfArms() {
        return this.hasCoatOfArms;
    }

    public void rotateClockwise() {
        TileSection tempTop = this.topSection;
        TileSection tempLeft = this.leftSection;
        TileSection tempBottom = this.bottomSection;
        TileSection tempRight = this.rightSection;

        this.rightSection = tempTop;
        this.bottomSection = tempRight;
        this.leftSection = tempBottom;
        this.topSection = tempLeft;

        this.clockWiseRotationCount = this.clockWiseRotationCount == 3
                ? 0
                : this.clockWiseRotationCount + 1;
    }

    public int getClockWiseRotationCount() {
        return this.clockWiseRotationCount;
    }

    public static class Builder {
        private int id;
        private TileSection topSection;
        private TileSection leftSection;
        private TileSection bottomSection;
        private TileSection rightSection;
        private final List<TileSection> centerSections;
        private boolean hasCoatOfArms;

        Builder(int id) {
            this.id = id;
            this.centerSections = new ArrayList<>();
        }

        Builder withTop(TileSection section) {
            this.topSection = section;
            return this;
        }

        Builder withLeft(TileSection section) {
            this.leftSection = section;
            return this;
        }

        Builder withBottom(TileSection section) {
            this.bottomSection = section;
            return this;
        }

        Builder withRight(TileSection section) {
            this.rightSection = section;
            return this;
        }

        Builder addCenter(TileSection section) {
            this.centerSections.add(section);
            return this;
        }

        Builder withCoatOfArms(boolean hasCoatOfArms) {
            this.hasCoatOfArms = hasCoatOfArms;
            return this;
        }

        Tile build() {
            return new Tile(this.id,
                    this.topSection,
                    this.leftSection,
                    this.bottomSection,
                    this.rightSection,
                    ImmutableList.copyOf(this.centerSections),
                    hasCoatOfArms);
        }

    }
}
