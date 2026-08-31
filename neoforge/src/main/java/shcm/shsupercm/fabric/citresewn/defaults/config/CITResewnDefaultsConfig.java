package shcm.shsupercm.fabric.citresewn.defaults.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.IOUtils;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

/** JSON-backed defaults-module configuration in NeoForge's config directory. */
public final class CITResewnDefaultsConfig {
    public float type_enchantment_scroll_multiplier = 1f;

    private static final File FILE = Platform.getConfigDir().resolve("citresewn-defaults.json").toFile();
    public static final CITResewnDefaultsConfig INSTANCE = read();

    public static CITResewnDefaultsConfig read() {
        if (!FILE.exists())
            return new CITResewnDefaultsConfig().write();

        Reader reader = null;
        try {
            reader = new FileReader(FILE);
            CITResewnDefaultsConfig config = new Gson().fromJson(reader, CITResewnDefaultsConfig.class);
            return config == null ? new CITResewnDefaultsConfig() : config;
        } catch (Exception exception) {
            throw new RuntimeException("Could not read " + FILE, exception);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public CITResewnDefaultsConfig write() {
        Gson gson = new Gson();
        JsonWriter writer = null;
        try {
            File parent = FILE.getParentFile();
            if (parent != null)
                parent.mkdirs();

            writer = gson.newJsonWriter(new FileWriter(FILE));
            writer.setIndent("    ");
            gson.toJson(gson.toJsonTree(this, CITResewnDefaultsConfig.class), writer);
            return this;
        } catch (Exception exception) {
            CITResewn.LOG.error("Could not save " + FILE, exception);
            throw new RuntimeException(exception);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
