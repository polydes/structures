package com.polydes.datastruct.ui.object;

import javax.swing.*;

public interface ObjectEditor
{
    void dispose();
    void saveChanges();
    void revertChanges();

    default JPanel getView()
    {
        return (JPanel) this;
    }
}
