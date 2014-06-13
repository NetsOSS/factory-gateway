package eu.nets.factory.gateway.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class GatewayDataJsGenerator {
    public static void main(String[] args) throws Exception {
        try (GeneratorWriter writer = getWriter(false)) {
            new eu.nets.factory.gateway.web.DataJsGenerator("GatewayData", "GatewayData", MiscController.class).
                    withDataMethodFilter(dataMethod -> dataMethod.url.startsWith("/data")).
                    generateJs(writer);
        }
    }

    @Test
    public void generate() throws Exception {
        main(new String[0]);
    }

    private static GeneratorWriter getWriter(boolean silent) throws IOException {
        Class<GatewayDataJsGenerator> c = GatewayDataJsGenerator.class;

        File dir = findModuleDirectory(c);
        File file = new File(dir, "src/main/webapp/resources/js/Data.js");
        return new GeneratorWriter(silent, file);
    }

    public static File findModuleDirectory(Class klass) throws IOException {
        URL url = klass.getResource("/" + klass.getCanonicalName().replace('.', '/') + ".class");

        File f = new File(url.getPath());

        do {
            f = f.getParentFile();

            if (new File(f, "pom.xml").canRead()) {
                return f;
            }
        } while (f != null);

        throw new IOException("Could not find module directory of " + klass.getCanonicalName());
    }
}
