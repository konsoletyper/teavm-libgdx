package org.teavm.libgdx.controllers.emu;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.ControllerManager;
import com.badlogic.gdx.utils.ObjectMap;
import org.teavm.libgdx.controllers.TeaVMControllers;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public class ControllersEmulator {
    static final ObjectMap<Application, ControllerManager> managers = new ObjectMap<>();

    @SuppressWarnings("unused")
    static private void initialize() {
        managers.put(Gdx.app, new TeaVMControllers());
    }
}
