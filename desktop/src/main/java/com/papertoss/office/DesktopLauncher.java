package com.papertoss.office;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public final class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Paper Toss Office");
        config.setWindowedMode(800, 600);
        config.setResizable(false);
        config.useVsync(true);
        new Lwjgl3Application(new PaperTossOffice(), config);
    }
}
