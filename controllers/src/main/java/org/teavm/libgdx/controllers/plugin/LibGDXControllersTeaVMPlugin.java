package org.teavm.libgdx.controllers.plugin;

import org.teavm.jso.impl.JSOPlugin;
import org.teavm.vm.spi.After;
import org.teavm.vm.spi.Before;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

@Before(JSOPlugin.class)
public class LibGDXControllersTeaVMPlugin implements TeaVMPlugin {
    @Override
    public void install(TeaVMHost host) {
        host.add(new OverlayTransformer());
    }
}
