package ru.profi1c.engine.app.ui;

import java.io.Serializable;

/**
 * Interface for rich context menu item
 */
public interface IActionItem extends Serializable {
    /**
     * The unique ID of this item
     */
    long getId();

    /**
     * resource id of title
     */
    int getTitleResId();

    /**
     * resource id of icon
     */
    int getIconResId();
}
