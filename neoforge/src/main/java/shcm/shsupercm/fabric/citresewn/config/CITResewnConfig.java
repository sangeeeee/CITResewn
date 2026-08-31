package shcm.shsupercm.fabric.citresewn.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.IOUtils;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Path;

/** JSON-backed client configuration stored in NeoForge's config directory. */
public class CITResewnConfig {
    public boolean enabled = true;
    public boolean mute_errors = false;
    public boolean mute_warns = false;
    public int cache_ms = 50;
    public boolean broken_paths = false;

    private static final File FILE = Platform.getConfigDir().resolve("citresewn.json").toFile();
    public static final CITResewnConfig INSTANCE = read();

    public static Path file() {
        return FILE.toPath();
    }

    public static CITResewnConfig read() {
        if (!FILE.exists())
            return new CITResewnConfig().write();

        Reader reader = null;
        try {
            reader = new FileReader(FILE);
            CITResewnConfig config = new Gson().fromJson(reader, CITResewnConfig.class);
            return config == null ? new CITResewnConfig() : config;
        } catch (Exception exception) {
            throw new RuntimeException("Could not read " + FILE, exception);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public CITResewnConfig write() {
        Gson gson = new Gson();
        JsonWriter writer = null;
        try {
            File parent = FILE.getParentFile();
            if (parent != null)
                parent.mkdirs();

            writer = gson.newJsonWriter(new FileWriter(FILE));
            writer.setIndent("    ");
            gson.toJson(gson.toJsonTree(this, CITResewnConfig.class), writer);
            return this;
        } catch (Exception exception) {
            CITResewn.LOG.error("Could not save " + FILE, exception);
            throw new RuntimeException(exception);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
