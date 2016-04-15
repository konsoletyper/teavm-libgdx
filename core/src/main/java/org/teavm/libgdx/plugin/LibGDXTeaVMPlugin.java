package org.teavm.libgdx.plugin;

import org.teavm.jso.impl.JSOPlugin;
import org.teavm.vm.spi.Before;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

@Before(JSOPlugin.class)
public class LibGDXTeaVMPlugin implements TeaVMPlugin {
    @Override
    public void install(TeaVMHost host) {
        host.add(new OverlayTransformer());
        host.add(new AssetsCopier());
    }
}
