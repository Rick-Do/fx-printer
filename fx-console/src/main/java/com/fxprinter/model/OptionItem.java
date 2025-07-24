package com.fxprinter.model;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author dongyu
 * @version 1.0
 * @date 2025-07-23 15:57:46
 * @since jdk1.8
 */
public class OptionItem {

    private final StringProperty value = new SimpleStringProperty();
    private final BooleanProperty disabled = new SimpleBooleanProperty(false);
    private final StringProperty label = new SimpleStringProperty();

    public OptionItem() {
    }

    public OptionItem(String value) {
        this.value.set(value);
    }

    public OptionItem(String value, String label) {
        this.value.set(value);
        this.label.set(label);
    }

    public OptionItem(String value, boolean disabled) {
        this.value.set(value);
        this.disabled.set(disabled);
    }

    public String getValue() {
        return value.get();
    }

    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public boolean isDisabled() {
        return disabled.get();
    }

    public BooleanProperty disabledProperty() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled.set(disabled);
    }

    @Override
    public String toString() {
        return getValue() + " " + getLabel();
    }

}
