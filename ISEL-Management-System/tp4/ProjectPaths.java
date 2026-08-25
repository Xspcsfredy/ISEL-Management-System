package tps.tp4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Classe utilitaria para localizar a raiz do projeto e os ficheiros XML/DTD
public final class ProjectPaths {

    private ProjectPaths() {
    }

    public static Path resolveProjectBase() {
        Path atual = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

        for (int i = 0; i < 8; i++) {
            Path xml = atual.resolve(Paths.get("xml", "isel.xml"));
            if (Files.exists(xml)) {
                return atual;
            }

            Path parent = atual.getParent();
            if (parent == null) {
                break;
            }
            atual = parent;
        }

        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    public static Path resolveXmlPath() {
        return resolveProjectBase().resolve(Paths.get("xml", "isel.xml"));
    }

    public static Path resolveDtdPath() {
        return resolveProjectBase().resolve(Paths.get("xml", "isel.dtd"));
    }
}

